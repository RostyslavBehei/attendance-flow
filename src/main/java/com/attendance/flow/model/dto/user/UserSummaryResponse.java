package com.attendance.flow.model.dto.user;

public record UserSummaryResponse(
        Long id,
        String firstName,
        String lastName,
        String middleName,
        String avatarUrl,
        String role,
        boolean enable
) {
}
