package com.attendance.flow.model.dto.appGroup;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

public record AppGroupCreateRequest(
        @NotBlank(message = "{appGroup.name.notBlank}")
        String name,

        @Size(max = 255, message = "{appGroup.description.size}")
        String description,

        @NotNull(message = "{appGroup.lessonDurationMinutes.notNull}")
        @Positive(message = "{appGroup.lessonDurationMinutes.positive}")
        Integer lessonDurationMinutes
) {
}
