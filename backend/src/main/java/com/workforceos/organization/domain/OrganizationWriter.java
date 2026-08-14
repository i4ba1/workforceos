package com.workforceos.organization.domain;

/** Write-side port for the organization structure. */
public interface OrganizationWriter {

    OrgUnit saveOrgUnit(OrgUnit unit);

    LegalEntity saveLegalEntity(LegalEntity entity);

    WorkLocation saveWorkLocation(WorkLocation location);
}
