package com.workforceos.attendance.adapter.inbound.event;

import com.workforceos.attendance.application.AttendanceService;
import com.workforceos.shared.time.BusinessDate;
import com.workforceos.timecapture.domain.event.TimeEventRecorded;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

/**
 * Triggers attendance recalculation when a raw time event is recorded.
 *
 * <p>Runs synchronously within the capturing transaction (PRD simple synchronous capture);
 * durable event publication with retry is a later hardening.</p>
 */
@Component
public class TimeEventRecordedListener {

    private final AttendanceService attendanceService;

    public TimeEventRecordedListener(AttendanceService attendanceService) {
        this.attendanceService = attendanceService;
    }

    @EventListener
    public void on(TimeEventRecorded event) {
        BusinessDate businessDate = BusinessDate.of(event.occurredAt().atZone(event.zoneId()).toLocalDate());
        attendanceService.recalculate(event.tenantId(), event.employeeId(), businessDate);
    }
}
