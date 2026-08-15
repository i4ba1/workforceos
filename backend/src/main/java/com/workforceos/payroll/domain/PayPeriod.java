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
 * mandatory reason. Closed-period data is never silently mutated. {@code version} counts
 * the number of closes so exports align with a specific close.</p>
 */
public class PayPeriod {

    private final PayPeriodId id;
    private final TenantId tenantId;
    private final LocalDate startDate;
    private final LocalDate endDate;
    private PayPeriodState state;
    private long version;
    private UserId closedBy;
    private Instant closedAt;

    public PayPeriod(PayPeriodId id, TenantId tenantId, LocalDate startDate, LocalDate endDate) {
        this(id, tenantId, startDate, endDate, PayPeriodState.OPEN, 0L, null, null);
    }

    public PayPeriod(PayPeriodId id, TenantId tenantId, LocalDate startDate, LocalDate endDate,
                     PayPeriodState state, long version, UserId closedBy, Instant closedAt) {
        this.id = Objects.requireNonNull(id, "id");
        this.tenantId = Objects.requireNonNull(tenantId, "tenantId");
        this.startDate = Objects.requireNonNull(startDate, "startDate");
        this.endDate = Objects.requireNonNull(endDate, "endDate");
        if (endDate.isBefore(startDate)) {
            throw new IllegalArgumentException("endDate must not be before startDate");
        }
        this.state = Objects.requireNonNull(state, "state");
        this.version = version;
        this.closedBy = closedBy;
        this.closedAt = closedAt;
    }

    public void startValidation() {
        if (state != PayPeriodState.OPEN && state != PayPeriodState.REOPENED) {
            throw new IllegalStateException("Period cannot be validated from state " + state);
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
        this.version++;
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

    public long version() {
        return version;
    }

    public UserId closedBy() {
        return closedBy;
    }

    public Instant closedAt() {
        return closedAt;
    }
}
