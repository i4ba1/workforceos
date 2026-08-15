package com.workforceos.payroll.adapter.outbound.readmodel;

import com.workforceos.payroll.domain.PayrollDataSource;
import com.workforceos.payroll.domain.PayrollProjection;
import com.workforceos.shared.id.EmployeeId;
import com.workforceos.shared.id.TenantId;
import com.workforceos.shared.time.Minutes;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

/**
 * Read-only access to attendance facts for payroll readiness and finalized totals.
 * Uses direct SQL on the attendance tables (an explicitly designed read model).
 */
@Repository
public class PayrollDataSourceAdapter implements PayrollDataSource {

    private final JdbcTemplate jdbc;

    public PayrollDataSourceAdapter(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    @Override
    public long countOpenExceptions(TenantId tenantId, LocalDate from, LocalDate to) {
        Long count = jdbc.queryForObject("""
                SELECT COUNT(*)
                FROM attendance_exception e
                JOIN attendance_record r ON e.record_id = r.id
                WHERE r.tenant_id = ? AND r.business_date BETWEEN ? AND ? AND e.state = 'OPEN'
                """, Long.class, tenantId.value(), java.sql.Date.valueOf(from), java.sql.Date.valueOf(to));
        return count == null ? 0 : count;
    }

    @Override
    public List<PayrollProjection.Line> findTotals(TenantId tenantId, LocalDate from, LocalDate to) {
        return jdbc.query("""
                SELECT employee_id,
                       COALESCE(SUM(regular_minutes), 0) AS regular,
                       COALESCE(SUM(overtime_minutes), 0) AS overtime
                FROM attendance_record
                WHERE tenant_id = ? AND business_date BETWEEN ? AND ?
                GROUP BY employee_id
                ORDER BY employee_id
                """,
                (rs, i) -> new PayrollProjection.Line(
                        new EmployeeId(rs.getObject("employee_id", UUID.class)),
                        Minutes.of(rs.getLong("regular")),
                        Minutes.of(rs.getLong("overtime")),
                        Minutes.ZERO),
                tenantId.value(), java.sql.Date.valueOf(from), java.sql.Date.valueOf(to));
    }
}
