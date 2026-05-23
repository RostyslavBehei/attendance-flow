package com.attendance.flow.model.dto.attendanceRecord;

import com.attendance.flow.model.enums.AttendanceStatus;

public record AttendanceMarkedEvent(
        Long studentId,
        AttendanceStatus status
) {
}
