package com.workforceos.scheduling.adapter.outbound.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

interface ShiftTemplateJpaRepository extends JpaRepository<ShiftTemplateJpaEntity, UUID> {

    List<ShiftTemplateJpaEntity> findAllByTenantId(UUID tenantId);

    Optional<ShiftTemplateJpaEntity> findByTenantIdAndId(UUID tenantId, UUID id);
}

interface ScheduleEntryJpaRepository extends JpaRepository<ScheduleEntryJpaEntity, UUID> {

    List<ScheduleEntryJpaEntity> findAllByTenantIdAndEmployeeIdAndBusinessDateBetween(
            UUID tenantId, UUID employeeId, LocalDate from, LocalDate to);

    @Query("""
            select e from ScheduleEntryJpaEntity e
            where e.tenantId = :tenantId
              and e.employeeId = :employeeId
              and e.plannedStart < :end
              and e.plannedEnd > :start
            """)
    List<ScheduleEntryJpaEntity> findOverlapping(
            @Param("tenantId") UUID tenantId,
            @Param("employeeId") UUID employeeId,
            @Param("start") Instant start,
            @Param("end") Instant end);
}
