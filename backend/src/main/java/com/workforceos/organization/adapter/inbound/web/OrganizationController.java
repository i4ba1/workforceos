package com.workforceos.organization.adapter.inbound.web;

import com.workforceos.organization.adapter.inbound.web.OrganizationDtos.CreateLegalEntityRequest;
import com.workforceos.organization.adapter.inbound.web.OrganizationDtos.CreateOrgUnitRequest;
import com.workforceos.organization.adapter.inbound.web.OrganizationDtos.CreateWorkLocationRequest;
import com.workforceos.organization.adapter.inbound.web.OrganizationDtos.LegalEntityResponse;
import com.workforceos.organization.adapter.inbound.web.OrganizationDtos.OrgUnitResponse;
import com.workforceos.organization.adapter.inbound.web.OrganizationDtos.WorkLocationResponse;
import com.workforceos.organization.application.OrganizationService;
import com.workforceos.shared.context.TenantContextHolder;
import com.workforceos.shared.id.OrgUnitId;
import com.workforceos.shared.id.TenantId;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.ZoneId;
import java.util.List;

@RestController
@RequestMapping("/api/v1")
public class OrganizationController {

    private final OrganizationService organizationService;

    public OrganizationController(OrganizationService organizationService) {
        this.organizationService = organizationService;
    }

    @GetMapping("/org-units")
    public List<OrgUnitResponse> listOrgUnits() {
        TenantId tenantId = TenantContextHolder.require().tenantId();
        return organizationService.listOrgUnits(tenantId).stream().map(OrgUnitResponse::from).toList();
    }

    @PostMapping("/org-units")
    public OrgUnitResponse createOrgUnit(@Valid @RequestBody CreateOrgUnitRequest request) {
        TenantId tenantId = TenantContextHolder.require().tenantId();
        var unit = organizationService.createOrgUnit(
                tenantId,
                request.name(),
                request.parentId() == null ? null : new OrgUnitId(request.parentId()));
        return OrgUnitResponse.from(unit);
    }

    @GetMapping("/legal-entities")
    public List<LegalEntityResponse> listLegalEntities() {
        TenantId tenantId = TenantContextHolder.require().tenantId();
        return organizationService.listLegalEntities(tenantId).stream().map(LegalEntityResponse::from).toList();
    }

    @PostMapping("/legal-entities")
    public LegalEntityResponse createLegalEntity(@Valid @RequestBody CreateLegalEntityRequest request) {
        TenantId tenantId = TenantContextHolder.require().tenantId();
        var entity = organizationService.createLegalEntity(tenantId, request.name(), ZoneId.of(request.primaryZone()));
        return LegalEntityResponse.from(entity);
    }

    @GetMapping("/work-locations")
    public List<WorkLocationResponse> listWorkLocations() {
        TenantId tenantId = TenantContextHolder.require().tenantId();
        return organizationService.listWorkLocations(tenantId).stream().map(WorkLocationResponse::from).toList();
    }

    @PostMapping("/work-locations")
    public WorkLocationResponse createWorkLocation(@Valid @RequestBody CreateWorkLocationRequest request) {
        TenantId tenantId = TenantContextHolder.require().tenantId();
        var location = organizationService.createWorkLocation(tenantId, request.name(), ZoneId.of(request.zoneId()));
        return WorkLocationResponse.from(location);
    }
}
