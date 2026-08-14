package com.workforceos.timecapture.domain;

import com.workforceos.shared.id.TenantId;
import com.workforceos.shared.id.TimeEventId;

import java.time.Instant;

/** Write-side port for appending raw time events and their idempotency records. */
public interface TimeEventAppender {

    TimeEvent append(TimeEvent event);

    void recordIngestion(TenantId tenantId, String idempotencyKey, TimeEventId timeEventId,
                         String requestDigest, Instant createdAt);
}
