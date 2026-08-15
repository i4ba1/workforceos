package com.workforceos.attendance.adapter.inbound.web;

import com.workforceos.attendance.adapter.inbound.web.AttendanceDtos.AttendanceDetailResponse;
import com.workforceos.attendance.adapter.inbound.web.AttendanceDtos.AttendanceRecordResponse;
import com.workforceos.attendance.adapter.inbound.web.AttendanceDtos.CorrectionRequest;
import com.workforceos.attendance.adapter.inbound.web.AttendanceDtos.CorrectionResponse;
import com.workforceos.attendance.adapter.inbound.web.AttendanceDtos.ExceptionResponse;
import com.workforceos.attendance.adapter.inbound.web.AttendanceDtos.RecalculateRequest;
import com.workforceos.attendance.application.AttendanceService;
import com.workforceos.approval.application.ApprovalService;
import com.workforceos.shared.context.TenantContextHolder;
import com.workforceos.shared.id.AttendanceRecordId;
import com.workforceos.shared.id.EmployeeId;
import com.workforceos.shared.id.TenantId;
import com.workforceos.shared.time.BusinessDate;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/attendance")
public class AttendanceController {

    private static final String ATTENDANCE_RECORD_SUBJECT = "ATTENDANCE_RECORD";

    private final AttendanceService attendanceService;
    private final ApprovalService approvalService;

    public AttendanceController(AttendanceService attendanceService, ApprovalService approvalService) {
        this.attendanceService = attendanceService;
        this.approvalService = approvalService;
    }

    @GetMapping
    public List<AttendanceRecordResponse> list() {
        TenantId tenantId = TenantContextHolder.require().tenantId();
        return attendanceService.list(tenantId).stream().map(AttendanceRecordResponse::from).toList();
    }

    @GetMapping("/{id}")
    public AttendanceDetailResponse detail(@PathVariable UUID id) {
        TenantId tenantId = TenantContextHolder.require().tenantId();
        var record = attendanceService.get(tenantId, new AttendanceRecordId(id));
        List<ExceptionResponse> exceptions = attendanceService.exceptions(tenantId, new AttendanceRecordId(id)).stream()
                .map(ExceptionResponse::from).toList();
        return new AttendanceDetailResponse(AttendanceRecordResponse.from(record), exceptions);
    }

    @PostMapping("/recalculate")
    public AttendanceRecordResponse recalculate(@Valid @RequestBody RecalculateRequest request) {
        TenantId tenantId = TenantContextHolder.require().tenantId();
        var record = attendanceService.recalculate(
                tenantId,
                new EmployeeId(request.employeeId()),
                BusinessDate.of(request.businessDate()));
        return AttendanceRecordResponse.from(record);
    }

    @GetMapping("/exceptions")
    public List<ExceptionResponse> exceptions() {
        TenantId tenantId = TenantContextHolder.require().tenantId();
        return attendanceService.openExceptions(tenantId).stream().map(ExceptionResponse::from).toList();
    }

    @PostMapping("/{id}/corrections")
    public CorrectionResponse submitCorrection(@PathVariable UUID id, @Valid @RequestBody CorrectionRequest request) {
        var context = TenantContextHolder.require();
        var approvalCase = approvalService.open(context.tenantId(), ATTENDANCE_RECORD_SUBJECT, id,
                context.userId(), request.reason());
        return new CorrectionResponse(approvalCase.id().value());
    }
}
