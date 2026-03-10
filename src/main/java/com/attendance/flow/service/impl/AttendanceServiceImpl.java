package com.attendance.flow.service.impl;

import com.attendance.flow.exception.NotFoundException;
import com.attendance.flow.model.AppGroup;
import com.attendance.flow.model.Attendance;
import com.attendance.flow.model.User;
import com.attendance.flow.model.dto.attendanceRecord.AttendanceMarkRequest;
import com.attendance.flow.model.dto.attendanceRecord.AttendanceResponse;
import com.attendance.flow.model.dto.attendanceRecord.StudentAttendanceStatsResponse;
import com.attendance.flow.model.enums.AttendanceStatus;
import com.attendance.flow.repository.AppGroupRepository;
import com.attendance.flow.repository.AttendanceRepository;
import com.attendance.flow.repository.UserRepository;
import com.attendance.flow.service.AttendanceService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AttendanceServiceImpl implements AttendanceService {

    private final AttendanceRepository attendanceRepository;
    private final UserRepository userRepository;
    private final AppGroupRepository appGroupRepository;

    @Override
    @Transactional(readOnly = true)
    public StudentAttendanceStatsResponse getStudentStats(Long studentId, Long groupId) {
        User student = userRepository.findById(studentId)
                .orElseThrow(() -> new NotFoundException("Student with id " + studentId + " not found"));

        int totalLessons = (int) attendanceRepository.countTotalSessionsForGroup(groupId);

        int presentCount = (int) attendanceRepository.countByStudentIdAndStatus(studentId, AttendanceStatus.PRESENT);
        int absentCount = (int) attendanceRepository.countByStudentIdAndStatus(studentId, AttendanceStatus.ABSENT);
        int lateCount = (int) attendanceRepository.countByStudentIdAndStatus(studentId, AttendanceStatus.LATE);
        int excuseCount = (int) attendanceRepository.countByStudentIdAndStatus(studentId, AttendanceStatus.EXCUSE);

        double attendancePercentage = 0;
        if (totalLessons > 0) {
            double attended = presentCount + lateCount;
            attendancePercentage = (attended / totalLessons) * 100;
            attendancePercentage = Math.round(attendancePercentage * 10.0) / 10.0;
        }

        return new StudentAttendanceStatsResponse(
                student.getId(),
                student.getFirstName(),
                student.getLastName(),
                totalLessons,
                presentCount,
                absentCount,
                lateCount,
                excuseCount,
                attendancePercentage
        );
    }

    @Override
    @Transactional(readOnly = true)
    public List<AttendanceResponse> getStudentHistory(Long studentId) {
        return attendanceRepository.findAllByStudentId(studentId).stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Override
    @Transactional
    public AttendanceResponse updateSingleAttendance(Long attendanceId, AttendanceMarkRequest request) {
        Attendance attendance = attendanceRepository.findById(attendanceId)
                .orElseThrow(() -> new NotFoundException("Attendance with id " + attendanceId + " not found"));

        attendance.setStatus(request.status());
        attendance.setComment(request.comment());

        Attendance updatedAttendance = attendanceRepository.save(attendance);
        return mapToResponse(updatedAttendance);
    }

    private AttendanceResponse mapToResponse(Attendance attendance) {
        String studentFullName = attendance.getStudent().getFirstName() + " " + attendance.getStudent().getLastName();

        return new AttendanceResponse(
                attendance.getId(),
                attendance.getStudent().getId(),
                studentFullName,
                attendance.getStatus(),
                attendance.getComment()
        );
    }
}
