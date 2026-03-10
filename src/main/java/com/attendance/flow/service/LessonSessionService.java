package com.attendance.flow.service;

import com.attendance.flow.model.dto.attendanceRecord.AttendanceBatchRequest;
import com.attendance.flow.model.dto.attendanceRecord.LessonSessionResponse;

import java.time.LocalDateTime;

public interface LessonSessionService {
    LessonSessionResponse saveLessonSession(Long teacherId, AttendanceBatchRequest request);
    LessonSessionResponse getLessonSession(Long lessonId, LocalDateTime dateTime);
    LessonSessionResponse updateLessonSession(Long sessionId, AttendanceBatchRequest request);
    void deleteLessonSession(Long lessonId);
}
