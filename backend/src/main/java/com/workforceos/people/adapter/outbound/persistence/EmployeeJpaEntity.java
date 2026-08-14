package com.workforceos.people.adapter.outbound.persistence;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.util.UUID;

/** JPA mapping of the employee aggregate. */
@Entity
@Table(name = "employee")
public class EmployeeJpaEntity {

    @Id
    @Column(name = "id", nullable = false)
    private UUID id;

    @Column(name = "tenant_id", nullable = false)
    private UUID tenantId;

    @Column(name = "employee_no", nullable = false)
    private String employeeNo;

    @Column(name = "first_name", nullable = false)
    private String firstName;

    @Column(name = "last_name", nullable = false)
    private String lastName;

    @Column(name = "email")
    private String email;

    @Column(name = "status", nullable = false)
    private String status;

    @Column(name = "linked_user_id")
    private UUID linkedUserId;

    protected EmployeeJpaEntity() {
    }

    public EmployeeJpaEntity(UUID id, UUID tenantId, String employeeNo, String firstName, String lastName,
                             String email, String status, UUID linkedUserId) {
        this.id = id;
        this.tenantId = tenantId;
        this.employeeNo = employeeNo;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.status = status;
        this.linkedUserId = linkedUserId;
    }

    public UUID getId() {
        return id;
    }

    public UUID getTenantId() {
        return tenantId;
    }

    public String getEmployeeNo() {
        return employeeNo;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getEmail() {
        return email;
    }

    public String getStatus() {
        return status;
    }

    public UUID getLinkedUserId() {
        return linkedUserId;
    }
}
