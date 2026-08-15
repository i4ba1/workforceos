package com.workforceos.attendance.adapter.inbound.event;

import com.workforceos.attendance.application.AttendanceService;
import com.workforceos.attendance.domain.ExceptionState;
import com.workforceos.approval.domain.event.ApprovalCompleted;
import com.workforceos.shared.id.AttendanceRecordId;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

/**
 * Completes the exception lifecycle: when a correction review for an attendance record is
 * approved, its open exceptions are resolved; when rejected, they are dismissed.
 */
@Component
public class ApprovalCompletedListener {

    private static final String ATTENDANCE_RECORD_SUBJECT = "ATTENDANCE_RECORD";

    private final AttendanceService attendanceService;

    public ApprovalCompletedListener(AttendanceService attendanceService) {
        this.attendanceService = attendanceService;
    }

    @EventListener
    public void on(ApprovalCompleted event) {
        if (!ATTENDANCE_RECORD_SUBJECT.equals(event.subjectType())) {
            return;
        }
        ExceptionState target = event.approved() ? ExceptionState.RESOLVED : ExceptionState.DISMISSED;
        attendanceService.resolveExceptions(event.tenantId(), new AttendanceRecordId(event.subjectId()), target);
    }
}
