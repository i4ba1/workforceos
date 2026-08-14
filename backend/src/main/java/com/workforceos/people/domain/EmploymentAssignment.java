package com.workforceos.people.domain;

import com.workforceos.shared.id.EmployeeId;
import com.workforceos.shared.id.OrgUnitId;
import com.workforceos.shared.id.PolicyId;
import com.workforceos.shared.id.TenantId;

import java.time.LocalDate;
import java.util.Objects;

/**
 * An effective-dated employment assignment.
 *
 * <p>An employee may have multiple assignments over time; the assignment in effect for
 * a given business date is resolved by effective range. Holds the reporting manager and
 * the assigned attendance policy.</p>
 */
public class EmploymentAssignment {

    private final EmployeeId employeeId;
    private final TenantId tenantId;
    private final OrgUnitId orgUnitId;
    private final EmployeeId managerId;
    private final PolicyId policyId;
    private final LocalDate effectiveFrom;
    private final LocalDate effectiveTo;

    public EmploymentAssignment(EmployeeId employeeId, TenantId tenantId, OrgUnitId orgUnitId,
                                EmployeeId managerId, PolicyId policyId,
                                LocalDate effectiveFrom, LocalDate effectiveTo) {
        this.employeeId = Objects.requireNonNull(employeeId, "employeeId");
        this.tenantId = Objects.requireNonNull(tenantId, "tenantId");
        this.orgUnitId = Objects.requireNonNull(orgUnitId, "orgUnitId");
        this.managerId = managerId;
        this.policyId = Objects.requireNonNull(policyId, "policyId");
        this.effectiveFrom = Objects.requireNonNull(effectiveFrom, "effectiveFrom");
        this.effectiveTo = effectiveTo;
        if (effectiveTo != null && !effectiveTo.isAfter(effectiveFrom)) {
            throw new IllegalArgumentException("effectiveTo must be after effectiveFrom");
        }
    }

    public boolean effectiveOn(LocalDate date) {
        return !date.isBefore(effectiveFrom) && (effectiveTo == null || !date.isAfter(effectiveTo));
    }

    public EmployeeId employeeId() {
        return employeeId;
    }

    public TenantId tenantId() {
        return tenantId;
    }

    public OrgUnitId orgUnitId() {
        return orgUnitId;
    }

    public EmployeeId managerId() {
        return managerId;
    }

    public PolicyId policyId() {
        return policyId;
    }

    public LocalDate effectiveFrom() {
        return effectiveFrom;
    }

    public LocalDate effectiveTo() {
        return effectiveTo;
    }
}
