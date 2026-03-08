package com.attendance.flow.model.dto.attendanceRecord;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;
import java.util.List;

public record AttendanceBatchRequest(
        @NotNull(message = "{attendance.lessonId.notNull}")
        Long lessonId,

        LocalDateTime dateTime,

        String topic,

        @Valid
        List<AttendanceMarkRequest> students
) {
}
