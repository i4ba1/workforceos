package com.workforceos.shared.id;

import java.util.UUID;

/** Approval case identifier value object. */
public record ApprovalCaseId(UUID value) {

    public static ApprovalCaseId of(String value) {
        return new ApprovalCaseId(UUID.fromString(value));
    }

    public static ApprovalCaseId newId() {
        return new ApprovalCaseId(UUID.randomUUID());
    }
}
