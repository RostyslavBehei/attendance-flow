package com.attendance.flow.controller.rest;

import com.attendance.flow.model.dto.attendanceRecord.AttendanceBatchRequest;
import com.attendance.flow.model.dto.attendanceRecord.LessonSessionResponse;
import com.attendance.flow.service.LessonSessionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/api/lesson-sessions")
@RequiredArgsConstructor
public class LessonSessionRestController {

    private final LessonSessionService lessonSessionService;

    @GetMapping
    public ResponseEntity<LessonSessionResponse> getLessonSession(
            @RequestParam Long lessonId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime dateTime) {
        LessonSessionResponse lessonSession = lessonSessionService.getLessonSession(lessonId, dateTime);
        return ResponseEntity.ok(lessonSession);
    }

    @PostMapping
    public ResponseEntity<LessonSessionResponse> saveLessonSession(
            @RequestParam Long teacherId,
            @Valid @RequestBody AttendanceBatchRequest request) {
        LessonSessionResponse lessonSessionResponse = lessonSessionService.saveLessonSession(teacherId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(lessonSessionResponse);
    }

    @PutMapping("/{sessionId}")
    public ResponseEntity<LessonSessionResponse> updateLessonSession(
            @PathVariable Long sessionId,
            @Valid @RequestBody AttendanceBatchRequest request) {
        LessonSessionResponse lessonSessionResponse = lessonSessionService.updateLessonSession(sessionId, request);
        return ResponseEntity.ok(lessonSessionResponse);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteLessonSession(
            @PathVariable Long id) {
        lessonSessionService.deleteLessonSession(id);
        return ResponseEntity.noContent().build();
    }
}