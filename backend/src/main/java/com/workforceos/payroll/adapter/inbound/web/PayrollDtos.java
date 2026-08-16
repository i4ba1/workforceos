package com.workforceos.payroll.adapter.inbound.web;

import com.workforceos.payroll.domain.PayPeriod;
import com.workforceos.payroll.domain.PayrollExport;
import com.workforceos.payroll.domain.PayrollReadiness;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;
import java.util.UUID;

/** Web DTOs for payroll endpoints. */
public final class PayrollDtos {

    private PayrollDtos() {
    }

    public record OpenPayPeriodRequest(
            @NotNull LocalDate startDate,
            @NotNull LocalDate endDate) {
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

    public record ReopenRequest(@NotBlank String reason) {
    }

    public record ReadinessResponse(
            int totalEmployees,
            int unresolvedCount,
            long totalRegularMinutes,
            long totalOvertimeMinutes,
            double finalizedPercent) {

        public static ReadinessResponse from(PayrollReadiness readiness) {
            return new ReadinessResponse(
                    readiness.totalEmployees(),
                    readiness.unresolvedCount(),
                    readiness.totalRegular().value(),
                    readiness.totalOvertime().value(),
                    readiness.finalizedPercent());
        }
    }

    public record ExportResponse(
            UUID id,
            int version,
            String checksum,
            String format,
            String generatedAt,
            UUID generatedBy) {

        public static ExportResponse from(PayrollExport export) {
            return new ExportResponse(
                    export.id().value(),
                    export.version(),
                    export.checksum(),
                    export.format(),
                    export.generatedAt().toString(),
                    export.generatedBy().value());
        }
    }
}
