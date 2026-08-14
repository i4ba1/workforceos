package com.workforceos.payroll.domain;

import com.workforceos.shared.id.PayPeriodId;
import com.workforceos.shared.id.TenantId;
import com.workforceos.shared.id.UserId;

import java.time.Instant;
import java.time.LocalDate;
import java.util.Objects;

/**
 * A payroll period that is opened, validated, closed and optionally reopened.
 *
 * <p>Close blocks normal edits for the period; reopen requires elevated permission and a
 * mandatory reason. Closed-period data is never silently mutated.</p>
 */
public class PayPeriod {

    private final PayPeriodId id;
    private final TenantId tenantId;
    private final LocalDate startDate;
    private final LocalDate endDate;
    private PayPeriodState state;
    private UserId closedBy;
    private Instant closedAt;

    public PayPeriod(PayPeriodId id, TenantId tenantId, LocalDate startDate, LocalDate endDate) {
        this.id = Objects.requireNonNull(id, "id");
        this.tenantId = Objects.requireNonNull(tenantId, "tenantId");
        this.startDate = Objects.requireNonNull(startDate, "startDate");
        this.endDate = Objects.requireNonNull(endDate, "endDate");
        if (endDate.isBefore(startDate)) {
            throw new IllegalArgumentException("endDate must not be before startDate");
        }
        this.state = PayPeriodState.OPEN;
    }

    public void startValidation() {
        if (state != PayPeriodState.OPEN) {
            throw new IllegalStateException("Period is not open");
        }
        this.state = PayPeriodState.VALIDATING;
    }

    public void close(UserId by, Instant at) {
        if (state != PayPeriodState.VALIDATING) {
            throw new IllegalStateException("Period must be validated before close");
        }
        this.state = PayPeriodState.CLOSED;
        this.closedBy = Objects.requireNonNull(by, "by");
        this.closedAt = Objects.requireNonNull(at, "at");
    }

    public void reopen(UserId by, Instant at) {
        if (state != PayPeriodState.CLOSED) {
            throw new IllegalStateException("Period is not closed");
        }
        this.state = PayPeriodState.REOPENED;
        this.closedBy = Objects.requireNonNull(by, "by");
        this.closedAt = Objects.requireNonNull(at, "at");
    }

    public PayPeriodId id() {
        return id;
    }

    public TenantId tenantId() {
        return tenantId;
    }

    public LocalDate startDate() {
        return startDate;
    }

    public LocalDate endDate() {
        return endDate;
    }

    public PayPeriodState state() {
        return state;
    }

    public UserId closedBy() {
        return closedBy;
    }

    public Instant closedAt() {
        return closedAt;
    }
}
