package com.workforceos.payroll.domain;

import com.workforceos.shared.id.PayPeriodId;
import com.workforceos.shared.id.PayrollExportId;
import com.workforceos.shared.id.TenantId;
import com.workforceos.shared.id.UserId;

import java.time.Instant;
import java.util.Objects;

/**
 * A reproducible payroll export artifact.
 *
 * <p>Re-exporting the same closed period returns the same logical dataset; an export
 * carries a checksum and version so its content is attributable and verifiable.</p>
 */
public class PayrollExport {

    private final PayrollExportId id;
    private final TenantId tenantId;
    private final PayPeriodId periodId;
    private final int version;
    private final String checksum;
    private final String format;
    private final UserId generatedBy;
    private final Instant generatedAt;

    public PayrollExport(PayrollExportId id, TenantId tenantId, PayPeriodId periodId, int version,
                         String checksum, String format, UserId generatedBy, Instant generatedAt) {
        this.id = Objects.requireNonNull(id, "id");
        this.tenantId = Objects.requireNonNull(tenantId, "tenantId");
        this.periodId = Objects.requireNonNull(periodId, "periodId");
        this.version = version;
        this.checksum = Objects.requireNonNull(checksum, "checksum");
        this.format = Objects.requireNonNull(format, "format");
        this.generatedBy = Objects.requireNonNull(generatedBy, "generatedBy");
        this.generatedAt = Objects.requireNonNull(generatedAt, "generatedAt");
    }

    public PayrollExportId id() {
        return id;
    }

    public TenantId tenantId() {
        return tenantId;
    }

    public PayPeriodId periodId() {
        return periodId;
    }

    public int version() {
        return version;
    }

    public String checksum() {
        return checksum;
    }

    public String format() {
        return format;
    }

    public UserId generatedBy() {
        return generatedBy;
    }

    public Instant generatedAt() {
        return generatedAt;
    }
}
