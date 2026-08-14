package com.workforceos.people.adapter.outbound.persistence;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.LocalDate;
import java.util.UUID;

/** JPA mapping of the employment assignment (surrogate id for persistence). */
@Entity
@Table(name = "employment_assignment")
public class EmploymentAssignmentJpaEntity {

    @Id
    @Column(name = "id", nullable = false)
    private UUID id;

    @Column(name = "tenant_id", nullable = false)
    private UUID tenantId;

    @Column(name = "employee_id", nullable = false)
    private UUID employeeId;

    @Column(name = "org_unit_id", nullable = false)
    private UUID orgUnitId;

    @Column(name = "manager_id")
    private UUID managerId;

    @Column(name = "policy_id")
    private UUID policyId;

    @Column(name = "effective_from", nullable = false)
    private LocalDate effectiveFrom;

    @Column(name = "effective_to")
    private LocalDate effectiveTo;

    protected EmploymentAssignmentJpaEntity() {
    }

    public EmploymentAssignmentJpaEntity(UUID id, UUID tenantId, UUID employeeId, UUID orgUnitId,
                                         UUID managerId, UUID policyId, LocalDate effectiveFrom, LocalDate effectiveTo) {
        this.id = id;
        this.tenantId = tenantId;
        this.employeeId = employeeId;
        this.orgUnitId = orgUnitId;
        this.managerId = managerId;
        this.policyId = policyId;
        this.effectiveFrom = effectiveFrom;
        this.effectiveTo = effectiveTo;
    }

    public UUID getId() {
        return id;
    }

    public UUID getTenantId() {
        return tenantId;
    }

    public UUID getEmployeeId() {
        return employeeId;
    }

    public UUID getOrgUnitId() {
        return orgUnitId;
    }

    public UUID getManagerId() {
        return managerId;
    }

    public UUID getPolicyId() {
        return policyId;
    }

    public LocalDate getEffectiveFrom() {
        return effectiveFrom;
    }

    public LocalDate getEffectiveTo() {
        return effectiveTo;
    }
}
