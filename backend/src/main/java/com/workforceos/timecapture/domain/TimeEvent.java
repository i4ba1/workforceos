package com.workforceos.timecapture.domain;

import com.workforceos.shared.id.EmployeeId;
import com.workforceos.shared.id.TenantId;
import com.workforceos.shared.id.TimeEventId;

import java.time.Instant;
import java.time.ZoneId;
import java.util.Objects;

/**
 * An immutable, append-only raw time event.
 *
 * <p>Persisted in UTC via {@link Instant}; the IANA {@link ZoneId} is retained for
 * business-time interpretation. Later corrections never overwrite this record.</p>
 */
public class TimeEvent {

    private final TimeEventId id;
    private final TenantId tenantId;
    private final EmployeeId employeeId;
    private final TimeEventType type;
    private final Instant occurredAt;
    private final Instant receivedAt;
    private final ZoneId zoneId;
    private final EventSource source;

    public TimeEvent(TimeEventId id, TenantId tenantId, EmployeeId employeeId, TimeEventType type,
                     Instant occurredAt, Instant receivedAt, ZoneId zoneId, EventSource source) {
        this.id = Objects.requireNonNull(id, "id");
        this.tenantId = Objects.requireNonNull(tenantId, "tenantId");
        this.employeeId = Objects.requireNonNull(employeeId, "employeeId");
        this.type = Objects.requireNonNull(type, "type");
        this.occurredAt = Objects.requireNonNull(occurredAt, "occurredAt");
        this.receivedAt = Objects.requireNonNull(receivedAt, "receivedAt");
        this.zoneId = Objects.requireNonNull(zoneId, "zoneId");
        this.source = Objects.requireNonNull(source, "source");
    }

    public TimeEventId id() {
        return id;
    }

    public TenantId tenantId() {
        return tenantId;
    }

    public EmployeeId employeeId() {
        return employeeId;
    }

    public TimeEventType type() {
        return type;
    }

    public Instant occurredAt() {
        return occurredAt;
    }

    public Instant receivedAt() {
        return receivedAt;
    }

    public ZoneId zoneId() {
        return zoneId;
    }

    public EventSource source() {
        return source;
    }
}
