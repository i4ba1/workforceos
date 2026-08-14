package com.workforceos.payroll.domain;

/**
 * Transforms a closed-period {@link PayrollProjection} into an external format (e.g. CSV).
 *
 * <p>Implemented as a strategy so new formats are added without touching payroll
 * close/reopen orchestration. Export must be deterministic: same input yields the same
 * bytes.</p>
 */
public interface PayrollExporter {

    String format();

    byte[] export(PayrollProjection projection);
}
