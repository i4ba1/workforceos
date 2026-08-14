package com.workforceos.notification.domain;

import com.workforceos.shared.id.NotificationId;
import com.workforceos.shared.id.TenantId;

import java.time.Instant;
import java.util.Objects;

/** A notification with delivery state, tracked after intent creation. */
public class Notification {

    private final NotificationId id;
    private final TenantId tenantId;
    private final NotificationIntent intent;
    private NotificationState state;
    private Instant deliveredAt;

    public Notification(NotificationId id, TenantId tenantId, NotificationIntent intent) {
        this.id = Objects.requireNonNull(id, "id");
        this.tenantId = Objects.requireNonNull(tenantId, "tenantId");
        this.intent = Objects.requireNonNull(intent, "intent");
        this.state = NotificationState.PENDING;
    }

    public void markSent(Instant at) {
        this.state = NotificationState.SENT;
        this.deliveredAt = Objects.requireNonNull(at, "at");
    }

    public void markFailed() {
        this.state = NotificationState.FAILED;
    }

    public NotificationId id() {
        return id;
    }

    public TenantId tenantId() {
        return tenantId;
    }

    public NotificationIntent intent() {
        return intent;
    }

    public NotificationState state() {
        return state;
    }

    public Instant deliveredAt() {
        return deliveredAt;
    }
}
