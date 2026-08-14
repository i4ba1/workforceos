package com.workforceos.approval.domain.event;

import com.workforceos.approval.domain.ApprovalState;
import com.workforceos.shared.id.ApprovalCaseId;
import com.workforceos.shared.id.TenantId;

/**
 * Published when a manager/HR decision finalizes an approval case.
 *
 * <p>Consumers: attendance finalization and notification.</p>
 */
public record ApprovalCompleted(TenantId tenantId, ApprovalCaseId caseId, ApprovalState state) {
}
