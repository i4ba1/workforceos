package com.workforceos.attendance.adapter.outbound.persistence;

import com.workforceos.attendance.domain.AttendanceException;
import com.workforceos.attendance.domain.AttendanceRecord;
import com.workforceos.attendance.domain.AttendanceRecordStore;
import com.workforceos.attendance.domain.AttendanceStatus;
import com.workforceos.attendance.domain.ExceptionFinding;
import com.workforceos.attendance.domain.ExceptionSeverity;
import com.workforceos.attendance.domain.ExceptionState;
import com.workforceos.attendance.domain.ExceptionType;
import com.workforceos.shared.id.AttendanceRecordId;
import com.workforceos.shared.id.EmployeeId;
import com.workforceos.shared.id.PolicyVersionId;
import com.workforceos.shared.id.ScheduleEntryId;
import com.workforceos.shared.id.TenantId;
import com.workforceos.shared.time.BusinessDate;
import com.workforceos.shared.time.Minutes;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/** Maps between attendance aggregates and their JPA representations. */
@Repository
public class AttendanceRecordPersistenceAdapter implements AttendanceRecordStore {

    private final AttendanceRecordJpaRepository recordRepository;
    private final AttendanceExceptionJpaRepository exceptionRepository;

    public AttendanceRecordPersistenceAdapter(AttendanceRecordJpaRepository recordRepository,
                                              AttendanceExceptionJpaRepository exceptionRepository) {
        this.recordRepository = recordRepository;
        this.exceptionRepository = exceptionRepository;
    }

    @Override
    public Optional<AttendanceRecord> find(TenantId tenantId, EmployeeId employeeId, BusinessDate businessDate) {
        return recordRepository.findByTenantIdAndEmployeeIdAndBusinessDate(tenantId.value(), employeeId.value(),
                businessDate.value()).map(this::toDomain);
    }

    @Override
    public Optional<AttendanceRecord> findById(TenantId tenantId, AttendanceRecordId id) {
        return recordRepository.findByTenantIdAndId(tenantId.value(), id.value()).map(this::toDomain);
    }

    @Override
    public List<AttendanceRecord> findByTenant(TenantId tenantId) {
        return recordRepository.findAllByTenantIdOrderByBusinessDateDesc(tenantId.value()).stream()
                .map(this::toDomain).toList();
    }

    @Override
    public AttendanceRecord save(AttendanceRecord record) {
        AttendanceRecordJpaEntity entity = new AttendanceRecordJpaEntity(
                record.id().value(),
                record.tenantId().value(),
                record.employeeId().value(),
                record.businessDate().value(),
                record.scheduleEntryId() == null ? null : record.scheduleEntryId().value(),
                record.policyVersionId() == null ? null : record.policyVersionId().value(),
                record.status().name(),
                record.regularMinutes().value(),
                record.overtimeMinutes().value(),
                record.breakMinutes().value(),
                record.version());
        return toDomain(recordRepository.save(entity));
    }

    @Override
    public List<AttendanceException> findExceptions(TenantId tenantId, AttendanceRecordId recordId) {
        return exceptionRepository.findAllByTenantIdAndRecordId(tenantId.value(), recordId.value()).stream()
                .map(this::toDomain).toList();
    }

    @Override
    public void replaceExceptions(TenantId tenantId, AttendanceRecordId recordId, List<ExceptionFinding> findings) {
        exceptionRepository.deleteAllByRecordId(recordId.value());
        for (ExceptionFinding finding : findings) {
            exceptionRepository.save(new AttendanceExceptionJpaEntity(
                    UUID.randomUUID(),
                    tenantId.value(),
                    recordId.value(),
                    finding.type().name(),
                    finding.severity().name(),
                    ExceptionState.OPEN.name(),
                    finding.detail(),
                    Instant.now()));
        }
    }

    private AttendanceRecord toDomain(AttendanceRecordJpaEntity entity) {
        return new AttendanceRecord(
                new AttendanceRecordId(entity.getId()),
                new TenantId(entity.getTenantId()),
                new EmployeeId(entity.getEmployeeId()),
                BusinessDate.of(entity.getBusinessDate()),
                entity.getScheduleEntryId() == null ? null : new ScheduleEntryId(entity.getScheduleEntryId()),
                entity.getPolicyVersionId() == null ? null : new PolicyVersionId(entity.getPolicyVersionId()),
                AttendanceStatus.valueOf(entity.getStatus()),
                Minutes.of(entity.getRegularMinutes()),
                Minutes.of(entity.getOvertimeMinutes()),
                Minutes.of(entity.getBreakMinutes()),
                entity.getVersion());
    }

    private AttendanceException toDomain(AttendanceExceptionJpaEntity entity) {
        return new AttendanceException(
                new AttendanceRecordId(entity.getRecordId()),
                ExceptionType.valueOf(entity.getType()),
                ExceptionSeverity.valueOf(entity.getSeverity()),
                entity.getDetail(),
                entity.getCreatedAt(),
                ExceptionState.valueOf(entity.getState()));
    }
}
