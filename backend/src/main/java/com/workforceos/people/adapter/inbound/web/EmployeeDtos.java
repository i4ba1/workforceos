package com.workforceos.people.adapter.inbound.web;

import com.workforceos.people.domain.Employee;
import com.workforceos.people.domain.EmploymentAssignment;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;
import java.util.UUID;

/** Web DTOs for employee endpoints. */
public final class EmployeeDtos {

    private EmployeeDtos() {
    }

    public record CreateEmployeeRequest(
            @NotBlank String employeeNo,
            @NotBlank String firstName,
            @NotBlank String lastName,
            @Email String email) {
    }

    public record EmployeeResponse(
            UUID id,
            String employeeNo,
            String firstName,
            String lastName,
            String email,
            String status) {

        public static EmployeeResponse from(Employee employee) {
            return new EmployeeResponse(
                    employee.id().value(),
                    employee.employeeNo(),
                    employee.firstName(),
                    employee.lastName(),
                    employee.email(),
                    employee.status().name());
        }
    }

    public record CreateAssignmentRequest(
            @NotNull UUID orgUnitId,
            UUID managerId,
            UUID policyId,
            @NotNull LocalDate effectiveFrom,
            LocalDate effectiveTo) {
    }

    public record AssignmentResponse(
            UUID employeeId,
            UUID orgUnitId,
            UUID managerId,
            UUID policyId,
            LocalDate effectiveFrom,
            LocalDate effectiveTo) {

        public static AssignmentResponse from(EmploymentAssignment assignment) {
            return new AssignmentResponse(
                    assignment.employeeId().value(),
                    assignment.orgUnitId().value(),
                    assignment.managerId() == null ? null : assignment.managerId().value(),
                    assignment.policyId() == null ? null : assignment.policyId().value(),
                    assignment.effectiveFrom(),
                    assignment.effectiveTo());
        }
    }
}
