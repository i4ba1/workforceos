package com.workforceos.attendance.domain;

import java.util.List;

/**
 * Evaluates a calculated {@link AttendanceContext} and returns zero or more typed findings.
 *
 * <p>Concrete rules (late grace, early-leave, missing punch, holiday work, ...) implement
 * this contract and are composed from configuration. Adding a rule never requires
 * rewriting the calculation core (open/closed principle).</p>
 */
public interface AttendanceRule {

    /**
     * @return zero or more findings; an empty list means the rule did not fire
     */
    List<ExceptionFinding> evaluate(AttendanceContext context);
}
