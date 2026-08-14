package com.workforceos.organization.adapter.inbound.web;

import com.workforceos.organization.domain.LegalEntity;
import com.workforceos.organization.domain.OrgUnit;
import com.workforceos.organization.domain.WorkLocation;
import jakarta.validation.constraints.NotBlank;

import java.util.UUID;

/** Web DTOs for organization endpoints. */
public final class OrganizationDtos {

    private OrganizationDtos() {
    }

    public record CreateOrgUnitRequest(@NotBlank String name, UUID parentId) {
    }

    public record OrgUnitResponse(UUID id, String name, UUID parentId) {

        public static OrgUnitResponse from(OrgUnit unit) {
            return new OrgUnitResponse(
                    unit.id().value(),
                    unit.name(),
                    unit.parentId() == null ? null : unit.parentId().value());
        }
    }

    public record CreateLegalEntityRequest(@NotBlank String name, @NotBlank String primaryZone) {
    }

    public record LegalEntityResponse(UUID id, String name, String primaryZone) {

        public static LegalEntityResponse from(LegalEntity entity) {
            return new LegalEntityResponse(entity.id().value(), entity.name(), entity.primaryZone().getId());
        }
    }

    public record CreateWorkLocationRequest(@NotBlank String name, @NotBlank String zoneId) {
    }

    public record WorkLocationResponse(UUID id, String name, String zoneId) {

        public static WorkLocationResponse from(WorkLocation location) {
            return new WorkLocationResponse(location.id().value(), location.name(), location.zoneId().getId());
        }
    }
}
