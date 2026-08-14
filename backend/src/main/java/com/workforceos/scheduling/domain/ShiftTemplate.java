package com.workforceos.scheduling.domain;

import com.workforceos.shared.id.ShiftTemplateId;
import com.workforceos.shared.id.TenantId;

import java.time.LocalTime;
import java.time.ZoneId;
import java.util.Objects;

/**
 * A reusable planned-work pattern expressed in local time plus a zone strategy.
 *
 * <p>The template stores local start/end and an IANA zone; derived instants are computed
 * per business date at execution time so that DST and cross-midnight shifts resolve
 * correctly.</p>
 */
public class ShiftTemplate {

    private final ShiftTemplateId id;
    private final TenantId tenantId;
    private final String name;
    private final LocalTime localStart;
    private final LocalTime localEnd;
    private final ZoneId zoneId;
    private final BreakConfig breakConfig;

    public ShiftTemplate(ShiftTemplateId id, TenantId tenantId, String name,
                         LocalTime localStart, LocalTime localEnd, ZoneId zoneId,
                         BreakConfig breakConfig) {
        this.id = Objects.requireNonNull(id, "id");
        this.tenantId = Objects.requireNonNull(tenantId, "tenantId");
        this.name = Objects.requireNonNull(name, "name");
        this.localStart = Objects.requireNonNull(localStart, "localStart");
        this.localEnd = Objects.requireNonNull(localEnd, "localEnd");
        this.zoneId = Objects.requireNonNull(zoneId, "zoneId");
        this.breakConfig = Objects.requireNonNull(breakConfig, "breakConfig");
    }

    public ShiftTemplateId id() {
        return id;
    }

    public TenantId tenantId() {
        return tenantId;
    }

    public String name() {
        return name;
    }

    public LocalTime localStart() {
        return localStart;
    }

    public LocalTime localEnd() {
        return localEnd;
    }

    public ZoneId zoneId() {
        return zoneId;
    }

    public BreakConfig breakConfig() {
        return breakConfig;
    }
}
