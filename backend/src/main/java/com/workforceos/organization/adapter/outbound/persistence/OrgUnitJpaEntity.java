package com.workforceos.organization.adapter.outbound.persistence;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.util.UUID;

/** JPA mapping of the org unit aggregate. */
@Entity
@Table(name = "org_unit")
public class OrgUnitJpaEntity {

    @Id
    @Column(name = "id", nullable = false)
    private UUID id;

    @Column(name = "tenant_id", nullable = false)
    private UUID tenantId;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "parent_id")
    private UUID parentId;

    protected OrgUnitJpaEntity() {
    }

    public OrgUnitJpaEntity(UUID id, UUID tenantId, String name, UUID parentId) {
        this.id = id;
        this.tenantId = tenantId;
        this.name = name;
        this.parentId = parentId;
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

    public UUID getParentId() {
        return parentId;
    }
}
