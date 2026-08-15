package com.workforceos.attendance.adapter.outbound.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

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

    List<AttendanceExceptionJpaEntity> findAllByTenantIdAndStateOrderByCreatedAtAsc(UUID tenantId, String state);

    void deleteAllByRecordId(UUID recordId);

    @Modifying
    @Query("update AttendanceExceptionJpaEntity e set e.state = :state where e.recordId = :recordId and e.state = 'OPEN'")
    int updateStateByRecordId(@Param("state") String state, @Param("recordId") UUID recordId);
}
