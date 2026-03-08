package com.attendance.flow.model.dto.appGroup;

public record AppGroupSummaryResponse(
        Long id,
        String name,
        String description,
        int studentCount
) {
}
