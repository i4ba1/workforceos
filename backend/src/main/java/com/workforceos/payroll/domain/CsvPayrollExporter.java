package com.workforceos.payroll.domain;

import java.nio.charset.StandardCharsets;
import java.util.Comparator;
import java.util.List;

/**
 * Deterministic CSV exporter: the same projection always yields the same bytes.
 */
public class CsvPayrollExporter implements PayrollExporter {

    @Override
    public String format() {
        return "csv";
    }

    @Override
    public byte[] export(PayrollProjection projection) {
        List<PayrollProjection.Line> lines = projection.lines().stream()
                .sorted(Comparator.comparing(line -> line.employeeId().value()))
                .toList();

        StringBuilder sb = new StringBuilder();
        sb.append("employee_id,regular_minutes,overtime_minutes,holiday_minutes\n");
        for (PayrollProjection.Line line : lines) {
            sb.append(line.employeeId().value()).append(',')
                    .append(line.regularMinutes().value()).append(',')
                    .append(line.overtimeMinutes().value()).append(',')
                    .append(line.holidayMinutes().value()).append('\n');
        }
        return sb.toString().getBytes(StandardCharsets.UTF_8);
    }
}
