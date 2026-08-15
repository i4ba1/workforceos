package com.workforceos.payroll.application;

import com.workforceos.payroll.domain.PayrollExport;

/** The result of an export: the artifact metadata plus its deterministic content. */
public record PayrollExportResult(PayrollExport export, byte[] content) {
}
