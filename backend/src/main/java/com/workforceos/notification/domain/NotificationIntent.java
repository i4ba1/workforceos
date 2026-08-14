package com.workforceos.notification.domain;

import com.workforceos.shared.id.TenantId;
import com.workforceos.shared.id.UserId;

import java.util.Objects;

/**
 * An already-created notification intent ready for delivery.
 *
 * <p>Created after the triggering state change is durably committed; delivery happens
 * out-of-band and is retried idempotently by the {@link NotificationChannel}.</p>
 */
public record NotificationIntent(
        TenantId tenantId,
        UserId recipientId,
        String channel,
        String subject,
        String body) {

    public NotificationIntent {
        Objects.requireNonNull(tenantId, "tenantId");
        Objects.requireNonNull(recipientId, "recipientId");
        Objects.requireNonNull(channel, "channel");
        Objects.requireNonNull(subject, "subject");
        Objects.requireNonNull(body, "body");
    }
}
