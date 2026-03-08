package com.attendance.flow.model.dto.schedule;

import com.attendance.flow.model.dto.lesson.LessonResponse;

import java.time.DayOfWeek;
import java.util.List;

public record ScheduleResponse(
        DayOfWeek dayOfWeek,
        List<LessonResponse> lessons
) {
}
