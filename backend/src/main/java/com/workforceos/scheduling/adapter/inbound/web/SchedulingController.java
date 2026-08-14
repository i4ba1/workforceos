package com.workforceos.scheduling.adapter.inbound.web;

import com.workforceos.scheduling.adapter.inbound.web.SchedulingDtos.CreateScheduleEntryRequest;
import com.workforceos.scheduling.adapter.inbound.web.SchedulingDtos.CreateShiftTemplateRequest;
import com.workforceos.scheduling.adapter.inbound.web.SchedulingDtos.ScheduleEntryResponse;
import com.workforceos.scheduling.adapter.inbound.web.SchedulingDtos.ShiftTemplateResponse;
import com.workforceos.scheduling.application.SchedulingService;
import com.workforceos.shared.context.TenantContextHolder;
import com.workforceos.shared.id.EmployeeId;
import com.workforceos.shared.id.TenantId;
import com.workforceos.shared.time.BusinessDate;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1")
public class SchedulingController {

    private final SchedulingService schedulingService;

    public SchedulingController(SchedulingService schedulingService) {
        this.schedulingService = schedulingService;
    }

    @GetMapping("/shift-templates")
    public List<ShiftTemplateResponse> listShiftTemplates() {
        TenantId tenantId = TenantContextHolder.require().tenantId();
        return schedulingService.listShiftTemplates(tenantId).stream().map(ShiftTemplateResponse::from).toList();
    }

    @PostMapping("/shift-templates")
    public ShiftTemplateResponse createShiftTemplate(@Valid @RequestBody CreateShiftTemplateRequest request) {
        TenantId tenantId = TenantContextHolder.require().tenantId();
        var template = schedulingService.createShiftTemplate(
                tenantId,
                request.name(),
                request.localStart(),
                request.localEnd(),
                ZoneId.of(request.zoneId()),
                request.breakMinutes(),
                request.breakPaid());
        return ShiftTemplateResponse.from(template);
    }

    @PostMapping("/schedule-entries")
    public ScheduleEntryResponse createScheduleEntry(@Valid @RequestBody CreateScheduleEntryRequest request) {
        TenantId tenantId = TenantContextHolder.require().tenantId();
        var entry = schedulingService.createScheduleEntry(
                tenantId,
                new EmployeeId(request.employeeId()),
                BusinessDate.of(request.businessDate()),
                ZoneId.of(request.zoneId()),
                request.plannedStart(),
                request.plannedEnd());
        return ScheduleEntryResponse.from(entry);
    }

    @GetMapping("/employees/{employeeId}/schedule")
    public List<ScheduleEntryResponse> schedule(@PathVariable UUID employeeId,
                                                @RequestParam LocalDate from,
                                                @RequestParam LocalDate to) {
        TenantId tenantId = TenantContextHolder.require().tenantId();
        return schedulingService.listEntries(tenantId, new EmployeeId(employeeId), BusinessDate.of(from), BusinessDate.of(to))
                .stream().map(ScheduleEntryResponse::from).toList();
    }
}
