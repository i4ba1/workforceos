package com.workforceos.approval.adapter.outbound.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

interface ApprovalCaseJpaRepository extends JpaRepository<ApprovalCaseJpaEntity, UUID> {

    Optional<ApprovalCaseJpaEntity> findByTenantIdAndId(UUID tenantId, UUID id);

    List<ApprovalCaseJpaEntity> findAllByTenantIdAndStateOrderByOpenedAtAsc(UUID tenantId, String state);
}

interface ApprovalActionJpaRepository extends JpaRepository<ApprovalActionJpaEntity, UUID> {

    List<ApprovalActionJpaEntity> findAllByTenantIdAndCaseId(UUID tenantId, UUID caseId);
}
