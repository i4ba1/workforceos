package com.workforceos.attendance.domain;

import com.workforceos.shared.id.AttendanceRecordId;

import java.time.Instant;
import java.util.Objects;

/**
 * An actionable anomaly detected by an {@link AttendanceRule}.
 *
 * <p>Identified by the owning record plus exception type; severity drives the manager
 * queue ordering by SLA and age.</p>
 */
public class AttendanceException {

    private final AttendanceRecordId recordId;
    private final ExceptionType type;
    private final ExceptionSeverity severity;
    private final String detail;
    private final Instant createdAt;
    private ExceptionState state;

    public AttendanceException(AttendanceRecordId recordId, ExceptionType type,
                               ExceptionSeverity severity, String detail, Instant createdAt) {
        this(recordId, type, severity, detail, createdAt, ExceptionState.OPEN);
    }

    public AttendanceException(AttendanceRecordId recordId, ExceptionType type,
                               ExceptionSeverity severity, String detail, Instant createdAt,
                               ExceptionState state) {
        this.recordId = Objects.requireNonNull(recordId, "recordId");
        this.type = Objects.requireNonNull(type, "type");
        this.severity = Objects.requireNonNull(severity, "severity");
        this.detail = Objects.requireNonNull(detail, "detail");
        this.createdAt = Objects.requireNonNull(createdAt, "createdAt");
        this.state = Objects.requireNonNull(state, "state");
    }

    public void beginReview() {
        this.state = ExceptionState.UNDER_REVIEW;
    }

    public void resolve() {
        this.state = ExceptionState.RESOLVED;
    }

    public void dismiss() {
        this.state = ExceptionState.DISMISSED;
    }

    public AttendanceRecordId recordId() {
        return recordId;
    }

    public ExceptionType type() {
        return type;
    }

    public ExceptionSeverity severity() {
        return severity;
    }

    public String detail() {
        return detail;
    }

    public Instant createdAt() {
        return createdAt;
    }

    public ExceptionState state() {
        return state;
    }
}
