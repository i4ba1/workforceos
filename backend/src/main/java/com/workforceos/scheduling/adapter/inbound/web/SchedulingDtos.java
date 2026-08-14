package com.workforceos.scheduling.adapter.inbound.web;

import com.workforceos.scheduling.domain.ScheduleEntry;
import com.workforceos.scheduling.domain.ShiftTemplate;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.UUID;

/** Web DTOs for scheduling endpoints. */
public final class SchedulingDtos {

    private SchedulingDtos() {
    }

    public record CreateShiftTemplateRequest(
            @NotBlank String name,
            @NotNull LocalTime localStart,
            @NotNull LocalTime localEnd,
            @NotBlank String zoneId,
            @Min(0) long breakMinutes,
            boolean breakPaid) {
    }

    public record ShiftTemplateResponse(
            UUID id,
            String name,
            LocalTime localStart,
            LocalTime localEnd,
            String zoneId,
            long breakMinutes,
            boolean breakPaid) {

        public static ShiftTemplateResponse from(ShiftTemplate template) {
            return new ShiftTemplateResponse(
                    template.id().value(),
                    template.name(),
                    template.localStart(),
                    template.localEnd(),
                    template.zoneId().getId(),
                    template.breakConfig().minimumBreakMinutes().value(),
                    template.breakConfig().paid());
        }
    }

    public record CreateScheduleEntryRequest(
            @NotNull UUID employeeId,
            @NotNull LocalDate businessDate,
            @NotBlank String zoneId,
            @NotNull Instant plannedStart,
            @NotNull Instant plannedEnd) {
    }

    public record ScheduleEntryResponse(
            UUID id,
            UUID employeeId,
            LocalDate businessDate,
            String zoneId,
            Instant plannedStart,
            Instant plannedEnd,
            long version) {

        public static ScheduleEntryResponse from(ScheduleEntry entry) {
            return new ScheduleEntryResponse(
                    entry.id().value(),
                    entry.employeeId().value(),
                    entry.businessDate().value(),
                    entry.zoneId().getId(),
                    entry.plannedStart(),
                    entry.plannedEnd(),
                    entry.version());
        }
    }
}
