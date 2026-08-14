package com.workforceos.organization.domain;

import com.workforceos.shared.id.LegalEntityId;
import com.workforceos.shared.id.OrgUnitId;
import com.workforceos.shared.id.TenantId;
import com.workforceos.shared.id.WorkLocationId;

import java.util.List;
import java.util.Optional;

/** Read-side port for the organization structure. */
public interface OrganizationReader {

    Optional<OrgUnit> findOrgUnit(TenantId tenantId, OrgUnitId id);

    List<OrgUnit> findOrgUnits(TenantId tenantId);

    Optional<LegalEntity> findLegalEntity(TenantId tenantId, LegalEntityId id);

    List<LegalEntity> findLegalEntities(TenantId tenantId);

    Optional<WorkLocation> findWorkLocation(TenantId tenantId, WorkLocationId id);

    List<WorkLocation> findWorkLocations(TenantId tenantId);
}
