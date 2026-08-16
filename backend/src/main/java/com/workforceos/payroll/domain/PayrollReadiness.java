package com.workforceos.payroll.domain;

import com.workforceos.shared.time.Minutes;

import java.util.List;

/**
 * Payroll-readiness summary for a period: aggregated totals plus unresolved blockers.
 */
public record PayrollReadiness(
        List<PayrollAttendanceLine> lines,
        int totalEmployees,
        int unresolvedCount,
        Minutes totalRegular,
        Minutes totalOvertime) {

    public double finalizedPercent() {
        if (totalEmployees == 0) {
            return 100.0;
        }
        return (totalEmployees - unresolvedCount) * 100.0 / totalEmployees;
    }
}
