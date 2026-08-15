package com.workforceos.payroll.adapter.outbound.export;

import com.workforceos.payroll.domain.PayrollExporter;
import com.workforceos.payroll.domain.PayrollProjection;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;

/**
 * Deterministic CSV exporter. Given the same projection it produces identical bytes, so a
 * re-export of the same closed period yields the same content and checksum.
 */
@Component
public class CsvPayrollExporter implements PayrollExporter {

    @Override
    public String format() {
        return "CSV";
    }

    @Override
    public byte[] export(PayrollProjection projection) {
        StringBuilder csv = new StringBuilder();
        csv.append("employee_id,regular_minutes,overtime_minutes,holiday_minutes,total_minutes\n");
        for (PayrollProjection.Line line : projection.lines()) {
            long total = line.regularMinutes().value() + line.overtimeMinutes().value() + line.holidayMinutes().value();
            csv.append(line.employeeId().value()).append(',')
                    .append(line.regularMinutes().value()).append(',')
                    .append(line.overtimeMinutes().value()).append(',')
                    .append(line.holidayMinutes().value()).append(',')
                    .append(total).append('\n');
        }
        return csv.toString().getBytes(StandardCharsets.UTF_8);
    }
}
