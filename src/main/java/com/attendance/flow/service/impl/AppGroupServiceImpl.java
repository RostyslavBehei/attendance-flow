package com.attendance.flow.service.impl;

import com.attendance.flow.exception.AccessDeniedException;
import com.attendance.flow.exception.NotFoundException;
import com.attendance.flow.model.AppGroup;
import com.attendance.flow.model.User;
import com.attendance.flow.model.dto.appGroup.*;
import com.attendance.flow.model.dto.user.UserSummaryResponse;
import com.attendance.flow.model.enums.Role;
import com.attendance.flow.model.enums.VerificationStatus;
import com.attendance.flow.repository.AppGroupRepository;
import com.attendance.flow.repository.UserRepository;
import com.attendance.flow.service.AppGroupService;
import com.attendance.flow.exception.AccessDeniedException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AppGroupServiceImpl implements AppGroupService {

    private final UserRepository userRepository;
    private final AppGroupRepository appGroupRepository;

    private static final SecureRandom RANDOM = new SecureRandom();
    private static final String CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
    private static final int CODE_LENGTH = 8;

    @Override
    @Transactional
    public AppGroupDetailedResponse createAppGroup(Long principalId, AppGroupCreateRequest request) {
        User principal = userRepository.findById(principalId)
                .orElseThrow(() -> new NotFoundException("User with id " + principalId + " not found"));

        if (principal .getRole() != Role.ROLE_PRINCIPAL) {
            throw new AccessDeniedException("Only principal can create app groups!");
        }

        if (principal.getVerificationStatus() != VerificationStatus.APPROVED) {
            throw new AccessDeniedException("Your account has not yet been verified by the system owner. You cannot create groups.");
        }

        String inviteCode = generateUniqueInviteCode();

        AppGroup appGroup = AppGroup.builder()
                .name(request.name())
                .description(request.description())
                .lessonDurationMinutes(request.lessonDurationMinutes())
                .inviteCode(inviteCode)
                .build();

        AppGroup savedGroup = appGroupRepository.save(appGroup);

        principal.getGroups().add(appGroup);
        userRepository.save(principal);

        UserSummaryResponse principalDto = new UserSummaryResponse(
                principal.getId(), principal.getFirstName(), principal.getLastName(), principal.getMiddleName() ,principal.getAvatarUrl(), principal.getRole().name(), principal.isEnabled()
        );

        return new AppGroupDetailedResponse(
                savedGroup.getId(),
                savedGroup.getName(),
                savedGroup.getDescription(),
                savedGroup.getInviteCode(),
                savedGroup.getLessonDurationMinutes(),
                List.of(principalDto),
                List.of()
        );
    }

    @Override
    @Transactional
    public AppGroupDetailedResponse joinAppGroup(Long studentId, AppGroupJoinRequest request) {
        User student = userRepository.findById(studentId)
                .orElseThrow(() -> new NotFoundException("User with id " + studentId + " not found"));

        if (student.getRole() != Role.ROLE_STUDENT) {
            throw new AccessDeniedException("Only student can join app groups!");
        }

        AppGroup group = appGroupRepository.findByInviteCode(request.inviteCode())
                .orElseThrow(() -> new NotFoundException("Group with code " + request.inviteCode() + " not found"));

        if (group.getStudents().contains(student)) {
            throw new AccessDeniedException("You are already joined this group!");
        }

        group.getStudents().add(student);
        userRepository.save(student);
        return mapToDetailedResponse(group);
    }

    @Override
    @Transactional
    public AppGroupDetailedResponse addTeacherToGroup(Long principalId, Long groupId, AddTeacherRequest request) {
        AppGroup appGroup = appGroupRepository.findById(groupId)
                .orElseThrow(() -> new NotFoundException("Group with id " + groupId + " not found"));

        User principal  = userRepository.findById(principalId)
                .orElseThrow(() -> new NotFoundException("User with id " + principalId + " not found"));

        if (!appGroup.getTeachers().contains(principal )) {
            throw new AccessDeniedException("You do not have permission to add teachers to this group!");
        }

        User newTeacher = userRepository.findByEmail(request.teacherEmail())
                .orElseThrow(() -> new NotFoundException("Teacher with id " + request.teacherEmail() + " not found"));

        if (newTeacher.getRole() != Role.ROLE_TEACHER && newTeacher.getRole() != Role.ROLE_PRINCIPAL ) {
            throw new AccessDeniedException("Only teachers or other principal can be added!");
        }

        if (appGroup.getTeachers().contains(newTeacher)) {
            throw new AccessDeniedException("This teacher is already attached to the group!");
        }

        appGroup.addTeacher(newTeacher);
        appGroupRepository.save(appGroup);

        return mapToDetailedResponse(appGroup);
    }

    @Override
    @Transactional(readOnly = true)
    public List<AppGroupSummaryResponse> getMyGroups(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User with id " + userId + " not found"));

        List<AppGroup> userGroups;
        if (user.getRole() == Role.ROLE_STUDENT) {
            userGroups = appGroupRepository.findAllByStudentsId(userId);
        } else if (user.getRole() == Role.ROLE_PRINCIPAL  || user.getRole() == Role.ROLE_TEACHER) {
            userGroups = appGroupRepository.findAllByTeachersId(userId);
        } else {
            throw new AccessDeniedException("There are no groups for this role.");
        }

        return userGroups.stream()
                .map(this::mapToSummaryResponse)
                .toList();
    }

    private AppGroupSummaryResponse mapToSummaryResponse(AppGroup group) {
        return new AppGroupSummaryResponse(
                group.getId(),
                group.getName(),
                group.getDescription(),
                group.getStudents().size()
        );
    }

    private AppGroupDetailedResponse mapToDetailedResponse(AppGroup group) {
        List<UserSummaryResponse> teachers = group.getTeachers().stream()
                .map(teacher -> new UserSummaryResponse(
                        teacher.getId(),
                        teacher.getFirstName(),
                        teacher.getLastName(),
                        teacher.getMiddleName(),
                        teacher.getAvatarUrl(),
                        teacher.getRole().name(),
                        teacher.isEnabled()
                )).toList();

        List<UserSummaryResponse> students = group.getStudents().stream()
                .map(student -> new UserSummaryResponse(
                        student.getId(),
                        student.getFirstName(),
                        student.getLastName(),
                        student.getMiddleName(),
                        student.getAvatarUrl(),
                        student.getRole().name(),
                        student.isEnabled()
                )).toList();

        return new AppGroupDetailedResponse(
                group.getId(),
                group.getName(),
                group.getDescription(),
                group.getInviteCode(),
                group.getLessonDurationMinutes(),
                teachers,
                students
        );
    }

    private String generateUniqueInviteCode() {
        String code;
        do {
            StringBuilder sb = new StringBuilder(CODE_LENGTH);
            for (int i = 0; i < CODE_LENGTH; i++) {
                sb.append(CHARACTERS.charAt(RANDOM.nextInt(CHARACTERS.length())));
            }
            code = sb.substring(0, 4) + "-" + sb.substring(4, 8);
        } while (appGroupRepository.existsByInviteCode(code));
        return code;
    }
}
