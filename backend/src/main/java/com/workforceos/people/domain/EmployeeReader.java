package com.workforceos.people.domain;

import com.workforceos.shared.id.EmployeeId;
import com.workforceos.shared.id.TenantId;

import java.util.List;
import java.util.Optional;

/** Read-side port for employee and assignment queries. */
public interface EmployeeReader {

    Optional<Employee> findById(TenantId tenantId, EmployeeId id);

    Optional<Employee> findByEmployeeNo(TenantId tenantId, String employeeNo);

    List<Employee> findAll(TenantId tenantId);

    List<EmploymentAssignment> findAssignments(TenantId tenantId, EmployeeId employeeId);
}
