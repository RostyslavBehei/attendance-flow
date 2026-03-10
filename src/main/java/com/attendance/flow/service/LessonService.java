package com.attendance.flow.service;

import com.attendance.flow.model.dto.lesson.LessonCreateRequest;
import com.attendance.flow.model.dto.lesson.LessonResponse;

import java.util.List;

public interface LessonService {
    List<LessonResponse> getAllLessons();
    LessonResponse addLessonToSchedule(Long scheduleId, LessonCreateRequest request);
    void deleteLessonFromSchedule(Long scheduleId, Long lessonId);
}
