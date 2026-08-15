package com.workforceos.payroll.domain;

import com.workforceos.shared.id.TenantId;

import java.time.LocalDate;
import java.util.List;

/**
 * Read-side source of payroll readiness and finalized totals from the attendance module.
 */
public interface PayrollDataSource {

    long countOpenExceptions(TenantId tenantId, LocalDate from, LocalDate to);

    List<PayrollProjection.Line> findTotals(TenantId tenantId, LocalDate from, LocalDate to);
}
