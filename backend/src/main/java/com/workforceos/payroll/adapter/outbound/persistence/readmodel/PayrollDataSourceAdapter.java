package com.workforceos.payroll.adapter.outbound.persistence.readmodel;

import com.workforceos.payroll.domain.PayrollAttendanceLine;
import com.workforceos.payroll.domain.PayrollDataSource;
import com.workforceos.shared.id.EmployeeId;
import com.workforceos.shared.id.TenantId;
import com.workforceos.shared.time.Minutes;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Read-model adapter that aggregates finalized attendance into payroll lines.
 */
@Repository
public class PayrollDataSourceAdapter implements PayrollDataSource {

    private final PayrollAttendanceRecordReadRepository recordRepository;
    private final PayrollExceptionReadRepository exceptionRepository;

    public PayrollDataSourceAdapter(PayrollAttendanceRecordReadRepository recordRepository,
                                    PayrollExceptionReadRepository exceptionRepository) {
        this.recordRepository = recordRepository;
        this.exceptionRepository = exceptionRepository;
    }

    @Override
    public List<PayrollAttendanceLine> findAttendance(TenantId tenantId, LocalDate from, LocalDate to) {
        List<PayrollAttendanceRecordReadEntity> records = recordRepository
                .findAllByTenantIdAndBusinessDateBetween(tenantId.value(), from, to);

        Set<UUID> periodRecordIds = records.stream().map(PayrollAttendanceRecordReadEntity::getId).collect(Collectors.toSet());
        Set<UUID> blockedRecordIds = exceptionRepository.findAllByTenantIdAndState(tenantId.value(), "OPEN").stream()
                .map(PayrollExceptionReadEntity::getRecordId)
                .filter(periodRecordIds::contains)
                .collect(Collectors.toSet());

        Map<UUID, List<PayrollAttendanceRecordReadEntity>> byEmployee = records.stream()
                .collect(Collectors.groupingBy(PayrollAttendanceRecordReadEntity::getEmployeeId));

        return byEmployee.entrySet().stream()
                .sorted(Map.Entry.comparingByKey())
                .map(entry -> toLine(entry.getKey(), entry.getValue(), blockedRecordIds))
                .toList();
    }

    private PayrollAttendanceLine toLine(UUID employeeId, List<PayrollAttendanceRecordReadEntity> records,
                                         Set<UUID> blockedRecordIds) {
        long regular = records.stream().mapToLong(PayrollAttendanceRecordReadEntity::getRegularMinutes).sum();
        long overtime = records.stream().mapToLong(PayrollAttendanceRecordReadEntity::getOvertimeMinutes).sum();
        boolean blocked = records.stream().anyMatch(r -> blockedRecordIds.contains(r.getId()));
        return new PayrollAttendanceLine(new EmployeeId(employeeId), Minutes.of(regular), Minutes.of(overtime), blocked);
    }
}
