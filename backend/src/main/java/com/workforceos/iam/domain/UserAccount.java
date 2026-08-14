package com.workforceos.iam.domain;

import com.workforceos.shared.id.TenantId;
import com.workforceos.shared.id.UserId;

import java.util.Objects;

/** A user account bound to a tenant and mapped from the identity provider. */
public class UserAccount {

    private final UserId id;
    private final TenantId tenantId;
    private final String subject;   // stable IdP subject claim
    private String username;
    private UserStatus status;

    public UserAccount(UserId id, TenantId tenantId, String subject, String username) {
        this.id = Objects.requireNonNull(id, "id");
        this.tenantId = Objects.requireNonNull(tenantId, "tenantId");
        this.subject = Objects.requireNonNull(subject, "subject");
        this.username = Objects.requireNonNull(username, "username");
        this.status = UserStatus.ACTIVE;
    }

    public void disable() {
        this.status = UserStatus.DISABLED;
    }

    public void enable() {
        this.status = UserStatus.ACTIVE;
    }

    public UserId id() {
        return id;
    }

    public TenantId tenantId() {
        return tenantId;
    }

    public String subject() {
        return subject;
    }

    public String username() {
        return username;
    }

    public UserStatus status() {
        return status;
    }
}
