package com.workforceos.attendance.adapter.outbound.persistence.readmodel;

import org.springframework.data.jpa.repository.JpaRepository;

import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

interface ScheduleEntryReadRepository extends JpaRepository<ScheduleEntryReadEntity, UUID> {

    Optional<ScheduleEntryReadEntity> findFirstByTenantIdAndEmployeeIdAndBusinessDateOrderByPlannedStartAsc(
            UUID tenantId, UUID employeeId, LocalDate businessDate);
}

interface TimeEventReadRepository extends JpaRepository<TimeEventReadEntity, UUID> {

    List<TimeEventReadEntity> findAllByTenantIdAndEmployeeIdAndOccurredAtBetweenOrderByOccurredAtAsc(
            UUID tenantId, UUID employeeId, Instant from, Instant to);
}

interface TenantReadRepository extends JpaRepository<TenantReadEntity, UUID> {
}
