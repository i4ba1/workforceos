package com.workforceos.shared.id;

import java.util.UUID;

/** Pay period identifier value object. */
public record PayPeriodId(UUID value) {

    public static PayPeriodId of(String value) {
        return new PayPeriodId(UUID.fromString(value));
    }

    public static PayPeriodId newId() {
        return new PayPeriodId(UUID.randomUUID());
    }
}
