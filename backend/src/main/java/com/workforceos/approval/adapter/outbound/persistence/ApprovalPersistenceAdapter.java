package com.workforceos.approval.adapter.outbound.persistence;

import com.workforceos.approval.domain.ApprovalAction;
import com.workforceos.approval.domain.ApprovalCase;
import com.workforceos.approval.domain.ApprovalCaseStore;
import com.workforceos.approval.domain.ApprovalDecision;
import com.workforceos.approval.domain.ApprovalState;
import com.workforceos.shared.id.ApprovalCaseId;
import com.workforceos.shared.id.TenantId;
import com.workforceos.shared.id.UserId;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/** Maps between approval aggregates and their JPA representations. */
@Repository
public class ApprovalPersistenceAdapter implements ApprovalCaseStore {

    private final ApprovalCaseJpaRepository caseRepository;
    private final ApprovalActionJpaRepository actionRepository;

    public ApprovalPersistenceAdapter(ApprovalCaseJpaRepository caseRepository,
                                      ApprovalActionJpaRepository actionRepository) {
        this.caseRepository = caseRepository;
        this.actionRepository = actionRepository;
    }

    @Override
    public Optional<ApprovalCase> findById(TenantId tenantId, ApprovalCaseId id) {
        return caseRepository.findByTenantIdAndId(tenantId.value(), id.value()).map(this::toDomain);
    }

    @Override
    public List<ApprovalCase> findOpen(TenantId tenantId) {
        return caseRepository.findAllByTenantIdAndStateOrderByOpenedAtAsc(tenantId.value(), ApprovalState.OPEN.name())
                .stream().map(this::toDomain).toList();
    }

    @Override
    public ApprovalCase save(ApprovalCase approvalCase) {
        ApprovalCaseJpaEntity entity = caseRepository.findById(approvalCase.id().value())
                .orElseGet(ApprovalCaseJpaEntity::new);
        entity.setId(approvalCase.id().value());
        entity.setTenantId(approvalCase.tenantId().value());
        entity.setSubjectType(approvalCase.subjectType());
        entity.setSubjectId(approvalCase.subjectId());
        entity.setOpenedBy(approvalCase.openedBy().value());
        entity.setOpenedAt(approvalCase.openedAt());
        entity.setReason(approvalCase.reason());
        entity.setState(approvalCase.state().name());
        entity.setVersion(approvalCase.version());
        return toDomain(caseRepository.save(entity));
    }

    @Override
    public void saveAction(ApprovalAction action) {
        actionRepository.save(new ApprovalActionJpaEntity(
                UUID.randomUUID(),
                action.tenantId().value(),
                action.caseId().value(),
                action.actorId().value(),
                action.decision().name(),
                action.reason(),
                action.actedAt(),
                action.caseVersion()));
    }

    @Override
    public List<ApprovalAction> findActions(TenantId tenantId, ApprovalCaseId caseId) {
        return actionRepository.findAllByTenantIdAndCaseId(tenantId.value(), caseId.value()).stream()
                .map(this::toDomain).toList();
    }

    private ApprovalCase toDomain(ApprovalCaseJpaEntity entity) {
        return new ApprovalCase(
                new ApprovalCaseId(entity.getId()),
                new TenantId(entity.getTenantId()),
                entity.getSubjectType(),
                entity.getSubjectId(),
                new UserId(entity.getOpenedBy()),
                entity.getOpenedAt(),
                entity.getReason(),
                ApprovalState.valueOf(entity.getState()),
                entity.getVersion());
    }

    private ApprovalAction toDomain(ApprovalActionJpaEntity entity) {
        return new ApprovalAction(
                new ApprovalCaseId(entity.getCaseId()),
                new TenantId(entity.getTenantId()),
                new UserId(entity.getActorId()),
                ApprovalDecision.valueOf(entity.getDecision()),
                entity.getReason(),
                entity.getActedAt(),
                entity.getCaseVersion());
    }
}
