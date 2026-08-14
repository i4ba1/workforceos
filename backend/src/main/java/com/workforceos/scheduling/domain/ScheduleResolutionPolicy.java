package com.workforceos.scheduling.domain;

import com.workforceos.shared.id.EmployeeId;
import com.workforceos.shared.id.ScheduleEntryId;
import com.workforceos.shared.time.BusinessDate;
import com.workforceos.shared.time.WorkInterval;

import java.util.Optional;

/**
 * Resolves the applicable schedule when planned work and actual events overlap.
 *
 * <p>Implemented as a small strategy so that business-date anchoring (cross-midnight
 * shifts) and unscheduled-work detection remain open for extension without touching the
 * calculation core.</p>
 */
public interface ScheduleResolutionPolicy {

    /**
     * Resolves the schedule entry governing the given employee, business date and worked
     * window, or {@link Optional#empty()} when no qualifying schedule exists.
     */
    Optional<ScheduleEntryId> resolve(EmployeeId employeeId, BusinessDate businessDate, WorkInterval workedWindow);
}
