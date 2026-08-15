package com.workforceos.audit.adapter.outbound.persistence;

import com.workforceos.audit.domain.AuditEvent;
import com.workforceos.audit.domain.AuditReader;
import com.workforceos.audit.domain.AuditWriter;
import com.workforceos.shared.id.AuditEventId;
import com.workforceos.shared.id.TenantId;
import com.workforceos.shared.id.UserId;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

/** Maps between audit events and their JPA representation. */
@Repository
public class AuditPersistenceAdapter implements AuditWriter, AuditReader {

    private final AuditEventJpaRepository repository;

    public AuditPersistenceAdapter(AuditEventJpaRepository repository) {
        this.repository = repository;
    }

    @Override
    public void append(AuditEvent event) {
        repository.save(new AuditEventJpaEntity(
                event.id().value(),
                event.tenantId().value(),
                event.actorId().value(),
                event.action(),
                event.entityType(),
                event.entityId(),
                event.beforeDigest(),
                event.afterDigest(),
                event.correlationId(),
                event.occurredAt()));
    }

    @Override
    public List<AuditEvent> query(TenantId tenantId, String entityType, UUID entityId, UserId actorId,
                                  Instant from, Instant to) {
        return repository.search(tenantId.value(), entityType, entityId,
                        actorId == null ? null : actorId.value(), from, to)
                .stream().map(this::toDomain).toList();
    }

    private AuditEvent toDomain(AuditEventJpaEntity entity) {
        return new AuditEvent(
                new AuditEventId(entity.getId()),
                new TenantId(entity.getTenantId()),
                new UserId(entity.getActorId()),
                entity.getAction(),
                entity.getEntityType(),
                entity.getEntityId(),
                entity.getBeforeDigest(),
                entity.getAfterDigest(),
                entity.getCorrelationId(),
                entity.getOccurredAt());
    }
}
