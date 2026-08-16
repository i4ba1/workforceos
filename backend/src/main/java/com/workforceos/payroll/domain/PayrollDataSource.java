package com.workforceos.payroll.domain;

import com.workforceos.shared.id.TenantId;

import java.time.LocalDate;
import java.util.List;

/** Read-side source of finalized attendance for a pay period. */
public interface PayrollDataSource {

    List<PayrollAttendanceLine> findAttendance(TenantId tenantId, LocalDate from, LocalDate to);
}
