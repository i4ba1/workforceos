package com.workforceos.tenancy.adapter.outbound.persistence;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.util.UUID;

/** JPA mapping of the tenant aggregate (adapter-only; the domain model stays pure). */
@Entity
@Table(name = "tenant")
public class TenantJpaEntity {

    @Id
    @Column(name = "id", nullable = false)
    private UUID id;

    @Column(name = "code", nullable = false, unique = true)
    private String code;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "default_zone", nullable = false)
    private String defaultZone;

    @Column(name = "locale", nullable = false)
    private String locale;

    @Column(name = "status", nullable = false)
    private String status;

    @Column(name = "retention_days", nullable = false)
    private int retentionDays;

    protected TenantJpaEntity() {
    }

    public TenantJpaEntity(UUID id, String code, String name, String defaultZone, String locale,
                           String status, int retentionDays) {
        this.id = id;
        this.code = code;
        this.name = name;
        this.defaultZone = defaultZone;
        this.locale = locale;
        this.status = status;
        this.retentionDays = retentionDays;
    }

    public UUID getId() {
        return id;
    }

    public String getCode() {
        return code;
    }

    public String getName() {
        return name;
    }

    public String getDefaultZone() {
        return defaultZone;
    }

    public String getLocale() {
        return locale;
    }

    public String getStatus() {
        return status;
    }

    public int getRetentionDays() {
        return retentionDays;
    }
}
