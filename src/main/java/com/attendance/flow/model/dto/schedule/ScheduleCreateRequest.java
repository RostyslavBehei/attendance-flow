package com.attendance.flow.model.dto.schedule;

import com.attendance.flow.model.dto.lesson.LessonCreateRequest;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

import java.time.DayOfWeek;
import java.util.List;

public record ScheduleCreateRequest(
        @NotNull(message = "{schedule.dayOfWeek.notNull}")
        DayOfWeek dayOfWeek,

        @Valid
        List<LessonCreateRequest> lessonCreateRequests
) {
}
