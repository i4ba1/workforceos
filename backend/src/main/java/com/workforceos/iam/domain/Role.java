package com.workforceos.iam.domain;

/** Coarse-grained roles; capabilities are granted through these bindings. */
public enum Role {
    EMPLOYEE,
    MANAGER,
    HR_ADMIN,
    PAYROLL_ADMIN,
    TENANT_ADMIN,
    SYSTEM_OPERATOR
}
