package com.workforceos.approval.domain.event;

import com.workforceos.shared.id.ApprovalCaseId;
import com.workforceos.shared.id.TenantId;

import java.util.UUID;

/**
 * Published when a manager/HR decision finalizes an approval case.
 *
 * <p>Carries the subject reference and outcome so consumers (e.g. attendance) can react
 * without importing the approval module's domain types.</p>
 */
public record ApprovalCompleted(
        TenantId tenantId,
        ApprovalCaseId caseId,
        boolean approved,
        String subjectType,
        UUID subjectId) {
}
