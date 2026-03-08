package com.attendance.flow.model.dto.attendanceRecord;

import java.time.LocalDateTime;
import java.util.List;

public record LessonSessionResponse(
    Long id,
    Long lessonId,
    LocalDateTime dateTime,
    String topic,
    String markedByFullName,
    List<AttendanceResponse> attendances
) {
}
