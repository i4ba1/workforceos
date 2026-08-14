package com.workforceos.people.domain;

import com.workforceos.shared.id.EmployeeId;
import com.workforceos.shared.id.TenantId;
import com.workforceos.shared.id.UserId;

import java.util.Objects;

/**
 * An employee profile.
 *
 * <p>The employee number is unique within a tenant (enforced by a composite unique
 * constraint at persistence time). Identity data is classified and must not leak into
 * application logs.</p>
 */
public class Employee {

    private final EmployeeId id;
    private final TenantId tenantId;
    private final String employeeNo;
    private String firstName;
    private String lastName;
    private String email;
    private EmploymentStatus status;
    private UserId linkedUserId;

    public Employee(EmployeeId id, TenantId tenantId, String employeeNo, String firstName, String lastName) {
        this(id, tenantId, employeeNo, firstName, lastName, null, EmploymentStatus.ACTIVE, null);
    }

    public Employee(EmployeeId id, TenantId tenantId, String employeeNo, String firstName, String lastName,
                    String email, EmploymentStatus status, UserId linkedUserId) {
        this.id = Objects.requireNonNull(id, "id");
        this.tenantId = Objects.requireNonNull(tenantId, "tenantId");
        this.employeeNo = Objects.requireNonNull(employeeNo, "employeeNo");
        this.firstName = Objects.requireNonNull(firstName, "firstName");
        this.lastName = Objects.requireNonNull(lastName, "lastName");
        this.email = email;
        this.status = Objects.requireNonNull(status, "status");
        this.linkedUserId = linkedUserId;
    }

    public void terminate() {
        this.status = EmploymentStatus.TERMINATED;
    }

    public void linkUser(UserId userId) {
        this.linkedUserId = Objects.requireNonNull(userId, "userId");
    }

    public void assignEmail(String email) {
        this.email = email;
    }

    public void rename(String newFirstName, String newLastName) {
        this.firstName = Objects.requireNonNull(newFirstName, "newFirstName");
        this.lastName = Objects.requireNonNull(newLastName, "newLastName");
    }

    public EmployeeId id() {
        return id;
    }

    public TenantId tenantId() {
        return tenantId;
    }

    public String employeeNo() {
        return employeeNo;
    }

    public String firstName() {
        return firstName;
    }

    public String lastName() {
        return lastName;
    }

    public String email() {
        return email;
    }

    public EmploymentStatus status() {
        return status;
    }

    public UserId linkedUserId() {
        return linkedUserId;
    }
}
