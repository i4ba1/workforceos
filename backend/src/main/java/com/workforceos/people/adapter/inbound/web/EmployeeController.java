package com.workforceos.people.adapter.inbound.web;

import com.workforceos.people.adapter.inbound.web.EmployeeDtos.AssignmentResponse;
import com.workforceos.people.adapter.inbound.web.EmployeeDtos.CreateAssignmentRequest;
import com.workforceos.people.adapter.inbound.web.EmployeeDtos.CreateEmployeeRequest;
import com.workforceos.people.adapter.inbound.web.EmployeeDtos.EmployeeResponse;
import com.workforceos.people.application.AssignEmployeeCommand;
import com.workforceos.people.application.EmployeeService;
import com.workforceos.shared.context.TenantContextHolder;
import com.workforceos.shared.id.EmployeeId;
import com.workforceos.shared.id.OrgUnitId;
import com.workforceos.shared.id.PolicyId;
import com.workforceos.shared.id.TenantId;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/employees")
public class EmployeeController {

    private final EmployeeService employeeService;

    public EmployeeController(EmployeeService employeeService) {
        this.employeeService = employeeService;
    }

    @GetMapping
    public List<EmployeeResponse> list() {
        TenantId tenantId = TenantContextHolder.require().tenantId();
        return employeeService.list(tenantId).stream().map(EmployeeResponse::from).toList();
    }

    @PostMapping
    public EmployeeResponse create(@Valid @RequestBody CreateEmployeeRequest request) {
        TenantId tenantId = TenantContextHolder.require().tenantId();
        var employee = employeeService.create(
                tenantId,
                request.employeeNo(),
                request.firstName(),
                request.lastName(),
                request.email());
        return EmployeeResponse.from(employee);
    }

    @GetMapping("/{id}")
    public EmployeeResponse get(@PathVariable UUID id) {
        TenantId tenantId = TenantContextHolder.require().tenantId();
        return EmployeeResponse.from(employeeService.get(tenantId, new EmployeeId(id)));
    }

    @PostMapping("/{id}/assignments")
    public AssignmentResponse assign(@PathVariable UUID id, @Valid @RequestBody CreateAssignmentRequest request) {
        TenantId tenantId = TenantContextHolder.require().tenantId();
        var assignment = employeeService.assign(tenantId, new AssignEmployeeCommand(
                new EmployeeId(id),
                new OrgUnitId(request.orgUnitId()),
                request.managerId() == null ? null : new EmployeeId(request.managerId()),
                request.policyId() == null ? null : new PolicyId(request.policyId()),
                request.effectiveFrom(),
                request.effectiveTo()));
        return AssignmentResponse.from(assignment);
    }

    @GetMapping("/{id}/assignments")
    public List<AssignmentResponse> assignments(@PathVariable UUID id) {
        TenantId tenantId = TenantContextHolder.require().tenantId();
        return employeeService.assignments(tenantId, new EmployeeId(id)).stream()
                .map(AssignmentResponse::from).toList();
    }
}
