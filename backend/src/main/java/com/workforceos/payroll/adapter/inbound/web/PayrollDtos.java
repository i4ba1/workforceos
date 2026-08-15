package com.workforceos.payroll.adapter.inbound.web;

import com.workforceos.payroll.domain.PayPeriod;
import com.workforceos.payroll.domain.PayrollExport;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;
import java.util.UUID;

/** Web DTOs for payroll endpoints. */
public final class PayrollDtos {

    private PayrollDtos() {
    }

    public record OpenPeriodRequest(@NotNull LocalDate startDate, @NotNull LocalDate endDate) {
    }

    public record ReopenRequest(@NotBlank String reason) {
    }

    public record PayPeriodResponse(
            UUID id,
            LocalDate startDate,
            LocalDate endDate,
            String state,
            long version,
            UUID closedBy,
            String closedAt) {

        public static PayPeriodResponse from(PayPeriod period) {
            return new PayPeriodResponse(
                    period.id().value(),
                    period.startDate(),
                    period.endDate(),
                    period.state().name(),
                    period.version(),
                    period.closedBy() == null ? null : period.closedBy().value(),
                    period.closedAt() == null ? null : period.closedAt().toString());
        }
    }

    public record PayrollExportResponse(
            UUID id,
            UUID periodId,
            int version,
            String checksum,
            String format,
            UUID generatedBy,
            String generatedAt) {

        public static PayrollExportResponse from(PayrollExport export) {
            return new PayrollExportResponse(
                    export.id().value(),
                    export.periodId().value(),
                    export.version(),
                    export.checksum(),
                    export.format(),
                    export.generatedBy().value(),
                    export.generatedAt().toString());
        }
    }

    public record PayrollExportContentResponse(PayrollExportResponse export, String content) {
    }
}
