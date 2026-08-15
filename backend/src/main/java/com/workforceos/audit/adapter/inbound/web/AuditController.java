package com.workforceos.audit.adapter.inbound.web;

import com.workforceos.audit.domain.AuditEvent;
import com.workforceos.audit.domain.AuditReader;
import com.workforceos.shared.context.TenantContextHolder;
import com.workforceos.shared.id.TenantId;
import com.workforceos.shared.id.UserId;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/audit-events")
public class AuditController {

    private final AuditReader auditReader;

    public AuditController(AuditReader auditReader) {
        this.auditReader = auditReader;
    }

    @GetMapping
    public List<AuditEventResponse> query(@RequestParam(required = false) String entityType,
                                          @RequestParam(required = false) UUID entityId,
                                          @RequestParam(required = false) UUID actorId,
                                          @RequestParam(required = false) Instant from,
                                          @RequestParam(required = false) Instant to) {
        TenantId tenantId = TenantContextHolder.require().tenantId();
        return auditReader.query(tenantId, entityType, entityId, actorId == null ? null : new UserId(actorId), from, to)
                .stream().map(AuditEventResponse::from).toList();
    }

    public record AuditEventResponse(
            UUID id,
            UUID actorId,
            String action,
            String entityType,
            UUID entityId,
            String correlationId,
            Instant occurredAt) {

        public static AuditEventResponse from(AuditEvent event) {
            return new AuditEventResponse(
                    event.id().value(),
                    event.actorId().value(),
                    event.action(),
                    event.entityType(),
                    event.entityId(),
                    event.correlationId(),
                    event.occurredAt());
        }
    }
}
