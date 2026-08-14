package com.workforceos.reporting.domain;

import com.workforceos.shared.id.TenantId;
import com.workforceos.shared.time.BusinessDate;
import com.workforceos.shared.time.Minutes;

import java.util.Objects;

/**
 * Daily attendance rollup used by dashboards.
 *
 * <p>Read-optimized projection; always tenant- and permission-scoped at query time.</p>
 */
public record DailyAttendanceProjection(
        TenantId tenantId,
        BusinessDate businessDate,
        long scheduled,
        long present,
        long absent,
        long late,
        long onLeave,
        long openExceptions,
        Minutes overtimeMinutes) {

    public DailyAttendanceProjection {
        Objects.requireNonNull(tenantId, "tenantId");
        Objects.requireNonNull(businessDate, "businessDate");
        Objects.requireNonNull(overtimeMinutes, "overtimeMinutes");
    }
}
