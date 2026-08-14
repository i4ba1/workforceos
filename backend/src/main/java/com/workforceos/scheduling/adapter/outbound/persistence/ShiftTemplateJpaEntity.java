package com.workforceos.scheduling.adapter.outbound.persistence;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.LocalTime;
import java.util.UUID;

/** JPA mapping of the shift template aggregate. */
@Entity
@Table(name = "shift_template")
public class ShiftTemplateJpaEntity {

    @Id
    @Column(name = "id", nullable = false)
    private UUID id;

    @Column(name = "tenant_id", nullable = false)
    private UUID tenantId;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "local_start", nullable = false)
    private LocalTime localStart;

    @Column(name = "local_end", nullable = false)
    private LocalTime localEnd;

    @Column(name = "zone_id", nullable = false)
    private String zoneId;

    @Column(name = "break_minutes", nullable = false)
    private long breakMinutes;

    @Column(name = "break_paid", nullable = false)
    private boolean breakPaid;

    protected ShiftTemplateJpaEntity() {
    }

    public ShiftTemplateJpaEntity(UUID id, UUID tenantId, String name, LocalTime localStart, LocalTime localEnd,
                                  String zoneId, long breakMinutes, boolean breakPaid) {
        this.id = id;
        this.tenantId = tenantId;
        this.name = name;
        this.localStart = localStart;
        this.localEnd = localEnd;
        this.zoneId = zoneId;
        this.breakMinutes = breakMinutes;
        this.breakPaid = breakPaid;
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

    public LocalTime getLocalStart() {
        return localStart;
    }

    public LocalTime getLocalEnd() {
        return localEnd;
    }

    public String getZoneId() {
        return zoneId;
    }

    public long getBreakMinutes() {
        return breakMinutes;
    }

    public boolean isBreakPaid() {
        return breakPaid;
    }
}
