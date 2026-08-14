package com.workforceos.shared.id;

import java.util.UUID;

/** Payroll export identifier value object. */
public record PayrollExportId(UUID value) {

    public static PayrollExportId of(String value) {
        return new PayrollExportId(UUID.fromString(value));
    }

    public static PayrollExportId newId() {
        return new PayrollExportId(UUID.randomUUID());
    }
}
