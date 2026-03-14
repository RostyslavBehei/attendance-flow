package com.attendance.flow.controller.rest;

import com.attendance.flow.model.dto.lesson.LessonCreateRequest;
import com.attendance.flow.model.dto.lesson.LessonResponse;
import com.attendance.flow.model.dto.schedule.ScheduleCreateRequest;
import com.attendance.flow.model.dto.schedule.ScheduleResponse;
import com.attendance.flow.model.dto.schedule.ScheduleUpdateRequest;
import com.attendance.flow.service.LessonService;
import com.attendance.flow.service.ScheduleService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/schedules")
@RequiredArgsConstructor
public class ScheduleRestController {

    private final ScheduleService scheduleService;
    private final LessonService lessonService;

    @GetMapping("/{groupId}")
    public ResponseEntity<List<ScheduleResponse>> getSchedules(
            @PathVariable Long groupId) {
        List<ScheduleResponse> schedules = scheduleService.getGroupSchedules(groupId);
        return ResponseEntity.ok(schedules);
    }

    @PostMapping("/{scheduleId}/lessons")
    public ResponseEntity<LessonResponse> addLessonToSchedule(
            @PathVariable Long scheduleId,
            @Valid @RequestBody LessonCreateRequest request) {
        LessonResponse lesson = lessonService.addLessonToSchedule(scheduleId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(lesson);
    }

    @PostMapping
    public ResponseEntity<ScheduleResponse> createSchedule(
            @RequestParam Long groupId,
            @Valid @RequestBody ScheduleCreateRequest request) {
        ScheduleResponse schedule = scheduleService.createSchedule(groupId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(schedule);
    }

    @PutMapping("/{scheduleId}")
    public ResponseEntity<ScheduleResponse> updateSchedule(
            @PathVariable Long scheduleId,
            @Valid @RequestBody ScheduleUpdateRequest request) {
        ScheduleResponse schedule = scheduleService.updateSchedule(scheduleId, request);
        return ResponseEntity.status(HttpStatus.OK).body(schedule);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteSchedule(
            @PathVariable Long id) {
        scheduleService.deleteSchedule(id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @DeleteMapping("/{scheduleId}/lessons/{lessonId}")
    public ResponseEntity<Void> deleteLessonFromSchedule(
            @PathVariable Long scheduleId,
            @PathVariable Long lessonId) {
        lessonService.deleteLessonFromSchedule(scheduleId, lessonId);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

}
