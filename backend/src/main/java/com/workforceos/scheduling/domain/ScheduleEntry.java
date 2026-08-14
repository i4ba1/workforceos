package com.workforceos.scheduling.domain;

import com.workforceos.shared.id.EmployeeId;
import com.workforceos.shared.id.ScheduleEntryId;
import com.workforceos.shared.id.TenantId;
import com.workforceos.shared.time.BusinessDate;

import java.time.Instant;
import java.time.ZoneId;
import java.util.Objects;

/**
 * A concrete scheduled work entry for an employee on a business date.
 *
 * <p>Stored with both local business semantics ({@link BusinessDate}, IANA {@link ZoneId})
 * and derived planned instants for execution. Schedule changes after clock events exist are
 * versioned and audited.</p>
 */
public class ScheduleEntry {

    private final ScheduleEntryId id;
    private final TenantId tenantId;
    private final EmployeeId employeeId;
    private final BusinessDate businessDate;
    private final ZoneId zoneId;
    private final Instant plannedStart;
    private final Instant plannedEnd;
    private final long version;

    public ScheduleEntry(ScheduleEntryId id, TenantId tenantId, EmployeeId employeeId,
                         BusinessDate businessDate, ZoneId zoneId,
                         Instant plannedStart, Instant plannedEnd, long version) {
        this.id = Objects.requireNonNull(id, "id");
        this.tenantId = Objects.requireNonNull(tenantId, "tenantId");
        this.employeeId = Objects.requireNonNull(employeeId, "employeeId");
        this.businessDate = Objects.requireNonNull(businessDate, "businessDate");
        this.zoneId = Objects.requireNonNull(zoneId, "zoneId");
        this.plannedStart = Objects.requireNonNull(plannedStart, "plannedStart");
        this.plannedEnd = Objects.requireNonNull(plannedEnd, "plannedEnd");
        if (!plannedEnd.isAfter(plannedStart)) {
            throw new IllegalArgumentException("plannedEnd must be after plannedStart");
        }
        this.version = version;
    }

    public boolean overlaps(Instant start, Instant end) {
        return plannedStart.isBefore(end) && start.isBefore(plannedEnd);
    }

    public ScheduleEntryId id() {
        return id;
    }

    public TenantId tenantId() {
        return tenantId;
    }

    public EmployeeId employeeId() {
        return employeeId;
    }

    public BusinessDate businessDate() {
        return businessDate;
    }

    public ZoneId zoneId() {
        return zoneId;
    }

    public Instant plannedStart() {
        return plannedStart;
    }

    public Instant plannedEnd() {
        return plannedEnd;
    }

    public long version() {
        return version;
    }
}
