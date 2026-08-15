package com.workforceos.attendance.domain;

import com.workforceos.shared.id.ScheduleEntryId;
import com.workforceos.shared.time.BusinessDate;

import java.time.Instant;
import java.time.ZoneId;
import java.util.Objects;

/**
 * Planned-shift facts read from the scheduling module.
 *
 * <p>A read model: the planned window is expressed as instants plus an IANA zone so the
 * calculator can anchor a business date and match actual events to planned work.</p>
 */
public record PlannedShift(
        ScheduleEntryId scheduleEntryId,
        ZoneId zoneId,
        Instant plannedStart,
        Instant plannedEnd,
        BusinessDate businessDate) {

    public PlannedShift {
        Objects.requireNonNull(scheduleEntryId, "scheduleEntryId");
        Objects.requireNonNull(zoneId, "zoneId");
        Objects.requireNonNull(plannedStart, "plannedStart");
        Objects.requireNonNull(plannedEnd, "plannedEnd");
        Objects.requireNonNull(businessDate, "businessDate");
    }
}
