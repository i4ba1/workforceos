package com.workforceos.attendance.adapter.inbound.web;

import com.workforceos.attendance.adapter.inbound.web.AttendanceDtos.AttendanceDetailResponse;
import com.workforceos.attendance.adapter.inbound.web.AttendanceDtos.AttendanceRecordResponse;
import com.workforceos.attendance.adapter.inbound.web.AttendanceDtos.ExceptionResponse;
import com.workforceos.attendance.adapter.inbound.web.AttendanceDtos.RecalculateRequest;
import com.workforceos.attendance.application.AttendanceService;
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

    private final AttendanceService attendanceService;

    public AttendanceController(AttendanceService attendanceService) {
        this.attendanceService = attendanceService;
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
}
