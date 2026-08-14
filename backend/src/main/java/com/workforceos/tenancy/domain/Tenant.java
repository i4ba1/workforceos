package com.workforceos.tenancy.domain;

import com.workforceos.shared.id.TenantId;

import java.time.ZoneId;
import java.util.Locale;
import java.util.Objects;

/**
 * Root security and configuration boundary for a tenant.
 *
 * <p>The tenant code is canonical and immutable after creation. The default zone and
 * locale seed the tenant-wide time/locale context for all downstream calculations.</p>
 */
public class Tenant {

    private final TenantId id;
    private final String code;
    private String name;
    private ZoneId defaultZone;
    private Locale locale;
    private TenantStatus status;
    private int retentionDays;

    public Tenant(TenantId id, String code, String name, ZoneId defaultZone, Locale locale) {
        this(id, code, name, defaultZone, locale, TenantStatus.ACTIVE, 365);
    }

    public Tenant(TenantId id, String code, String name, ZoneId defaultZone, Locale locale,
                  TenantStatus status, int retentionDays) {
        this.id = Objects.requireNonNull(id, "id");
        this.code = Objects.requireNonNull(code, "code");
        this.name = Objects.requireNonNull(name, "name");
        this.defaultZone = Objects.requireNonNull(defaultZone, "defaultZone");
        this.locale = Objects.requireNonNull(locale, "locale");
        this.status = Objects.requireNonNull(status, "status");
        this.retentionDays = retentionDays;
    }

    public void suspend() {
        this.status = TenantStatus.SUSPENDED;
    }

    public void activate() {
        this.status = TenantStatus.ACTIVE;
    }

    public void rename(String newName) {
        this.name = Objects.requireNonNull(newName, "newName");
    }

    public void changeRetention(int days) {
        if (days < 1) {
            throw new IllegalArgumentException("retentionDays must be positive");
        }
        this.retentionDays = days;
    }

    public TenantId id() {
        return id;
    }

    public String code() {
        return code;
    }

    public String name() {
        return name;
    }

    public ZoneId defaultZone() {
        return defaultZone;
    }

    public Locale locale() {
        return locale;
    }

    public TenantStatus status() {
        return status;
    }

    public int retentionDays() {
        return retentionDays;
    }
}
