package com.workforceos.approval.adapter.inbound.web;

import com.workforceos.approval.domain.ApprovalAction;
import com.workforceos.approval.domain.ApprovalCase;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;

import java.util.List;
import java.util.UUID;

/** Web DTOs for approval endpoints. */
public final class ApprovalDtos {

    private ApprovalDtos() {
    }

    public record OpenCaseRequest(
            @NotBlank String subjectType,
            @NotNull UUID subjectId,
            @NotBlank String reason) {
    }

    public record DecisionRequest(
            @PositiveOrZero long expectedVersion,
            @NotBlank String reason) {
    }

    public record ApprovalCaseResponse(
            UUID id,
            String subjectType,
            UUID subjectId,
            String state,
            long version,
            String reason,
            String openedAt,
            UUID openedBy) {

        public static ApprovalCaseResponse from(ApprovalCase approvalCase) {
            return new ApprovalCaseResponse(
                    approvalCase.id().value(),
                    approvalCase.subjectType(),
                    approvalCase.subjectId(),
                    approvalCase.state().name(),
                    approvalCase.version(),
                    approvalCase.reason(),
                    approvalCase.openedAt().toString(),
                    approvalCase.openedBy().value());
        }
    }

    public record ApprovalActionResponse(String decision, String reason, UUID actorId, String actedAt, long caseVersion) {

        public static ApprovalActionResponse from(ApprovalAction action) {
            return new ApprovalActionResponse(
                    action.decision().name(),
                    action.reason(),
                    action.actorId().value(),
                    action.actedAt().toString(),
                    action.caseVersion());
        }
    }

    public record ApprovalCaseDetailResponse(ApprovalCaseResponse approvalCase, List<ApprovalActionResponse> actions) {
    }
}
