package com.workforceos.people.adapter.outbound.persistence;

import com.workforceos.people.domain.Employee;
import com.workforceos.people.domain.EmployeeReader;
import com.workforceos.people.domain.EmployeeWriter;
import com.workforceos.people.domain.EmploymentAssignment;
import com.workforceos.people.domain.EmploymentStatus;
import com.workforceos.shared.id.EmployeeId;
import com.workforceos.shared.id.OrgUnitId;
import com.workforceos.shared.id.PolicyId;
import com.workforceos.shared.id.TenantId;
import com.workforceos.shared.id.UserId;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/** Maps between people aggregates and their JPA representations. */
@Repository
public class PeoplePersistenceAdapter implements EmployeeReader, EmployeeWriter {

    private final EmployeeJpaRepository employeeRepository;
    private final EmploymentAssignmentJpaRepository assignmentRepository;

    public PeoplePersistenceAdapter(EmployeeJpaRepository employeeRepository,
                                    EmploymentAssignmentJpaRepository assignmentRepository) {
        this.employeeRepository = employeeRepository;
        this.assignmentRepository = assignmentRepository;
    }

    @Override
    public Optional<Employee> findById(TenantId tenantId, EmployeeId id) {
        return employeeRepository.findByTenantIdAndId(tenantId.value(), id.value()).map(this::toDomain);
    }

    @Override
    public Optional<Employee> findByEmployeeNo(TenantId tenantId, String employeeNo) {
        return employeeRepository.findByTenantIdAndEmployeeNo(tenantId.value(), employeeNo).map(this::toDomain);
    }

    @Override
    public List<Employee> findAll(TenantId tenantId) {
        return employeeRepository.findAllByTenantId(tenantId.value()).stream().map(this::toDomain).toList();
    }

    @Override
    public List<EmploymentAssignment> findAssignments(TenantId tenantId, EmployeeId employeeId) {
        return assignmentRepository.findAllByTenantIdAndEmployeeId(tenantId.value(), employeeId.value())
                .stream().map(this::toDomain).toList();
    }

    @Override
    public Employee save(Employee employee) {
        EmployeeJpaEntity entity = new EmployeeJpaEntity(
                employee.id().value(),
                employee.tenantId().value(),
                employee.employeeNo(),
                employee.firstName(),
                employee.lastName(),
                employee.email(),
                employee.status().name(),
                employee.linkedUserId() == null ? null : employee.linkedUserId().value());
        return toDomain(employeeRepository.save(entity));
    }

    @Override
    public void saveAssignment(EmploymentAssignment assignment) {
        EmploymentAssignmentJpaEntity entity = new EmploymentAssignmentJpaEntity(
                UUID.randomUUID(),
                assignment.tenantId().value(),
                assignment.employeeId().value(),
                assignment.orgUnitId().value(),
                assignment.managerId() == null ? null : assignment.managerId().value(),
                assignment.policyId() == null ? null : assignment.policyId().value(),
                assignment.effectiveFrom(),
                assignment.effectiveTo());
        assignmentRepository.save(entity);
    }

    private Employee toDomain(EmployeeJpaEntity entity) {
        return new Employee(
                new EmployeeId(entity.getId()),
                new TenantId(entity.getTenantId()),
                entity.getEmployeeNo(),
                entity.getFirstName(),
                entity.getLastName(),
                entity.getEmail(),
                EmploymentStatus.valueOf(entity.getStatus()),
                entity.getLinkedUserId() == null ? null : new UserId(entity.getLinkedUserId()));
    }

    private EmploymentAssignment toDomain(EmploymentAssignmentJpaEntity entity) {
        return new EmploymentAssignment(
                new EmployeeId(entity.getEmployeeId()),
                new TenantId(entity.getTenantId()),
                new OrgUnitId(entity.getOrgUnitId()),
                entity.getManagerId() == null ? null : new EmployeeId(entity.getManagerId()),
                entity.getPolicyId() == null ? null : new PolicyId(entity.getPolicyId()),
                entity.getEffectiveFrom(),
                entity.getEffectiveTo());
    }
}
