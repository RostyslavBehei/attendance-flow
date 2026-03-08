package com.attendance.flow.model.dto.attendanceRecord;

import com.attendance.flow.model.enums.AttendanceStatus;

public record AttendanceResponse(
        Long id,
        Long studentId,
        String studentFullName,
        AttendanceStatus status,
        String comment
) {
}
