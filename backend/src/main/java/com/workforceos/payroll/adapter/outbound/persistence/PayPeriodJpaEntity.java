package com.workforceos.payroll.adapter.outbound.persistence;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

/** JPA mapping of the pay period aggregate. */
@Entity
@Table(name = "pay_period")
public class PayPeriodJpaEntity {

    @Id
    @Column(name = "id", nullable = false)
    private UUID id;

    @Column(name = "tenant_id", nullable = false)
    private UUID tenantId;

    @Column(name = "start_date", nullable = false)
    private LocalDate startDate;

    @Column(name = "end_date", nullable = false)
    private LocalDate endDate;

    @Column(name = "state", nullable = false)
    private String state;

    @Column(name = "version", nullable = false)
    private long version;

    @Column(name = "closed_by")
    private UUID closedBy;

    @Column(name = "closed_at")
    private Instant closedAt;

    protected PayPeriodJpaEntity() {
    }

    public PayPeriodJpaEntity(UUID id, UUID tenantId, LocalDate startDate, LocalDate endDate, String state,
                              long version, UUID closedBy, Instant closedAt) {
        this.id = id;
        this.tenantId = tenantId;
        this.startDate = startDate;
        this.endDate = endDate;
        this.state = state;
        this.version = version;
        this.closedBy = closedBy;
        this.closedAt = closedAt;
    }

    public UUID getId() {
        return id;
    }

    public UUID getTenantId() {
        return tenantId;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public String getState() {
        return state;
    }

    public long getVersion() {
        return version;
    }

    public UUID getClosedBy() {
        return closedBy;
    }

    public Instant getClosedAt() {
        return closedAt;
    }
}
