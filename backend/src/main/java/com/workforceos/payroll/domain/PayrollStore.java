package com.workforceos.payroll.domain;

import com.workforceos.shared.id.PayPeriodId;
import com.workforceos.shared.id.PayrollExportId;
import com.workforceos.shared.id.TenantId;

import java.util.List;
import java.util.Optional;

/** Persistence port for pay periods and their export history. */
public interface PayrollStore {

    Optional<PayPeriod> findPeriod(TenantId tenantId, PayPeriodId id);

    List<PayPeriod> findPeriods(TenantId tenantId);

    PayPeriod savePeriod(PayPeriod period);

    Optional<PayrollExport> findLatestExport(TenantId tenantId, PayPeriodId periodId);

    PayrollExport saveExport(PayrollExport export);

    List<PayrollExport> findExports(TenantId tenantId, PayPeriodId periodId);
}
