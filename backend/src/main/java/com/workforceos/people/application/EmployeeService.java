package com.workforceos.people.application;

import com.workforceos.people.domain.Employee;
import com.workforceos.people.domain.EmployeeReader;
import com.workforceos.people.domain.EmployeeWriter;
import com.workforceos.people.domain.EmploymentAssignment;
import com.workforceos.shared.error.ConflictException;
import com.workforceos.shared.error.NotFoundException;
import com.workforceos.shared.id.EmployeeId;
import com.workforceos.shared.id.TenantId;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/** Use-cases for employee administration and employment assignment. */
@Service
public class EmployeeService {

    private final EmployeeReader reader;
    private final EmployeeWriter writer;

    public EmployeeService(EmployeeReader reader, EmployeeWriter writer) {
        this.reader = reader;
        this.writer = writer;
    }

    @Transactional
    public Employee create(TenantId tenantId, String employeeNo, String firstName, String lastName, String email) {
        reader.findByEmployeeNo(tenantId, employeeNo).ifPresent(ignored -> {
            throw new ConflictException("employee.number_taken", "Employee number already exists: " + employeeNo);
        });
        Employee employee = new Employee(EmployeeId.newId(), tenantId, employeeNo, firstName, lastName);
        if (email != null && !email.isBlank()) {
            employee.assignEmail(email);
        }
        return writer.save(employee);
    }

    @Transactional(readOnly = true)
    public Employee get(TenantId tenantId, EmployeeId id) {
        return reader.findById(tenantId, id)
                .orElseThrow(() -> new NotFoundException("employee.not_found", "Employee not found: " + id));
    }

    @Transactional(readOnly = true)
    public List<Employee> list(TenantId tenantId) {
        return reader.findAll(tenantId);
    }

    @Transactional
    public EmploymentAssignment assign(TenantId tenantId, AssignEmployeeCommand command) {
        get(tenantId, command.employeeId());
        EmploymentAssignment assignment = new EmploymentAssignment(
                command.employeeId(),
                tenantId,
                command.orgUnitId(),
                command.managerId(),
                command.policyId(),
                command.effectiveFrom(),
                command.effectiveTo());
        writer.saveAssignment(assignment);
        return assignment;
    }

    @Transactional(readOnly = true)
    public List<EmploymentAssignment> assignments(TenantId tenantId, EmployeeId employeeId) {
        return reader.findAssignments(tenantId, employeeId);
    }
}
