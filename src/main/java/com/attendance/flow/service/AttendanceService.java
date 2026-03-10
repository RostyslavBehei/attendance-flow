package com.attendance.flow.service;

import com.attendance.flow.model.dto.attendanceRecord.AttendanceMarkRequest;
import com.attendance.flow.model.dto.attendanceRecord.AttendanceResponse;
import com.attendance.flow.model.dto.attendanceRecord.StudentAttendanceStatsResponse;

import java.util.List;

public interface AttendanceService {
    StudentAttendanceStatsResponse getStudentStats(Long studentId, Long groupId);
    List<AttendanceResponse> getStudentHistory(Long studentId);
    AttendanceResponse updateSingleAttendance(Long attendanceId, AttendanceMarkRequest request);
}
