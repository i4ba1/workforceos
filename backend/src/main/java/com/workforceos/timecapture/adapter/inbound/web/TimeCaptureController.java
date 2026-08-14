package com.workforceos.timecapture.adapter.inbound.web;

import com.workforceos.shared.context.TenantContextHolder;
import com.workforceos.shared.id.EmployeeId;
import com.workforceos.shared.id.TenantId;
import com.workforceos.timecapture.adapter.inbound.web.TimeCaptureDtos.RecordTimeEventRequest;
import com.workforceos.timecapture.adapter.inbound.web.TimeCaptureDtos.TimeEventResponse;
import com.workforceos.timecapture.application.RecordTimeEventCommand;
import com.workforceos.timecapture.application.TimeCaptureService;
import com.workforceos.timecapture.domain.EventSource;
import com.workforceos.timecapture.domain.TimeEventType;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;
import java.time.ZoneId;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1")
public class TimeCaptureController {

    private static final String DEFAULT_SOURCE = "WEB_CLOCK";

    private final TimeCaptureService timeCaptureService;

    public TimeCaptureController(TimeCaptureService timeCaptureService) {
        this.timeCaptureService = timeCaptureService;
    }

    @PostMapping("/time-events")
    public TimeEventResponse record(@Valid @RequestBody RecordTimeEventRequest request,
                                    @RequestHeader(value = "Idempotency-Key", required = false) String idempotencyKey) {
        TenantId tenantId = TenantContextHolder.require().tenantId();
        var event = timeCaptureService.record(tenantId, new RecordTimeEventCommand(
                new EmployeeId(request.employeeId()),
                TimeEventType.valueOf(request.eventType()),
                request.occurredAt(),
                Instant.now(),
                ZoneId.of(request.zoneId()),
                new EventSource(request.source() == null ? DEFAULT_SOURCE : request.source(), request.sourceEventId()),
                idempotencyKey));
        return TimeEventResponse.from(event);
    }

    @GetMapping("/employees/{employeeId}/time-events")
    public List<TimeEventResponse> timeline(@PathVariable UUID employeeId,
                                            @RequestParam Instant from,
                                            @RequestParam Instant to) {
        TenantId tenantId = TenantContextHolder.require().tenantId();
        return timeCaptureService.timeline(tenantId, new EmployeeId(employeeId), from, to).stream()
                .map(TimeEventResponse::from).toList();
    }
}
