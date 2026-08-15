package com.workforceos.payroll.application;

import com.workforceos.audit.domain.AuditEvent;
import com.workforceos.audit.domain.AuditWriter;
import com.workforceos.payroll.adapter.outbound.export.CsvPayrollExporter;
import com.workforceos.payroll.domain.PayPeriod;
import com.workforceos.payroll.domain.PayPeriodState;
import com.workforceos.payroll.domain.PayrollDataSource;
import com.workforceos.payroll.domain.PayrollExport;
import com.workforceos.payroll.domain.PayrollProjection;
import com.workforceos.payroll.domain.PayrollStore;
import com.workforceos.shared.error.ConflictException;
import com.workforceos.shared.id.EmployeeId;
import com.workforceos.shared.id.PayPeriodId;
import com.workforceos.shared.id.TenantId;
import com.workforceos.shared.id.UserId;
import com.workforceos.shared.time.Minutes;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class PayrollServiceTest {

    static class FakeStore implements PayrollStore {
        final Map<PayPeriodId, PayPeriod> periods = new LinkedHashMap<>();
        final List<PayrollExport> exports = new ArrayList<>();

        @Override
        public Optional<PayPeriod> findPeriod(TenantId tenantId, PayPeriodId id) {
            return Optional.ofNullable(periods.get(id));
        }

        @Override
        public List<PayPeriod> findPeriods(TenantId tenantId) {
            return List.copyOf(periods.values());
        }

        @Override
        public PayPeriod savePeriod(PayPeriod period) {
            periods.put(period.id(), period);
            return period;
        }

        @Override
        public Optional<PayrollExport> findLatestExport(TenantId tenantId, PayPeriodId periodId) {
            return exports.stream().filter(e -> e.periodId().equals(periodId))
                    .max(Comparator.comparingInt(PayrollExport::version));
        }

        @Override
        public PayrollExport saveExport(PayrollExport export) {
            exports.add(export);
            return export;
        }

        @Override
        public List<PayrollExport> findExports(TenantId tenantId, PayPeriodId periodId) {
            return exports.stream().filter(e -> e.periodId().equals(periodId)).toList();
        }
    }

    static class FakeDataSource implements PayrollDataSource {
        long openExceptions = 0;
        List<PayrollProjection.Line> totals = List.of();

        @Override
        public long countOpenExceptions(TenantId tenantId, LocalDate from, LocalDate to) {
            return openExceptions;
        }

        @Override
        public List<PayrollProjection.Line> findTotals(TenantId tenantId, LocalDate from, LocalDate to) {
            return totals;
        }
    }

    static class RecordingAuditWriter implements AuditWriter {
        final List<AuditEvent> events = new ArrayList<>();

        @Override
        public void append(AuditEvent event) {
            events.add(event);
        }
    }

    private static final TenantId TENANT = TenantId.newId();
    private static final UserId MANAGER = UserId.newId();
    private static final EmployeeId EMPLOYEE = EmployeeId.newId();

    private final FakeStore store = new FakeStore();
    private final FakeDataSource dataSource = new FakeDataSource();
    private final RecordingAuditWriter auditWriter = new RecordingAuditWriter();
    private final List<Object> published = new ArrayList<>();
    private final PayrollService service = new PayrollService(store, dataSource, new CsvPayrollExporter(), auditWriter, published::add);

    private PayPeriod open() {
        return service.open(TENANT, LocalDate.of(2026, 8, 1), LocalDate.of(2026, 8, 14));
    }

    @Test
    void close_withUnresolvedExceptions_throwsConflict() {
        PayPeriod period = open();
        dataSource.openExceptions = 3;

        assertThatThrownBy(() -> service.close(TENANT, period.id(), MANAGER, Instant.now()))
                .isInstanceOf(ConflictException.class)
                .hasMessageContaining("exception");
        assertThat(period.state()).isEqualTo(PayPeriodState.VALIDATING);
    }

    @Test
    void close_withoutExceptions_closesAndPublishes() {
        PayPeriod period = open();
        dataSource.openExceptions = 0;

        PayPeriod closed = service.close(TENANT, period.id(), MANAGER, Instant.now());

        assertThat(closed.state()).isEqualTo(PayPeriodState.CLOSED);
        assertThat(closed.version()).isEqualTo(1);
        assertThat(published).hasSize(1);
    }

    @Test
    void export_isDeterministicAndIdempotentForSameClose() {
        PayPeriod period = open();
        service.close(TENANT, period.id(), MANAGER, Instant.now());
        dataSource.totals = List.of(new PayrollProjection.Line(EMPLOYEE, Minutes.of(480), Minutes.of(60), Minutes.ZERO));

        PayrollExportResult first = service.export(TENANT, period.id(), MANAGER);
        PayrollExportResult second = service.export(TENANT, period.id(), MANAGER);

        assertThat(first.export().checksum()).isEqualTo(second.export().checksum());
        assertThat(first.content()).isEqualTo(second.content());
        assertThat(first.export().id()).isEqualTo(second.export().id()); // idempotent re-export
    }

    @Test
    void reopen_thenClose_bumpsVersionAndCreatesNewExport() {
        PayPeriod period = open();
        service.close(TENANT, period.id(), MANAGER, Instant.now());
        dataSource.totals = List.of(new PayrollProjection.Line(EMPLOYEE, Minutes.of(480), Minutes.of(0), Minutes.ZERO));
        PayrollExportResult first = service.export(TENANT, period.id(), MANAGER);

        service.reopen(TENANT, period.id(), MANAGER, Instant.now(), "Fix totals");
        service.close(TENANT, period.id(), MANAGER, Instant.now());
        PayrollExportResult second = service.export(TENANT, period.id(), MANAGER);

        assertThat(period.version()).isEqualTo(2);
        assertThat(second.export().version()).isEqualTo(2);
        assertThat(second.export().id()).isNotEqualTo(first.export().id());
    }

    @Test
    void export_whenNotClosed_throwsConflict() {
        PayPeriod period = open();

        assertThatThrownBy(() -> service.export(TENANT, period.id(), MANAGER))
                .isInstanceOf(ConflictException.class)
                .hasMessageContaining("closed");
    }
}
