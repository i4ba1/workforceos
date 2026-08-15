package com.workforceos.audit.adapter.outbound.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

interface AuditEventJpaRepository extends JpaRepository<AuditEventJpaEntity, UUID> {

    @Query("""
            select e from AuditEventJpaEntity e
            where e.tenantId = :tenantId
              and (:entityType is null or e.entityType = :entityType)
              and (:entityId is null or e.entityId = :entityId)
              and (:actorId is null or e.actorId = :actorId)
              and (:from is null or e.occurredAt >= :from)
              and (:to is null or e.occurredAt <= :to)
            order by e.occurredAt desc
            """)
    List<AuditEventJpaEntity> search(@Param("tenantId") UUID tenantId,
                                     @Param("entityType") String entityType,
                                     @Param("entityId") UUID entityId,
                                     @Param("actorId") UUID actorId,
                                     @Param("from") Instant from,
                                     @Param("to") Instant to);
}
