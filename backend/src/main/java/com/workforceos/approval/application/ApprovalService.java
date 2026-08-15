package com.workforceos.approval.application;

import com.workforceos.approval.domain.ApprovalAction;
import com.workforceos.approval.domain.ApprovalCase;
import com.workforceos.approval.domain.ApprovalCaseStore;
import com.workforceos.approval.domain.ApprovalDecision;
import com.workforceos.approval.domain.ApprovalState;
import com.workforceos.approval.domain.event.ApprovalCompleted;
import com.workforceos.audit.domain.AuditEvent;
import com.workforceos.audit.domain.AuditWriter;
import com.workforceos.shared.error.ConflictException;
import com.workforceos.shared.error.NotFoundException;
import com.workforceos.shared.id.ApprovalCaseId;
import com.workforceos.shared.id.AuditEventId;
import com.workforceos.shared.id.TenantId;
import com.workforceos.shared.id.UserId;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

/**
 * Approval workflow: open cases, approve/reject with optimistic locking, and record an
 * immutable audit trail for every decision.
 */
@Service
public class ApprovalService {

    private final ApprovalCaseStore store;
    private final AuditWriter auditWriter;
    private final ApplicationEventPublisher eventPublisher;

    public ApprovalService(ApprovalCaseStore store, AuditWriter auditWriter, ApplicationEventPublisher eventPublisher) {
        this.store = store;
        this.auditWriter = auditWriter;
        this.eventPublisher = eventPublisher;
    }

    @Transactional
    public ApprovalCase open(TenantId tenantId, String subjectType, UUID subjectId, UserId openedBy, String reason) {
        ApprovalCase approvalCase = new ApprovalCase(ApprovalCaseId.newId(), tenantId, subjectType, subjectId,
                openedBy, Instant.now(), reason);
        ApprovalCase saved = store.save(approvalCase);
        auditWriter.append(audit(tenantId, openedBy, "approval.case_opened", "approval_case", saved.id().value(), null, null));
        return saved;
    }

    @Transactional
    public ApprovalCase approve(TenantId tenantId, ApprovalCaseId id, UserId actorId, long expectedVersion, String reason) {
        return decide(tenantId, id, actorId, ApprovalDecision.APPROVE, expectedVersion, reason);
    }

    @Transactional
    public ApprovalCase reject(TenantId tenantId, ApprovalCaseId id, UserId actorId, long expectedVersion, String reason) {
        return decide(tenantId, id, actorId, ApprovalDecision.REJECT, expectedVersion, reason);
    }

    @Transactional(readOnly = true)
    public List<ApprovalCase> queue(TenantId tenantId) {
        return store.findOpen(tenantId);
    }

    @Transactional(readOnly = true)
    public ApprovalCase get(TenantId tenantId, ApprovalCaseId id) {
        return store.findById(tenantId, id)
                .orElseThrow(() -> new NotFoundException("approval.not_found", "Approval case not found: " + id));
    }

    @Transactional(readOnly = true)
    public List<ApprovalAction> actions(TenantId tenantId, ApprovalCaseId id) {
        return store.findActions(tenantId, id);
    }

    private ApprovalCase decide(TenantId tenantId, ApprovalCaseId id, UserId actorId, ApprovalDecision decision,
                                long expectedVersion, String reason) {
        ApprovalCase approvalCase = get(tenantId, id);
        if (approvalCase.state() != ApprovalState.OPEN) {
            throw new ConflictException("approval.already_decided", "Approval case is already decided");
        }
        if (!approvalCase.decide(decision, expectedVersion)) {
            throw new ConflictException("approval.version_conflict",
                    "Approval case was updated concurrently; current version is " + approvalCase.version());
        }
        ApprovalCase saved = store.save(approvalCase);
        store.saveAction(new ApprovalAction(id, tenantId, actorId, decision, reason, Instant.now(), expectedVersion));
        auditWriter.append(audit(tenantId, actorId, "approval." + decision.name().toLowerCase(),
                "approval_case", id.value(), ApprovalState.OPEN.name(), saved.state().name()));
        eventPublisher.publishEvent(new ApprovalCompleted(
                tenantId, id, saved.state() == ApprovalState.APPROVED, saved.subjectType(), saved.subjectId()));
        return saved;
    }

    private AuditEvent audit(TenantId tenantId, UserId actorId, String action, String entityType, UUID entityId,
                             String before, String after) {
        return new AuditEvent(AuditEventId.newId(), tenantId, actorId, action, entityType, entityId, before, after,
                UUID.randomUUID().toString(), Instant.now());
    }
}
