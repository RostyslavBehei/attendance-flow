package com.attendance.flow.controller.rest;

import com.attendance.flow.model.dto.attendanceRecord.AttendanceMarkRequest;
import com.attendance.flow.model.dto.attendanceRecord.AttendanceResponse;
import com.attendance.flow.model.dto.attendanceRecord.StudentAttendanceStatsResponse;
import com.attendance.flow.service.AttendanceService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/attendances")
@RequiredArgsConstructor
public class AttendanceRestController {

    private final AttendanceService attendanceService;

    @GetMapping("/student/{studentId}/stats")
    public ResponseEntity<StudentAttendanceStatsResponse> getStudentStats(
            @PathVariable Long studentId,
            @RequestParam Long groupId) {

        StudentAttendanceStatsResponse studentStats = attendanceService.getStudentStats(studentId, groupId);
        return ResponseEntity.ok(studentStats);
    }

    @GetMapping("/student/{studentId}/history")
    public ResponseEntity<List<AttendanceResponse>> getStudentHistory(
            @PathVariable Long studentId) {

        List<AttendanceResponse> attendances = attendanceService.getStudentHistory(studentId);
        return ResponseEntity.ok(attendances);
    }

    @PatchMapping("/{attendanceId}")
    public ResponseEntity<AttendanceResponse> updateSingleAttendance(
            @PathVariable Long attendanceId,
            @Valid @RequestBody AttendanceMarkRequest markRequest) {

        AttendanceResponse attendance = attendanceService.updateSingleAttendance(attendanceId, markRequest);
        return ResponseEntity.ok(attendance);
    }
}