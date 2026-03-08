package com.attendance.flow.model.dto.attendanceRecord;

import com.attendance.flow.model.enums.AttendanceStatus;
import jakarta.validation.constraints.NotNull;

public record AttendanceMarkRequest(

        @NotNull(message = "{attendance.studentId.notNull}")
        Long studentId,

        @NotNull(message = "{attendance.attendanceStatus.notNull}")
        AttendanceStatus status,
        String comment
) {
}
