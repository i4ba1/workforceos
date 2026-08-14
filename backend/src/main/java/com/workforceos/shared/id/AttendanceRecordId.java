package com.workforceos.shared.id;

import java.util.UUID;

/** Attendance record identifier value object. */
public record AttendanceRecordId(UUID value) {

    public static AttendanceRecordId of(String value) {
        return new AttendanceRecordId(UUID.fromString(value));
    }

    public static AttendanceRecordId newId() {
        return new AttendanceRecordId(UUID.randomUUID());
    }
}
