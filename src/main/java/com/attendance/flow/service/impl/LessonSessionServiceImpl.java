package com.attendance.flow.service.impl;

import com.attendance.flow.exception.NotFoundException;
import com.attendance.flow.model.Attendance;
import com.attendance.flow.model.Lesson;
import com.attendance.flow.model.LessonSession;
import com.attendance.flow.model.User;
import com.attendance.flow.model.dto.attendanceRecord.AttendanceBatchRequest;
import com.attendance.flow.model.dto.attendanceRecord.AttendanceMarkRequest;
import com.attendance.flow.model.dto.attendanceRecord.AttendanceResponse;
import com.attendance.flow.model.dto.attendanceRecord.LessonSessionResponse;
import com.attendance.flow.repository.LessonRepository;
import com.attendance.flow.repository.LessonSessionRepository;
import com.attendance.flow.repository.UserRepository;
import com.attendance.flow.service.LessonSessionService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class LessonSessionServiceImpl implements LessonSessionService {

    private LessonSessionRepository lessonSessionRepository;
    private UserRepository userRepository;
    private LessonRepository lessonRepository;

    @Override
    @Transactional
    public LessonSessionResponse saveLessonSession(Long teacherId, AttendanceBatchRequest request) {
        User teacher = userRepository.findById(teacherId)
                .orElseThrow(() -> new NotFoundException("Teacher with id " +  teacherId + " not found"));

        Lesson lesson = lessonRepository.findById(request.lessonId())
                .orElseThrow(() -> new NotFoundException("Lesson with id " + request.lessonId() + " not found"));

        LessonSession lessonSession = LessonSession.builder()
                .lesson(lesson)
                .dateTime(request.dateTime())
                .topic(request.topic())
                .markedBy(teacher)
                .build();

        for (AttendanceMarkRequest markReq : request.students()) {

            User student = userRepository.findById(markReq.studentId())
                    .orElseThrow(() -> new NotFoundException("User with id " +  markReq.studentId() + " not found"));

            Attendance attendance = Attendance.builder()
                    .student(student)
                    .status(markReq.status())
                    .comment(markReq.comment())
                    .build();

            lessonSession.addAttendance(attendance);
        }

        LessonSession savedLessonSession = lessonSessionRepository.save(lessonSession);

        return mapToResponse(savedLessonSession);
    }

    @Override
    @Transactional(readOnly = true)
    public LessonSessionResponse getLessonSession(Long lessonId, LocalDateTime dateTime) {
        LessonSession lessonSession = lessonSessionRepository.findByLessonIdAndDateTime(lessonId, dateTime)
                .orElseThrow(() -> new NotFoundException("Log for pair with id " + lessonId + " on date " + dateTime + " not found"));

        return mapToResponse(lessonSession);
    }

    @Override
    @Transactional
    public LessonSessionResponse updateLessonSession(Long sessionId, AttendanceBatchRequest request) {

        LessonSession lessonSession = lessonSessionRepository.findById(sessionId)
                .orElseThrow(() -> new NotFoundException("Lesson session with id " + sessionId + " not found"));

        Lesson lesson = lessonRepository.findById(request.lessonId())
                .orElseThrow(() -> new NotFoundException("Lesson with id " + request.lessonId() + " not found"));
        lessonSession.setLesson(lesson);

        lessonSession.setDateTime(request.dateTime());
        lessonSession.setTopic(request.topic());

        lessonSession.getAttendances().clear();

        if (request.students() != null && !request.students().isEmpty()) {
            for (AttendanceMarkRequest markReq : request.students()) {

                User student = userRepository.findById(markReq.studentId())
                        .orElseThrow(() -> new NotFoundException("User with id " + markReq.studentId() + " not found"));

                Attendance attendance = Attendance.builder()
                        .student(student)
                        .status(markReq.status())
                        .comment(markReq.comment())
                        .lessonSession(lessonSession)
                        .build();

                lessonSession.getAttendances().add(attendance);
            }
        }

        LessonSession savedLessonSession = lessonSessionRepository.save(lessonSession);

        return mapToResponse(savedLessonSession);
    }

    @Override
    @Transactional
    public void deleteLessonSession(Long lessonId) {
        LessonSession lessonSession = lessonSessionRepository.findById(lessonId)
                .orElseThrow(() -> new NotFoundException("Lesson session with id " + lessonId + " not found"));
        lessonSessionRepository.delete(lessonSession);
    }

    private LessonSessionResponse mapToResponse(LessonSession session) {

        String teacherName = session.getMarkedBy().getFirstName() + " " + session.getMarkedBy().getLastName();

        List<AttendanceResponse> attendanceResponses = session.getAttendances().stream()
                .map(att -> new AttendanceResponse(
                        att.getId(),
                        att.getStudent().getId(),
                        att.getStudent().getFirstName() + " " + att.getStudent().getLastName(),
                        att.getStatus(),
                        att.getComment()
                )).toList();

        return new LessonSessionResponse(
                session.getId(),
                session.getLesson().getId(),
                session.getDateTime(),
                session.getTopic(),
                teacherName,
                attendanceResponses
        );
    }

}
