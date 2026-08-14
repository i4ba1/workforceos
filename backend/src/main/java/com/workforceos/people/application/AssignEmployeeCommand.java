package com.workforceos.people.application;

import com.workforceos.shared.id.EmployeeId;
import com.workforceos.shared.id.OrgUnitId;
import com.workforceos.shared.id.PolicyId;

import java.time.LocalDate;

/** Command to create an employment assignment. */
public record AssignEmployeeCommand(
        EmployeeId employeeId,
        OrgUnitId orgUnitId,
        EmployeeId managerId,
        PolicyId policyId,
        LocalDate effectiveFrom,
        LocalDate effectiveTo) {
}
