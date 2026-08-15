package com.workforceos.attendance.adapter.inbound.web;

import com.workforceos.attendance.domain.AttendanceException;
import com.workforceos.attendance.domain.AttendanceRecord;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

/** Web DTOs for attendance endpoints. */
public final class AttendanceDtos {

    private AttendanceDtos() {
    }

    public record RecalculateRequest(
            @NotNull UUID employeeId,
            @NotNull LocalDate businessDate) {
    }

    public record AttendanceRecordResponse(
            UUID id,
            UUID employeeId,
            LocalDate businessDate,
            String status,
            long regularMinutes,
            long overtimeMinutes,
            long breakMinutes,
            long workedMinutes,
            long version,
            UUID scheduleEntryId) {

        public static AttendanceRecordResponse from(AttendanceRecord record) {
            return new AttendanceRecordResponse(
                    record.id().value(),
                    record.employeeId().value(),
                    record.businessDate().value(),
                    record.status().name(),
                    record.regularMinutes().value(),
                    record.overtimeMinutes().value(),
                    record.breakMinutes().value(),
                    record.regularMinutes().value() + record.overtimeMinutes().value(),
                    record.version(),
                    record.scheduleEntryId() == null ? null : record.scheduleEntryId().value());
        }
    }

    public record ExceptionResponse(UUID recordId, String type, String severity, String state, String detail, String createdAt) {

        public static ExceptionResponse from(AttendanceException exception) {
            return new ExceptionResponse(
                    exception.recordId().value(),
                    exception.type().name(),
                    exception.severity().name(),
                    exception.state().name(),
                    exception.detail(),
                    exception.createdAt().toString());
        }
    }

    public record CorrectionRequest(@NotBlank String reason) {
    }

    public record CorrectionResponse(UUID caseId) {
    }

    public record AttendanceDetailResponse(AttendanceRecordResponse record, List<ExceptionResponse> exceptions) {
    }
}
