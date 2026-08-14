package com.workforceos.leave.domain;

import com.workforceos.shared.id.EmployeeId;
import com.workforceos.shared.id.LeaveRequestId;
import com.workforceos.shared.id.TenantId;
import com.workforceos.shared.id.UserId;

import java.time.LocalDate;
import java.util.Objects;

/**
 * A leave request for an employee over an inclusive date range.
 *
 * <p>Approved requests produce {@link LeaveWindow}s that affect expected attendance.</p>
 */
public class LeaveRequest {

    private final LeaveRequestId id;
    private final TenantId tenantId;
    private final EmployeeId employeeId;
    private final LeaveType type;
    private final LocalDate startDate;
    private final LocalDate endDate;
    private LeaveRequestState state;
    private UserId decidedBy;

    public LeaveRequest(LeaveRequestId id, TenantId tenantId, EmployeeId employeeId, LeaveType type,
                        LocalDate startDate, LocalDate endDate) {
        this.id = Objects.requireNonNull(id, "id");
        this.tenantId = Objects.requireNonNull(tenantId, "tenantId");
        this.employeeId = Objects.requireNonNull(employeeId, "employeeId");
        this.type = Objects.requireNonNull(type, "type");
        this.startDate = Objects.requireNonNull(startDate, "startDate");
        this.endDate = Objects.requireNonNull(endDate, "endDate");
        if (endDate.isBefore(startDate)) {
            throw new IllegalArgumentException("endDate must not be before startDate");
        }
        this.state = LeaveRequestState.REQUESTED;
    }

    public void approve(UserId by) {
        this.state = LeaveRequestState.APPROVED;
        this.decidedBy = Objects.requireNonNull(by, "by");
    }

    public void reject(UserId by) {
        this.state = LeaveRequestState.REJECTED;
        this.decidedBy = Objects.requireNonNull(by, "by");
    }

    public LeaveRequestId id() {
        return id;
    }

    public TenantId tenantId() {
        return tenantId;
    }

    public EmployeeId employeeId() {
        return employeeId;
    }

    public LeaveType type() {
        return type;
    }

    public LocalDate startDate() {
        return startDate;
    }

    public LocalDate endDate() {
        return endDate;
    }

    public LeaveRequestState state() {
        return state;
    }

    public UserId decidedBy() {
        return decidedBy;
    }
}
