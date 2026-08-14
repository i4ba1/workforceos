package com.workforceos.tenancy.adapter.inbound.web;

import com.workforceos.tenancy.domain.Tenant;
import jakarta.validation.constraints.NotBlank;

import java.util.UUID;

/** Web DTOs for tenant endpoints. */
public final class TenantDtos {

    private TenantDtos() {
    }

    public record CreateTenantRequest(
            @NotBlank String code,
            @NotBlank String name,
            @NotBlank String defaultZone,
            @NotBlank String locale) {
    }

    public record TenantResponse(
            UUID id,
            String code,
            String name,
            String defaultZone,
            String locale,
            String status,
            int retentionDays) {

        public static TenantResponse from(Tenant tenant) {
            return new TenantResponse(
                    tenant.id().value(),
                    tenant.code(),
                    tenant.name(),
                    tenant.defaultZone().getId(),
                    tenant.locale().toLanguageTag(),
                    tenant.status().name(),
                    tenant.retentionDays());
        }
    }
}
