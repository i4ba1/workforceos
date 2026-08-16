package com.workforceos.payroll.application;

import com.workforceos.audit.domain.AuditEvent;
import com.workforceos.audit.domain.AuditWriter;
import com.workforceos.payroll.domain.CsvPayrollExporter;
import com.workforceos.payroll.domain.PayPeriod;
import com.workforceos.payroll.domain.PayPeriodState;
import com.workforceos.payroll.domain.PayrollAttendanceLine;
import com.workforceos.payroll.domain.PayrollDataSource;
import com.workforceos.payroll.domain.PayrollExport;
import com.workforceos.payroll.domain.PayrollStore;
import com.workforceos.shared.error.ConflictException;
import com.workforceos.shared.id.EmployeeId;
import com.workforceos.shared.id.PayPeriodId;
import com.workforceos.shared.id.TenantId;
import com.workforceos.shared.id.UserId;
import com.workforceos.shared.time.Minutes;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class PayrollServiceTest {

    static class FakePayrollStore implements PayrollStore {
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
                    .max(java.util.Comparator.comparingInt(PayrollExport::version));
        }

        @Override
        public List<PayrollExport> findExports(TenantId tenantId, PayPeriodId periodId) {
            return exports.stream().filter(e -> e.periodId().equals(periodId)).toList();
        }

        @Override
        public PayrollExport saveExport(PayrollExport export) {
            exports.add(export);
            return export;
        }
    }

    static class FakeDataSource implements PayrollDataSource {
        List<PayrollAttendanceLine> lines = List.of();

        @Override
        public List<PayrollAttendanceLine> findAttendance(TenantId tenantId, LocalDate from, LocalDate to) {
            return lines;
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
    private static final UserId PAYROLL_ADMIN = UserId.newId();
    private static final LocalDate START = LocalDate.of(2026, 8, 1);
    private static final LocalDate END = LocalDate.of(2026, 8, 31);

    private final FakePayrollStore store = new FakePayrollStore();
    private final FakeDataSource dataSource = new FakeDataSource();
    private final RecordingAuditWriter audit = new RecordingAuditWriter();
    private final List<Object> published = new ArrayList<>();
    private final PayrollService service = new PayrollService(store, dataSource, new CsvPayrollExporter(), audit, published::add);

    private PayPeriod openPeriod() {
        return service.open(TENANT, START, END);
    }

    @Test
    void open_createsOpenPeriod() {
        PayPeriod period = openPeriod();
        assertThat(period.state()).isEqualTo(PayPeriodState.OPEN);
        assertThat(period.version()).isZero();
    }

    @Test
    void close_withUnresolvedRecords_throwsConflict() {
        PayPeriod period = openPeriod();
        dataSource.lines = List.of(
                new PayrollAttendanceLine(EmployeeId.newId(), Minutes.of(480), Minutes.ZERO, true));

        assertThatThrownBy(() -> service.close(TENANT, period.id(), PAYROLL_ADMIN))
                .isInstanceOf(ConflictException.class)
                .hasMessageContaining("unresolved");
        assertThat(store.periods.get(period.id()).state()).isEqualTo(PayPeriodState.OPEN);
    }

    @Test
    void close_withoutUnresolvedRecords_closesAndBumpsVersion() {
        PayPeriod period = openPeriod();
        dataSource.lines = List.of(
                new PayrollAttendanceLine(EmployeeId.newId(), Minutes.of(480), Minutes.ZERO, false));

        PayPeriod closed = service.close(TENANT, period.id(), PAYROLL_ADMIN);

        assertThat(closed.state()).isEqualTo(PayPeriodState.CLOSED);
        assertThat(closed.version()).isEqualTo(1);
        assertThat(closed.closedBy()).isEqualTo(PAYROLL_ADMIN);
        assertThat(audit.events).isNotEmpty();
    }

    @Test
    void export_beforeClose_throwsConflict() {
        PayPeriod period = openPeriod();

        assertThatThrownBy(() -> service.export(TENANT, period.id(), PAYROLL_ADMIN))
                .isInstanceOf(ConflictException.class)
                .hasMessageContaining("closed");
    }

    @Test
    void export_afterClose_isDeterministicAndVersioned() {
        PayPeriod period = openPeriod();
        dataSource.lines = List.of(
                new PayrollAttendanceLine(EmployeeId.newId(), Minutes.of(480), Minutes.of(120), false));
        service.close(TENANT, period.id(), PAYROLL_ADMIN);

        PayrollExport first = service.export(TENANT, period.id(), PAYROLL_ADMIN);
        PayrollExport second = service.export(TENANT, period.id(), PAYROLL_ADMIN);

        assertThat(first.version()).isEqualTo(1);
        assertThat(second.version()).isEqualTo(2);
        assertThat(first.checksum()).isEqualTo(second.checksum());
        assertThat(store.exports).hasSize(2);
    }

    @Test
    void reopen_afterClose_reopensPeriod() {
        PayPeriod period = openPeriod();
        dataSource.lines = List.of();
        service.close(TENANT, period.id(), PAYROLL_ADMIN);

        PayPeriod reopened = service.reopen(TENANT, period.id(), PAYROLL_ADMIN, "Payroll correction");

        assertThat(reopened.state()).isEqualTo(PayPeriodState.REOPENED);
        assertThat(audit.events.stream().anyMatch(e -> e.action().equals("payroll.period_reopened"))).isTrue();
    }
}
