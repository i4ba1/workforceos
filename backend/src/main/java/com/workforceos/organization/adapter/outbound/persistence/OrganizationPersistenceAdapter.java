package com.workforceos.organization.adapter.outbound.persistence;

import com.workforceos.organization.domain.LegalEntity;
import com.workforceos.organization.domain.OrgUnit;
import com.workforceos.organization.domain.OrganizationReader;
import com.workforceos.organization.domain.OrganizationWriter;
import com.workforceos.organization.domain.WorkLocation;
import com.workforceos.shared.id.LegalEntityId;
import com.workforceos.shared.id.OrgUnitId;
import com.workforceos.shared.id.TenantId;
import com.workforceos.shared.id.WorkLocationId;
import org.springframework.stereotype.Repository;

import java.time.ZoneId;
import java.util.List;
import java.util.Optional;

/** Maps between organization aggregates and their JPA representations. */
@Repository
public class OrganizationPersistenceAdapter implements OrganizationReader, OrganizationWriter {

    private final OrgUnitJpaRepository orgUnitRepository;
    private final LegalEntityJpaRepository legalEntityRepository;
    private final WorkLocationJpaRepository workLocationRepository;

    public OrganizationPersistenceAdapter(OrgUnitJpaRepository orgUnitRepository,
                                          LegalEntityJpaRepository legalEntityRepository,
                                          WorkLocationJpaRepository workLocationRepository) {
        this.orgUnitRepository = orgUnitRepository;
        this.legalEntityRepository = legalEntityRepository;
        this.workLocationRepository = workLocationRepository;
    }

    @Override
    public Optional<OrgUnit> findOrgUnit(TenantId tenantId, OrgUnitId id) {
        return orgUnitRepository.findByTenantIdAndId(tenantId.value(), id.value()).map(this::toDomain);
    }

    @Override
    public List<OrgUnit> findOrgUnits(TenantId tenantId) {
        return orgUnitRepository.findAllByTenantId(tenantId.value()).stream().map(this::toDomain).toList();
    }

    @Override
    public Optional<LegalEntity> findLegalEntity(TenantId tenantId, LegalEntityId id) {
        return legalEntityRepository.findByTenantIdAndId(tenantId.value(), id.value()).map(this::toDomain);
    }

    @Override
    public List<LegalEntity> findLegalEntities(TenantId tenantId) {
        return legalEntityRepository.findAllByTenantId(tenantId.value()).stream().map(this::toDomain).toList();
    }

    @Override
    public Optional<WorkLocation> findWorkLocation(TenantId tenantId, WorkLocationId id) {
        return workLocationRepository.findByTenantIdAndId(tenantId.value(), id.value()).map(this::toDomain);
    }

    @Override
    public List<WorkLocation> findWorkLocations(TenantId tenantId) {
        return workLocationRepository.findAllByTenantId(tenantId.value()).stream().map(this::toDomain).toList();
    }

    @Override
    public OrgUnit saveOrgUnit(OrgUnit unit) {
        OrgUnitJpaEntity entity = new OrgUnitJpaEntity(
                unit.id().value(), unit.tenantId().value(), unit.name(),
                unit.parentId() == null ? null : unit.parentId().value());
        return toDomain(orgUnitRepository.save(entity));
    }

    @Override
    public LegalEntity saveLegalEntity(LegalEntity legalEntity) {
        LegalEntityJpaEntity entity = new LegalEntityJpaEntity(
                legalEntity.id().value(), legalEntity.tenantId().value(),
                legalEntity.name(), legalEntity.primaryZone().getId());
        return toDomain(legalEntityRepository.save(entity));
    }

    @Override
    public WorkLocation saveWorkLocation(WorkLocation location) {
        WorkLocationJpaEntity entity = new WorkLocationJpaEntity(
                location.id().value(), location.tenantId().value(),
                location.name(), location.zoneId().getId());
        return toDomain(workLocationRepository.save(entity));
    }

    private OrgUnit toDomain(OrgUnitJpaEntity entity) {
        return new OrgUnit(
                new OrgUnitId(entity.getId()), new TenantId(entity.getTenantId()), entity.getName(),
                entity.getParentId() == null ? null : new OrgUnitId(entity.getParentId()));
    }

    private LegalEntity toDomain(LegalEntityJpaEntity entity) {
        return new LegalEntity(
                new LegalEntityId(entity.getId()), new TenantId(entity.getTenantId()),
                entity.getName(), ZoneId.of(entity.getPrimaryZone()));
    }

    private WorkLocation toDomain(WorkLocationJpaEntity entity) {
        return new WorkLocation(
                new WorkLocationId(entity.getId()), new TenantId(entity.getTenantId()),
                entity.getName(), ZoneId.of(entity.getZoneId()));
    }
}
