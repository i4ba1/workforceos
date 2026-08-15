package com.workforceos.attendance.adapter.outbound.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

interface AttendanceRecordJpaRepository extends JpaRepository<AttendanceRecordJpaEntity, UUID> {

    Optional<AttendanceRecordJpaEntity> findByTenantIdAndEmployeeIdAndBusinessDate(
            UUID tenantId, UUID employeeId, LocalDate businessDate);

    Optional<AttendanceRecordJpaEntity> findByTenantIdAndId(UUID tenantId, UUID id);

    List<AttendanceRecordJpaEntity> findAllByTenantIdOrderByBusinessDateDesc(UUID tenantId);
}

interface AttendanceExceptionJpaRepository extends JpaRepository<AttendanceExceptionJpaEntity, UUID> {

    List<AttendanceExceptionJpaEntity> findAllByTenantIdAndRecordId(UUID tenantId, UUID recordId);

    void deleteAllByRecordId(UUID recordId);
}
