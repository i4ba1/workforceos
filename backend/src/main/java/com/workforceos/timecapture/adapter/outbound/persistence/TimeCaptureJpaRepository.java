package com.workforceos.timecapture.adapter.outbound.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

interface TimeEventJpaRepository extends JpaRepository<TimeEventJpaEntity, UUID> {

    Optional<TimeEventJpaEntity> findByTenantIdAndId(UUID tenantId, UUID id);

    Optional<TimeEventJpaEntity> findByTenantIdAndSourceAndSourceEventId(UUID tenantId, String source, String sourceEventId);

    List<TimeEventJpaEntity> findAllByTenantIdAndEmployeeIdAndOccurredAtBetweenOrderByOccurredAtAsc(
            UUID tenantId, UUID employeeId, Instant from, Instant to);
}

interface IngestionRequestJpaRepository extends JpaRepository<IngestionRequestJpaEntity, UUID> {

    Optional<IngestionRequestJpaEntity> findByTenantIdAndIdempotencyKey(UUID tenantId, String idempotencyKey);
}
