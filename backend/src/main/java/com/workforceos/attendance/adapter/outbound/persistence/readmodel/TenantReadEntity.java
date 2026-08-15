package com.workforceos.attendance.adapter.outbound.persistence.readmodel;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import org.hibernate.annotations.Immutable;

import java.util.UUID;

/** Read-only projection of the tenancy module's {@code tenant} table. */
@Entity
@Immutable
@Table(name = "tenant")
public class TenantReadEntity {

    @Id
    @Column(name = "id")
    private UUID id;

    @Column(name = "default_zone")
    private String defaultZone;

    protected TenantReadEntity() {
    }

    public UUID getId() {
        return id;
    }

    public String getDefaultZone() {
        return defaultZone;
    }
}
