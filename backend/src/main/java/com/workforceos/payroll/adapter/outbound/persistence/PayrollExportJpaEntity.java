package com.workforceos.payroll.adapter.outbound.persistence;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.Instant;
import java.util.UUID;

/** JPA mapping of a reproducible payroll export. */
@Entity
@Table(name = "payroll_export")
public class PayrollExportJpaEntity {

    @Id
    @Column(name = "id", nullable = false)
    private UUID id;

    @Column(name = "tenant_id", nullable = false)
    private UUID tenantId;

    @Column(name = "period_id", nullable = false)
    private UUID periodId;

    @Column(name = "version", nullable = false)
    private int version;

    @Column(name = "checksum", nullable = false)
    private String checksum;

    @Column(name = "format", nullable = false)
    private String format;

    @Column(name = "generated_by", nullable = false)
    private UUID generatedBy;

    @Column(name = "generated_at", nullable = false)
    private Instant generatedAt;

    protected PayrollExportJpaEntity() {
    }

    public PayrollExportJpaEntity(UUID id, UUID tenantId, UUID periodId, int version, String checksum,
                                  String format, UUID generatedBy, Instant generatedAt) {
        this.id = id;
        this.tenantId = tenantId;
        this.periodId = periodId;
        this.version = version;
        this.checksum = checksum;
        this.format = format;
        this.generatedBy = generatedBy;
        this.generatedAt = generatedAt;
    }

    public UUID getId() {
        return id;
    }

    public UUID getTenantId() {
        return tenantId;
    }

    public UUID getPeriodId() {
        return periodId;
    }

    public int getVersion() {
        return version;
    }

    public String getChecksum() {
        return checksum;
    }

    public String getFormat() {
        return format;
    }

    public UUID getGeneratedBy() {
        return generatedBy;
    }

    public Instant getGeneratedAt() {
        return generatedAt;
    }
}
