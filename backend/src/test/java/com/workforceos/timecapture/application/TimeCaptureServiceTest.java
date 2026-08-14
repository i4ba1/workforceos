package com.workforceos.timecapture.application;

import com.workforceos.shared.error.ConflictException;
import com.workforceos.shared.id.EmployeeId;
import com.workforceos.shared.id.TenantId;
import com.workforceos.shared.id.TimeEventId;
import com.workforceos.timecapture.domain.EventSource;
import com.workforceos.timecapture.domain.IngestionRecord;
import com.workforceos.timecapture.domain.TimeEvent;
import com.workforceos.timecapture.domain.TimeEventAppender;
import com.workforceos.timecapture.domain.TimeEventReader;
import com.workforceos.timecapture.domain.TimeEventType;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class TimeCaptureServiceTest {

    static class InMemoryTimeCapture implements TimeEventReader, TimeEventAppender {
        private final Map<TimeEventId, TimeEvent> byId = new LinkedHashMap<>();
        private final Map<String, TimeEvent> bySource = new HashMap<>();
        private final Map<String, IngestionRecord> byKey = new HashMap<>();

        @Override
        public Optional<TimeEvent> findById(TenantId tenantId, TimeEventId id) {
            return Optional.ofNullable(byId.get(id));
        }

        @Override
        public Optional<TimeEvent> findBySource(TenantId tenantId, EventSource source) {
            return Optional.ofNullable(bySource.get(sourceKey(source)));
        }

        @Override
        public List<TimeEvent> findForEmployee(TenantId tenantId, EmployeeId employeeId, Instant from, Instant to) {
            return byId.values().stream()
                    .filter(e -> e.employeeId().equals(employeeId))
                    .filter(e -> !e.occurredAt().isBefore(from) && !e.occurredAt().isAfter(to))
                    .sorted(Comparator.comparing(TimeEvent::occurredAt))
                    .toList();
        }

        @Override
        public Optional<IngestionRecord> findIngestion(TenantId tenantId, String idempotencyKey) {
            return Optional.ofNullable(byKey.get(idempotencyKey));
        }

        @Override
        public TimeEvent append(TimeEvent event) {
            byId.put(event.id(), event);
            if (event.source().sourceEventId() != null) {
                bySource.put(sourceKey(event.source()), event);
            }
            return event;
        }

        @Override
        public void recordIngestion(TenantId tenantId, String idempotencyKey, TimeEventId timeEventId,
                                    String requestDigest, Instant createdAt) {
            byKey.put(idempotencyKey, new IngestionRecord(timeEventId, requestDigest));
        }

        private String sourceKey(EventSource source) {
            return source.source() + "|" + source.sourceEventId();
        }

        int eventCount() {
            return byId.size();
        }
    }

    private static final TenantId TENANT = TenantId.newId();
    private static final EmployeeId EMPLOYEE = EmployeeId.newId();
    private static final ZoneId JAKARTA = ZoneId.of("Asia/Jakarta");
    private static final Instant OCCURRED = Instant.parse("2026-08-14T01:00:00Z");
    private static final Instant RECEIVED = Instant.parse("2026-08-14T01:00:05Z");

    private final InMemoryTimeCapture store = new InMemoryTimeCapture();
    private final List<Object> published = new ArrayList<>();
    private final TimeCaptureService service = new TimeCaptureService(store, store, published::add);

    private RecordTimeEventCommand command(String idempotencyKey, Instant occurredAt, EventSource source) {
        return new RecordTimeEventCommand(EMPLOYEE, TimeEventType.CLOCK_IN, occurredAt, RECEIVED, JAKARTA, source, idempotencyKey);
    }

    @Test
    void record_newEvent_persistsAndPublishes() {
        TimeEvent event = service.record(TENANT, command("key-1", OCCURRED, new EventSource("WEB_CLOCK", null)));

        assertThat(event.id()).isNotNull();
        assertThat(store.eventCount()).isEqualTo(1);
        assertThat(published).hasSize(1);
    }

    @Test
    void record_sameKeySamePayload_returnsOriginalWithoutDuplicate() {
        TimeEvent first = service.record(TENANT, command("key-1", OCCURRED, new EventSource("WEB_CLOCK", null)));
        TimeEvent second = service.record(TENANT, command("key-1", OCCURRED, new EventSource("WEB_CLOCK", null)));

        assertThat(second.id()).isEqualTo(first.id());
        assertThat(store.eventCount()).isEqualTo(1);
        assertThat(published).hasSize(1);
    }

    @Test
    void record_sameKeyDifferentPayload_throwsConflict() {
        service.record(TENANT, command("key-1", OCCURRED, new EventSource("WEB_CLOCK", null)));

        Instant different = Instant.parse("2026-08-14T02:00:00Z");
        assertThatThrownBy(() -> service.record(TENANT, command("key-1", different, new EventSource("WEB_CLOCK", null))))
                .isInstanceOf(ConflictException.class)
                .hasMessageContaining("Idempotency key");
        assertThat(store.eventCount()).isEqualTo(1);
    }

    @Test
    void record_sameSourceEventId_returnsOriginal() {
        EventSource source = new EventSource("TERMINAL_7", "ext-42");
        TimeEvent first = service.record(TENANT, command(null, OCCURRED, source));
        TimeEvent second = service.record(TENANT, command(null, OCCURRED, source));

        assertThat(second.id()).isEqualTo(first.id());
        assertThat(store.eventCount()).isEqualTo(1);
    }

    @Test
    void timeline_returnsEventsInOccurrenceOrder() {
        Instant first = Instant.parse("2026-08-14T01:00:00Z");
        Instant second = Instant.parse("2026-08-14T09:00:00Z");
        service.record(TENANT, command("key-1", first, new EventSource("WEB_CLOCK", null)));
        service.record(TENANT, command("key-2", second, new EventSource("WEB_CLOCK", null)));

        List<TimeEvent> timeline = service.timeline(TENANT, EMPLOYEE, Instant.parse("2026-08-14T00:00:00Z"),
                Instant.parse("2026-08-15T00:00:00Z"));

        assertThat(timeline).hasSize(2);
        assertThat(timeline.get(0).occurredAt()).isEqualTo(first);
        assertThat(timeline.get(1).occurredAt()).isEqualTo(second);
    }
}
