package com.attendance.flow.model.dto.attendanceRecord;

public record StudentAttendanceStatsResponse(
        Long studentId,
        String firstName,
        String lastName,

        int totalLessons,

        int presentCount,
        int absentCount,
        int lateCount,
        int excusedCount,

        double attendancePercentage
) {
}
