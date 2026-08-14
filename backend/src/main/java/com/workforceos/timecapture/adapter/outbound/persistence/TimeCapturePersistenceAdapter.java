package com.workforceos.timecapture.adapter.outbound.persistence;

import com.workforceos.shared.id.EmployeeId;
import com.workforceos.shared.id.TenantId;
import com.workforceos.shared.id.TimeEventId;
import com.workforceos.timecapture.domain.EventSource;
import com.workforceos.timecapture.domain.IngestionRecord;
import com.workforceos.timecapture.domain.TimeEvent;
import com.workforceos.timecapture.domain.TimeEventAppender;
import com.workforceos.timecapture.domain.TimeEventReader;
import com.workforceos.timecapture.domain.TimeEventType;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.time.ZoneId;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/** Maps between time-capture aggregates and their JPA representations. */
@Repository
public class TimeCapturePersistenceAdapter implements TimeEventReader, TimeEventAppender {

    private final TimeEventJpaRepository timeEventRepository;
    private final IngestionRequestJpaRepository ingestionRepository;

    public TimeCapturePersistenceAdapter(TimeEventJpaRepository timeEventRepository,
                                         IngestionRequestJpaRepository ingestionRepository) {
        this.timeEventRepository = timeEventRepository;
        this.ingestionRepository = ingestionRepository;
    }

    @Override
    public Optional<TimeEvent> findById(TenantId tenantId, TimeEventId id) {
        return timeEventRepository.findByTenantIdAndId(tenantId.value(), id.value()).map(this::toDomain);
    }

    @Override
    public Optional<TimeEvent> findBySource(TenantId tenantId, EventSource source) {
        return timeEventRepository
                .findByTenantIdAndSourceAndSourceEventId(tenantId.value(), source.source(), source.sourceEventId())
                .map(this::toDomain);
    }

    @Override
    public List<TimeEvent> findForEmployee(TenantId tenantId, EmployeeId employeeId, Instant from, Instant to) {
        return timeEventRepository
                .findAllByTenantIdAndEmployeeIdAndOccurredAtBetweenOrderByOccurredAtAsc(
                        tenantId.value(), employeeId.value(), from, to)
                .stream().map(this::toDomain).toList();
    }

    @Override
    public Optional<IngestionRecord> findIngestion(TenantId tenantId, String idempotencyKey) {
        return ingestionRepository.findByTenantIdAndIdempotencyKey(tenantId.value(), idempotencyKey)
                .map(e -> new IngestionRecord(new TimeEventId(e.getTimeEventId()), e.getRequestDigest()));
    }

    @Override
    public TimeEvent append(TimeEvent event) {
        TimeEventJpaEntity entity = new TimeEventJpaEntity(
                event.id().value(),
                event.tenantId().value(),
                event.employeeId().value(),
                event.type().name(),
                event.occurredAt(),
                event.receivedAt(),
                event.zoneId().getId(),
                event.source().source(),
                event.source().sourceEventId());
        return toDomain(timeEventRepository.save(entity));
    }

    @Override
    public void recordIngestion(TenantId tenantId, String idempotencyKey, TimeEventId timeEventId,
                                String requestDigest, Instant createdAt) {
        IngestionRequestJpaEntity entity = new IngestionRequestJpaEntity(
                UUID.randomUUID(), tenantId.value(), idempotencyKey, timeEventId.value(), requestDigest, createdAt);
        ingestionRepository.save(entity);
    }

    private TimeEvent toDomain(TimeEventJpaEntity entity) {
        return new TimeEvent(
                new TimeEventId(entity.getId()),
                new TenantId(entity.getTenantId()),
                new EmployeeId(entity.getEmployeeId()),
                TimeEventType.valueOf(entity.getEventType()),
                entity.getOccurredAt(),
                entity.getReceivedAt(),
                ZoneId.of(entity.getZoneId()),
                new EventSource(entity.getSource(), entity.getSourceEventId()));
    }
}
