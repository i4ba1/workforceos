package com.workforceos.attendance.application;

import com.workforceos.attendance.domain.AttendanceCalculator;
import com.workforceos.attendance.domain.AttendanceDataSource;
import com.workforceos.attendance.domain.AttendanceException;
import com.workforceos.attendance.domain.AttendancePolicyParameters;
import com.workforceos.attendance.domain.AttendanceRecord;
import com.workforceos.attendance.domain.AttendanceRecordStore;
import com.workforceos.attendance.domain.CalculationInput;
import com.workforceos.attendance.domain.CalculationResult;
import com.workforceos.attendance.domain.ClockEventReadModel;
import com.workforceos.attendance.domain.EventStamp;
import com.workforceos.attendance.domain.ExceptionFinding;
import com.workforceos.attendance.domain.ExceptionState;
import com.workforceos.attendance.domain.PlannedShift;
import com.workforceos.attendance.domain.event.AttendanceRecalculated;
import com.workforceos.attendance.domain.event.ExceptionOpened;
import com.workforceos.shared.error.NotFoundException;
import com.workforceos.shared.id.AttendanceRecordId;
import com.workforceos.shared.id.EmployeeId;
import com.workforceos.shared.id.ScheduleEntryId;
import com.workforceos.shared.id.TenantId;
import com.workforceos.shared.time.BusinessDate;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.ZoneId;
import java.util.List;
import java.util.Optional;

/**
 * Recalculates and persists an employee's derived attendance for a business date.
 *
 * <p>Policy parameters are a fixed default until the policy module supplies
 * effective-dated versions in a later phase.</p>
 */
@Service
public class AttendanceService {

    private final AttendanceDataSource dataSource;
    private final AttendanceCalculator calculator;
    private final AttendanceRecordStore store;
    private final ApplicationEventPublisher eventPublisher;

    public AttendanceService(AttendanceDataSource dataSource, AttendanceCalculator calculator,
                             AttendanceRecordStore store, ApplicationEventPublisher eventPublisher) {
        this.dataSource = dataSource;
        this.calculator = calculator;
        this.store = store;
        this.eventPublisher = eventPublisher;
    }

    @Transactional
    public AttendanceRecord recalculate(TenantId tenantId, EmployeeId employeeId, BusinessDate businessDate) {
        Optional<PlannedShift> shiftOpt = dataSource.findShift(tenantId, employeeId, businessDate);
        PlannedShift shift = shiftOpt.orElse(null);

        ZoneId zone = shift != null ? shift.zoneId() : dataSource.tenantDefaultZone(tenantId);
        Instant from = shift != null
                ? shift.plannedStart()
                : businessDate.value().atStartOfDay(zone).toInstant();
        Instant to = shift != null
                ? shift.plannedEnd()
                : businessDate.value().plusDays(1).atStartOfDay(zone).toInstant();

        List<EventStamp> stamps = dataSource.findEvents(tenantId, employeeId, from, to).stream()
                .map(this::toStamp)
                .toList();

        CalculationResult result = calculator.calculate(new CalculationInput(
                employeeId, businessDate, shift, stamps, AttendancePolicyParameters.defaults(),
                false, false, false));

        ScheduleEntryId scheduleRef = shift == null ? null : shift.scheduleEntryId();
        AttendanceRecord record = store.find(tenantId, employeeId, businessDate)
                .orElseGet(() -> new AttendanceRecord(AttendanceRecordId.newId(), tenantId, employeeId,
                        businessDate, scheduleRef, null));
        record.applyTotals(result.status(), result.regularMinutes(), result.overtimeMinutes(), result.breakMinutes());
        AttendanceRecord saved = store.save(record);
        store.replaceExceptions(tenantId, saved.id(), result.findings());

        eventPublisher.publishEvent(new AttendanceRecalculated(tenantId, employeeId, saved.id(), businessDate));
        for (ExceptionFinding finding : result.findings()) {
            eventPublisher.publishEvent(new ExceptionOpened(tenantId, saved.id(), finding.type(), finding.severity()));
        }
        return saved;
    }

    @Transactional(readOnly = true)
    public List<AttendanceRecord> list(TenantId tenantId) {
        return store.findByTenant(tenantId);
    }

    @Transactional(readOnly = true)
    public AttendanceRecord get(TenantId tenantId, AttendanceRecordId id) {
        return store.findById(tenantId, id)
                .orElseThrow(() -> new NotFoundException("attendance.not_found", "Attendance record not found: " + id));
    }

    @Transactional(readOnly = true)
    public List<AttendanceException> exceptions(TenantId tenantId, AttendanceRecordId id) {
        return store.findExceptions(tenantId, id);
    }

    @Transactional(readOnly = true)
    public List<AttendanceException> openExceptions(TenantId tenantId) {
        return store.findOpenExceptions(tenantId);
    }

    @Transactional
    public void resolveExceptions(TenantId tenantId, AttendanceRecordId recordId, ExceptionState state) {
        store.updateExceptionStates(tenantId, recordId, state);
    }

    private EventStamp toStamp(ClockEventReadModel event) {
        return new EventStamp(event.occurredAt(), event.kind());
    }
}
