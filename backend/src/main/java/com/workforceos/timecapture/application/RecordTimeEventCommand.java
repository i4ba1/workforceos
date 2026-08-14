package com.workforceos.timecapture.application;

import com.workforceos.shared.id.EmployeeId;
import com.workforceos.timecapture.domain.EventSource;
import com.workforceos.timecapture.domain.TimeEventType;

import java.time.Instant;
import java.time.ZoneId;

/**
 * Command to record a raw time event.
 *
 * @param idempotencyKey optional client-generated key making retries safe; may be null
 *                       when deduplication relies solely on {@link EventSource}
 */
public record RecordTimeEventCommand(
        EmployeeId employeeId,
        TimeEventType type,
        Instant occurredAt,
        Instant receivedAt,
        ZoneId zoneId,
        EventSource source,
        String idempotencyKey) {
}
