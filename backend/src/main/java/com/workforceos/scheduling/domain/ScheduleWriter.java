package com.workforceos.scheduling.domain;

/** Write-side port for schedules and shift templates. */
public interface ScheduleWriter {

    ShiftTemplate saveShiftTemplate(ShiftTemplate template);

    ScheduleEntry saveScheduleEntry(ScheduleEntry entry);
}
