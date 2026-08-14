package com.workforceos.notification.domain;

/**
 * Delivers an already-created {@link NotificationIntent} to an external channel.
 *
 * <p>Concrete channels (email, webhook, in-app) implement this contract. Deliveries must
 * be idempotent and support bounded retry.</p>
 */
public interface NotificationChannel {

    String channel();

    void deliver(NotificationIntent intent);
}
