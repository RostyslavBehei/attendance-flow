package com.attendance.flow.model.dto.lesson;

import com.attendance.flow.model.dto.subject.SubjectResponse;

import java.time.LocalTime;

public record LessonResponse(
        Long id,
        Integer lessonNumber,
        LocalTime startTime,
        LocalTime endTime,
        SubjectResponse subject
) {
}
