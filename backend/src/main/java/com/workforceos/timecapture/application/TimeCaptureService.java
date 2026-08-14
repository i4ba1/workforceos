package com.workforceos.timecapture.application;

import com.workforceos.shared.error.ConflictException;
import com.workforceos.shared.error.NotFoundException;
import com.workforceos.shared.id.EmployeeId;
import com.workforceos.shared.id.TenantId;
import com.workforceos.shared.id.TimeEventId;
import com.workforceos.timecapture.domain.IngestionRecord;
import com.workforceos.timecapture.domain.TimeEvent;
import com.workforceos.timecapture.domain.TimeEventAppender;
import com.workforceos.timecapture.domain.TimeEventReader;
import com.workforceos.timecapture.domain.event.TimeEventRecorded;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.Instant;
import java.util.HexFormat;
import java.util.List;
import java.util.Optional;

/**
 * Records immutable raw time events idempotently.
 *
 * <p>A retried submission (same idempotency key, or same source event identity) returns
 * the original event and never creates a duplicate. A retry with the same key but a
 * different payload is rejected as a conflict. The database unique constraints backstop
 * concurrent duplicates.</p>
 */
@Service
public class TimeCaptureService {

    private final TimeEventReader reader;
    private final TimeEventAppender appender;
    private final ApplicationEventPublisher eventPublisher;

    public TimeCaptureService(TimeEventReader reader, TimeEventAppender appender,
                              ApplicationEventPublisher eventPublisher) {
        this.reader = reader;
        this.appender = appender;
        this.eventPublisher = eventPublisher;
    }

    @Transactional
    public TimeEvent record(TenantId tenantId, RecordTimeEventCommand command) {
        if (command.idempotencyKey() != null) {
            Optional<IngestionRecord> existing = reader.findIngestion(tenantId, command.idempotencyKey());
            if (existing.isPresent()) {
                return resolveReplay(tenantId, existing.get(), command);
            }
        }
        if (command.source().sourceEventId() != null) {
            Optional<TimeEvent> bySource = reader.findBySource(tenantId, command.source());
            if (bySource.isPresent()) {
                return bySource.get();
            }
        }

        TimeEvent event = new TimeEvent(
                TimeEventId.newId(),
                tenantId,
                command.employeeId(),
                command.type(),
                command.occurredAt(),
                command.receivedAt(),
                command.zoneId(),
                command.source());
        TimeEvent saved = appender.append(event);

        if (command.idempotencyKey() != null) {
            appender.recordIngestion(tenantId, command.idempotencyKey(), saved.id(), digestOf(command), command.receivedAt());
        }

        eventPublisher.publishEvent(new TimeEventRecorded(
                tenantId, saved.employeeId(), saved.id(), saved.type(), saved.occurredAt()));
        return saved;
    }

    @Transactional(readOnly = true)
    public List<TimeEvent> timeline(TenantId tenantId, EmployeeId employeeId, Instant from, Instant to) {
        return reader.findForEmployee(tenantId, employeeId, from, to);
    }

    private TimeEvent resolveReplay(TenantId tenantId, IngestionRecord existing, RecordTimeEventCommand command) {
        String digest = digestOf(command);
        if (!existing.digest().equals(digest)) {
            throw new ConflictException("time_event.idempotency_conflict",
                    "Idempotency key was reused with a different payload");
        }
        return reader.findById(tenantId, existing.timeEventId())
                .orElseThrow(() -> new NotFoundException("time_event.not_found",
                        "Original event for idempotency key no longer exists"));
    }

    private String digestOf(RecordTimeEventCommand command) {
        String canonical = String.join("|",
                command.employeeId().value().toString(),
                command.type().name(),
                command.occurredAt().toString(),
                command.source().source(),
                command.source().sourceEventId() == null ? "" : command.source().sourceEventId());
        return sha256(canonical);
    }

    private static String sha256(String value) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            return HexFormat.of().formatHex(digest.digest(value.getBytes(StandardCharsets.UTF_8)));
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("SHA-256 not available", e);
        }
    }
}
