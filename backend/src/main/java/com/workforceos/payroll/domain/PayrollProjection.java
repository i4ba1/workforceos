package com.workforceos.payroll.domain;

import com.workforceos.shared.id.EmployeeId;
import com.workforceos.shared.id.PayPeriodId;
import com.workforceos.shared.time.Minutes;

import java.util.List;

/**
 * Read-model projection of a closed period used as the export input.
 *
 * <p>Kept free of vendor-specific export DTOs; exporters map this internal projection to
 * external formats.</p>
 */
public record PayrollProjection(PayPeriodId periodId, List<PayrollProjection.Line> lines) {

    public record Line(
            EmployeeId employeeId,
            Minutes regularMinutes,
            Minutes overtimeMinutes,
            Minutes holidayMinutes) {
    }
}
