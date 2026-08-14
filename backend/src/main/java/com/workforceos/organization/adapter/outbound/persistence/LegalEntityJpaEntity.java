package com.workforceos.organization.adapter.outbound.persistence;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.util.UUID;

/** JPA mapping of the legal entity aggregate. */
@Entity
@Table(name = "legal_entity")
public class LegalEntityJpaEntity {

    @Id
    @Column(name = "id", nullable = false)
    private UUID id;

    @Column(name = "tenant_id", nullable = false)
    private UUID tenantId;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "primary_zone", nullable = false)
    private String primaryZone;

    protected LegalEntityJpaEntity() {
    }

    public LegalEntityJpaEntity(UUID id, UUID tenantId, String name, String primaryZone) {
        this.id = id;
        this.tenantId = tenantId;
        this.name = name;
        this.primaryZone = primaryZone;
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

    public String getPrimaryZone() {
        return primaryZone;
    }
}
