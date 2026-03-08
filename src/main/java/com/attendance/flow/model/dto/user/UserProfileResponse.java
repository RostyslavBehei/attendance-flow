package com.attendance.flow.model.dto.user;

import com.attendance.flow.model.AppGroup;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Set;

public record UserProfileResponse(
        Long id,
        String firstName,
        String lastName,
        String middleName,
        LocalDate birthday,
        String avatarUrl,
        String phoneNumber,
        String email,
        String role,
        Set<AppGroup> groups,
        boolean enabled,
        String verificationStatus,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {

}
