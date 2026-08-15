package com.workforceos.audit.domain;

import com.workforceos.shared.id.TenantId;
import com.workforceos.shared.id.UserId;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

/** Read-side port for querying the immutable audit stream. */
public interface AuditReader {

    List<AuditEvent> query(TenantId tenantId, String entityType, UUID entityId, UserId actorId,
                           Instant from, Instant to);
}
