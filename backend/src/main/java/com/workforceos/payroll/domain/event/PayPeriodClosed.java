package com.workforceos.payroll.domain.event;

import com.workforceos.shared.id.PayPeriodId;
import com.workforceos.shared.id.TenantId;

/** Published when all close validations pass for a pay period. */
public record PayPeriodClosed(TenantId tenantId, PayPeriodId payPeriodId) {
}
