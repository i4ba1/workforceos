package com.workforceos.organization.adapter.outbound.persistence;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.util.UUID;

/** JPA mapping of the work location aggregate. */
@Entity
@Table(name = "work_location")
public class WorkLocationJpaEntity {

    @Id
    @Column(name = "id", nullable = false)
    private UUID id;

    @Column(name = "tenant_id", nullable = false)
    private UUID tenantId;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "zone_id", nullable = false)
    private String zoneId;

    protected WorkLocationJpaEntity() {
    }

    public WorkLocationJpaEntity(UUID id, UUID tenantId, String name, String zoneId) {
        this.id = id;
        this.tenantId = tenantId;
        this.name = name;
        this.zoneId = zoneId;
    }

    public UUID getId() {
        return id;
    }

    public UUID getTenantId() {
        return tenantId;
    }

    public String getName() {
        return name;
    }

    public String getZoneId() {
        return zoneId;
    }
}
