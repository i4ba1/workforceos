package com.workforceos.timecapture.adapter.inbound.web;

import com.workforceos.timecapture.domain.TimeEvent;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.Instant;
import java.util.UUID;

/** Web DTOs for time-event endpoints. */
public final class TimeCaptureDtos {

    private TimeCaptureDtos() {
    }

    public record RecordTimeEventRequest(
            @NotNull UUID employeeId,
            @NotBlank String eventType,
            @NotNull Instant occurredAt,
            @NotBlank String zoneId,
            String source,
            String sourceEventId) {
    }

    public record TimeEventResponse(
            UUID id,
            UUID employeeId,
            String eventType,
            Instant occurredAt,
            Instant receivedAt,
            String zoneId,
            String source,
            String sourceEventId) {

        public static TimeEventResponse from(TimeEvent event) {
            return new TimeEventResponse(
                    event.id().value(),
                    event.employeeId().value(),
                    event.type().name(),
                    event.occurredAt(),
                    event.receivedAt(),
                    event.zoneId().getId(),
                    event.source().source(),
                    event.source().sourceEventId());
        }
    }
}
