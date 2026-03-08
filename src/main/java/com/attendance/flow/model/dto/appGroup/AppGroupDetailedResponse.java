package com.attendance.flow.model.dto.appGroup;

import com.attendance.flow.model.dto.user.UserSummaryResponse;

import java.util.List;

public record AppGroupDetailedResponse(
        Long id,
        String name,
        String description,
        String inviteCode,
        Integer lessonDurationMinutes,
        List<UserSummaryResponse> teachers,
        List<UserSummaryResponse> students
) {
}
