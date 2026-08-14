package com.workforceos.payroll.domain.event;

import com.workforceos.shared.id.PayrollExportId;
import com.workforceos.shared.id.TenantId;

/** Published when an export artifact is produced. */
public record PayrollExportGenerated(TenantId tenantId, PayrollExportId payrollExportId) {
}
