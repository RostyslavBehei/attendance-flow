package com.attendance.flow.model.dto.lesson;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.time.LocalTime;

public record LessonCreateRequest(
        @NotNull(message = "{lesson.lessonNumber.notNull}")
        @Positive(message = "{lesson.lessonNumber.positive}")
        Integer lessonNumber,

        @NotNull(message = "{lesson.subjectId.notNull}")
        @Positive(message = "{lesson.subjectId.positive}")
        Long subjectId,

        @NotNull(message = "{lesson.startTime.notNull}")
        LocalTime startTime,

        @NotNull(message = "{lesson.endTime.notNull}")
        LocalTime endTime
) {
}
