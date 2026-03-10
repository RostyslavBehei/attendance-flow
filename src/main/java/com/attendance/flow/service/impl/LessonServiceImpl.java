package com.attendance.flow.service.impl;

import com.attendance.flow.exception.NotFoundException;
import com.attendance.flow.model.Lesson;
import com.attendance.flow.model.Schedule;
import com.attendance.flow.model.Subject;
import com.attendance.flow.model.dto.lesson.LessonCreateRequest;
import com.attendance.flow.model.dto.lesson.LessonResponse;
import com.attendance.flow.model.dto.subject.SubjectResponse;
import com.attendance.flow.repository.LessonRepository;
import com.attendance.flow.repository.ScheduleRepository;
import com.attendance.flow.repository.SubjectRepository;
import com.attendance.flow.service.LessonService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class LessonServiceImpl implements LessonService {

    private final LessonRepository lessonRepository;
    private final ScheduleRepository scheduleRepository;
    private final SubjectRepository subjectRepository;

    @Override
    @Transactional(readOnly = true)
    public List<LessonResponse> getAllLessons() {
        return lessonRepository.findAll().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public LessonResponse addLessonToSchedule(Long scheduleId, LessonCreateRequest request) {
        Schedule schedule = scheduleRepository.findById(scheduleId)
                .orElseThrow(() -> new NotFoundException("Schedule with id " + scheduleId + " not found"));

        Subject subject = subjectRepository.findById(request.subjectId())
                .orElseThrow(() -> new NotFoundException("Subject with id " + request.subjectId() + " not found"));

        Lesson lesson = Lesson.builder()
                .lessonNumber(request.lessonNumber())
                .subject(subject)
                .startTime(request.startTime())
                .endTime(request.endTime())
                .schedule(schedule)
                .build();

        schedule.getLessons().add(lesson);

        scheduleRepository.saveAndFlush(schedule);
        return mapToResponse(lesson);
    }

    @Override
    @Transactional
    public void deleteLessonFromSchedule(Long scheduleId, Long lessonId) {
        Schedule schedule = scheduleRepository.findById(scheduleId)
                .orElseThrow(() -> new NotFoundException("Schedule with id " + scheduleId + " not found"));

        boolean removed = schedule.getLessons().removeIf(lesson -> lesson.getId().equals(lessonId));
        if (!removed) {
            throw new NotFoundException("Lesson with id " + lessonId + " not found in the schedule");
        }

        scheduleRepository.save(schedule);
    }

    private LessonResponse mapToResponse(Lesson lesson) {
        return new LessonResponse(
                lesson.getId(),
                lesson.getLessonNumber(),
                lesson.getStartTime(),
                lesson.getEndTime(),
                new SubjectResponse(
                        lesson.getSubject().getId(),
                        lesson.getSubject().getName(),
                        lesson.getSubject().getDescription()
                )
        );
    }
}
