package com.workforceos.organization.adapter.outbound.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

interface LegalEntityJpaRepository extends JpaRepository<LegalEntityJpaEntity, UUID> {

    List<LegalEntityJpaEntity> findAllByTenantId(UUID tenantId);

    Optional<LegalEntityJpaEntity> findByTenantIdAndId(UUID tenantId, UUID id);
}

interface OrgUnitJpaRepository extends JpaRepository<OrgUnitJpaEntity, UUID> {

    List<OrgUnitJpaEntity> findAllByTenantId(UUID tenantId);

    Optional<OrgUnitJpaEntity> findByTenantIdAndId(UUID tenantId, UUID id);
}

interface WorkLocationJpaRepository extends JpaRepository<WorkLocationJpaEntity, UUID> {

    List<WorkLocationJpaEntity> findAllByTenantId(UUID tenantId);

    Optional<WorkLocationJpaEntity> findByTenantIdAndId(UUID tenantId, UUID id);
}
