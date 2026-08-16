package com.workforceos.payroll.config;

import com.workforceos.payroll.domain.CsvPayrollExporter;
import com.workforceos.payroll.domain.PayrollExporter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/** Registers the deterministic CSV export strategy. */
@Configuration
public class PayrollConfiguration {

    @Bean
    PayrollExporter payrollExporter() {
        return new CsvPayrollExporter();
    }
}
