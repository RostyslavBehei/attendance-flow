package com.attendance.flow.service.impl;

import com.attendance.flow.exception.AlreadyExistsException;
import com.attendance.flow.exception.NotFoundException;
import com.attendance.flow.model.AppGroup;
import com.attendance.flow.model.Lesson;
import com.attendance.flow.model.Schedule;
import com.attendance.flow.model.Subject;
import com.attendance.flow.model.dto.schedule.ScheduleCreateRequest;
import com.attendance.flow.model.dto.schedule.ScheduleResponse;
import com.attendance.flow.model.dto.schedule.ScheduleUpdateRequest;
import com.attendance.flow.model.dto.lesson.LessonCreateRequest;
import com.attendance.flow.model.dto.lesson.LessonResponse;
import com.attendance.flow.model.dto.subject.SubjectResponse;
import com.attendance.flow.repository.AppGroupRepository;
import com.attendance.flow.repository.ScheduleRepository;
import com.attendance.flow.repository.SubjectRepository;
import com.attendance.flow.service.ScheduleService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ScheduleServiceImpl implements ScheduleService {

    private final ScheduleRepository scheduleRepository;
    private final AppGroupRepository appGroupRepository;
    private final SubjectRepository subjectRepository;

    @Override
    @Transactional(readOnly = true)
    public List<ScheduleResponse> getGroupSchedules(Long groupId) {
        return scheduleRepository.findByGroupId(groupId).stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Override
    @Transactional
    public ScheduleResponse createSchedule(Long groupId, ScheduleCreateRequest request) {
        AppGroup group = appGroupRepository.findById(groupId)
                .orElseThrow(() -> new NotFoundException("Group with id " + groupId + " not found"));

        Schedule newSchedule = Schedule.builder()
                .group(group)
                .dayOfWeek(request.dayOfWeek())
                .build();

        for (LessonCreateRequest lessonReq : request.lessonCreateRequests()) {
            Subject subject = subjectRepository.findById(lessonReq.subjectId())
                    .orElseThrow(() -> new NotFoundException("Subject with id " + lessonReq.subjectId() + " not found"));

            Lesson lesson = Lesson.builder()
                    .lessonNumber(lessonReq.lessonNumber())
                    .subject(subject)
                    .startTime(lessonReq.startTime())
                    .endTime(lessonReq.endTime())
                    .schedule(newSchedule)
                    .build();

            newSchedule.getLessons().add(lesson);
        }

        Schedule savedSchedule = scheduleRepository.save(newSchedule);

        return mapToResponse(savedSchedule);
    }

    @Override
    @Transactional
    public ScheduleResponse updateSchedule(Long scheduleId, ScheduleUpdateRequest request) {
        Schedule schedule = scheduleRepository.findById(scheduleId)
                .orElseThrow(() -> new NotFoundException("Schedule with id " + scheduleId + " not found"));

        if (!schedule.getDayOfWeek().equals(request.dayOfWeek())) {
            boolean dayAlreadyExists = scheduleRepository.existsByGroupIdAndDayOfWeek(
                    schedule.getGroup().getId(), request.dayOfWeek());

            if (dayAlreadyExists) {
                throw new AlreadyExistsException("A schedule for this group on " + request.dayOfWeek() + " already exists!");
            }
            schedule.setDayOfWeek(request.dayOfWeek());
        }

        schedule.getLessons().clear();

        for (LessonCreateRequest lessonReq : request.lessonCreateRequests()) {
            Subject subject = subjectRepository.findById(lessonReq.subjectId())
                    .orElseThrow(() -> new NotFoundException("Subject with id " + lessonReq.subjectId() + " not found"));

            Lesson lesson = Lesson.builder()
                    .lessonNumber(lessonReq.lessonNumber())
                    .subject(subject)
                    .startTime(lessonReq.startTime())
                    .endTime(lessonReq.endTime())
                    .schedule(schedule)
                    .build();

            schedule.getLessons().add(lesson);
        }

        Schedule updatedSchedule = scheduleRepository.save(schedule);

        return mapToResponse(updatedSchedule);
    }

    @Override
    @Transactional
    public void deleteSchedule(Long id) {
        if (scheduleRepository.existsById(id)) {
            scheduleRepository.deleteById(id);
        } else {
            throw new NotFoundException("Schedule with id " + id + " not found");
        }
    }

    private ScheduleResponse mapToResponse(Schedule schedule) {
        List<LessonResponse> lessonResponses = schedule.getLessons().stream()
                .map(lesson -> new LessonResponse(
                        lesson.getId(),
                        lesson.getLessonNumber(),
                        lesson.getStartTime(),
                        lesson.getEndTime(),
                        new SubjectResponse(
                                lesson.getSubject().getId(),
                                lesson.getSubject().getName(),
                                lesson.getSubject().getDescription()
                        )
                )).toList();

        return new ScheduleResponse(
                schedule.getDayOfWeek(),
                lessonResponses
        );
    }
}