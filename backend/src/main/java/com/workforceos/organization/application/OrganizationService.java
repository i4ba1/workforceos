package com.workforceos.organization.application;

import com.workforceos.organization.domain.LegalEntity;
import com.workforceos.organization.domain.OrgUnit;
import com.workforceos.organization.domain.OrganizationReader;
import com.workforceos.organization.domain.OrganizationWriter;
import com.workforceos.organization.domain.WorkLocation;
import com.workforceos.shared.error.NotFoundException;
import com.workforceos.shared.id.LegalEntityId;
import com.workforceos.shared.id.OrgUnitId;
import com.workforceos.shared.id.TenantId;
import com.workforceos.shared.id.WorkLocationId;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.ZoneId;
import java.util.List;

/** Use-cases for maintaining legal entities, org units and work locations. */
@Service
public class OrganizationService {

    private final OrganizationReader reader;
    private final OrganizationWriter writer;

    public OrganizationService(OrganizationReader reader, OrganizationWriter writer) {
        this.reader = reader;
        this.writer = writer;
    }

    @Transactional
    public OrgUnit createOrgUnit(TenantId tenantId, String name, OrgUnitId parentId) {
        return writer.saveOrgUnit(new OrgUnit(OrgUnitId.newId(), tenantId, name, parentId));
    }

    @Transactional(readOnly = true)
    public List<OrgUnit> listOrgUnits(TenantId tenantId) {
        return reader.findOrgUnits(tenantId);
    }

    @Transactional(readOnly = true)
    public OrgUnit getOrgUnit(TenantId tenantId, OrgUnitId id) {
        return reader.findOrgUnit(tenantId, id)
                .orElseThrow(() -> new NotFoundException("org_unit.not_found", "Org unit not found: " + id));
    }

    @Transactional
    public LegalEntity createLegalEntity(TenantId tenantId, String name, ZoneId primaryZone) {
        return writer.saveLegalEntity(new LegalEntity(LegalEntityId.newId(), tenantId, name, primaryZone));
    }

    @Transactional(readOnly = true)
    public List<LegalEntity> listLegalEntities(TenantId tenantId) {
        return reader.findLegalEntities(tenantId);
    }

    @Transactional
    public WorkLocation createWorkLocation(TenantId tenantId, String name, ZoneId zoneId) {
        return writer.saveWorkLocation(new WorkLocation(WorkLocationId.newId(), tenantId, name, zoneId));
    }

    @Transactional(readOnly = true)
    public List<WorkLocation> listWorkLocations(TenantId tenantId) {
        return reader.findWorkLocations(tenantId);
    }
}
