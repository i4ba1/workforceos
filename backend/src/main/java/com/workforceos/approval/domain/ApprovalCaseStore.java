package com.workforceos.approval.domain;

import com.workforceos.shared.id.ApprovalCaseId;
import com.workforceos.shared.id.TenantId;

import java.util.List;
import java.util.Optional;

/** Persistence port for approval cases and their decision actions. */
public interface ApprovalCaseStore {

    Optional<ApprovalCase> findById(TenantId tenantId, ApprovalCaseId id);

    List<ApprovalCase> findOpen(TenantId tenantId);

    ApprovalCase save(ApprovalCase approvalCase);

    void saveAction(ApprovalAction action);

    List<ApprovalAction> findActions(TenantId tenantId, ApprovalCaseId caseId);
}
