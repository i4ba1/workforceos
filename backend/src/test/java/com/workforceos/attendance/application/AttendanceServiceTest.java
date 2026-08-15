package com.workforceos.attendance.application;

import com.workforceos.attendance.domain.AttendanceCalculator;
import com.workforceos.attendance.domain.AttendanceDataSource;
import com.workforceos.attendance.domain.AttendanceException;
import com.workforceos.attendance.domain.AttendancePolicyParameters;
import com.workforceos.attendance.domain.AttendanceRecord;
import com.workforceos.attendance.domain.AttendanceRecordStore;
import com.workforceos.attendance.domain.AttendanceStatus;
import com.workforceos.attendance.domain.ClockEventKind;
import com.workforceos.attendance.domain.ClockEventReadModel;
import com.workforceos.attendance.domain.ExceptionFinding;
import com.workforceos.attendance.domain.PlannedShift;
import com.workforceos.attendance.domain.SimpleOvertimePolicy;
import com.workforceos.attendance.domain.StandardEventPairingPolicy;
import com.workforceos.attendance.domain.rule.AbsenceRule;
import com.workforceos.attendance.domain.rule.BreakViolationRule;
import com.workforceos.attendance.domain.rule.EarlyLeaveRule;
import com.workforceos.attendance.domain.rule.LateArrivalRule;
import com.workforceos.attendance.domain.rule.MissingClockInRule;
import com.workforceos.attendance.domain.rule.MissingClockOutRule;
import com.workforceos.attendance.domain.rule.OvertimeRule;
import com.workforceos.attendance.domain.rule.UnscheduledWorkRule;
import com.workforceos.shared.id.AttendanceRecordId;
import com.workforceos.shared.id.EmployeeId;
import com.workforceos.shared.id.ScheduleEntryId;
import com.workforceos.shared.id.TenantId;
import com.workforceos.shared.time.BusinessDate;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

class AttendanceServiceTest {

    static class FakeDataSource implements AttendanceDataSource {
        PlannedShift shift;
        List<ClockEventReadModel> events = List.of();

        @Override
        public Optional<PlannedShift> findShift(TenantId tenantId, EmployeeId employeeId, BusinessDate businessDate) {
            return Optional.ofNullable(shift);
        }

        @Override
        public List<ClockEventReadModel> findEvents(TenantId tenantId, EmployeeId employeeId, Instant from, Instant to) {
            return events;
        }

        @Override
        public ZoneId tenantDefaultZone(TenantId tenantId) {
            return ZoneId.of("Asia/Jakarta");
        }
    }

    static class FakeStore implements AttendanceRecordStore {
        final Map<AttendanceRecordId, AttendanceRecord> records = new LinkedHashMap<>();
        final Map<AttendanceRecordId, List<ExceptionFinding>> findings = new LinkedHashMap<>();

        @Override
        public Optional<AttendanceRecord> find(TenantId tenantId, EmployeeId employeeId, BusinessDate businessDate) {
            return records.values().stream()
                    .filter(r -> r.employeeId().equals(employeeId) && r.businessDate().equals(businessDate))
                    .findFirst();
        }

        @Override
        public Optional<AttendanceRecord> findById(TenantId tenantId, AttendanceRecordId id) {
            return Optional.ofNullable(records.get(id));
        }

        @Override
        public List<AttendanceRecord> findByTenant(TenantId tenantId) {
            return List.copyOf(records.values());
        }

        @Override
        public AttendanceRecord save(AttendanceRecord record) {
            records.put(record.id(), record);
            return record;
        }

        @Override
        public List<AttendanceException> findExceptions(TenantId tenantId, AttendanceRecordId recordId) {
            return List.of();
        }

        @Override
        public void replaceExceptions(TenantId tenantId, AttendanceRecordId recordId, List<ExceptionFinding> findings) {
            this.findings.put(recordId, findings);
        }
    }

    private static final TenantId TENANT = TenantId.newId();
    private static final EmployeeId EMPLOYEE = EmployeeId.newId();
    private static final ZoneId JAKARTA = ZoneId.of("Asia/Jakarta");
    private static final BusinessDate DATE = BusinessDate.of(2026, 8, 14);

    @Test
    void recalculate_lateArrival_persistsRecordAndException() {
        Instant shiftStart = Instant.parse("2026-08-14T01:00:00Z");
        Instant shiftEnd = Instant.parse("2026-08-14T09:00:00Z");
        FakeDataSource dataSource = new FakeDataSource();
        dataSource.shift = new PlannedShift(ScheduleEntryId.newId(), JAKARTA, shiftStart, shiftEnd, DATE);
        dataSource.events = List.of(
                new ClockEventReadModel(Instant.parse("2026-08-14T01:20:00Z"), ClockEventKind.CLOCK_IN, JAKARTA),
                new ClockEventReadModel(shiftEnd, ClockEventKind.CLOCK_OUT, JAKARTA));

        FakeStore store = new FakeStore();
        List<Object> published = new ArrayList<>();
        AttendanceCalculator calculator = new AttendanceCalculator(new StandardEventPairingPolicy(),
                new SimpleOvertimePolicy(), List.of(new LateArrivalRule(), new EarlyLeaveRule(), new MissingClockInRule(),
                new MissingClockOutRule(), new AbsenceRule(), new OvertimeRule(), new UnscheduledWorkRule(),
                new BreakViolationRule()));
        AttendanceService service = new AttendanceService(dataSource, calculator, store, published::add);

        AttendanceRecord record = service.recalculate(TENANT, EMPLOYEE, DATE);

        assertThat(record.status()).isEqualTo(AttendanceStatus.LATE);
        assertThat(store.records).hasSize(1);
        assertThat(store.findings.get(record.id())).extracting(ExceptionFinding::type)
                .containsExactly(com.workforceos.attendance.domain.ExceptionType.LATE);
        assertThat(published).isNotEmpty();
    }
}
