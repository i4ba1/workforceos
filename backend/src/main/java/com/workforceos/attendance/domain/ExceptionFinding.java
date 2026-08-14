package com.workforceos.attendance.domain;

import java.util.Objects;

/**
 * A typed rule finding emitted by an {@link AttendanceRule}.
 *
 * @param type     exception type produced
 * @param severity severity used for queue ordering
 * @param detail   human-safe explanation, free of PII
 */
public record ExceptionFinding(ExceptionType type, ExceptionSeverity severity, String detail) {

    public ExceptionFinding {
        Objects.requireNonNull(type, "type");
        Objects.requireNonNull(severity, "severity");
        Objects.requireNonNull(detail, "detail");
    }
}
