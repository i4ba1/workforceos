package com.workforceos.shared.id;

import java.util.UUID;

/** Leave request identifier value object. */
public record LeaveRequestId(UUID value) {

    public static LeaveRequestId of(String value) {
        return new LeaveRequestId(UUID.fromString(value));
    }

    public static LeaveRequestId newId() {
        return new LeaveRequestId(UUID.randomUUID());
    }
}
