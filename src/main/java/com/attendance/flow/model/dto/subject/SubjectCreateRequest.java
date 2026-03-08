package com.attendance.flow.model.dto.subject;

import jakarta.validation.constraints.NotBlank;

public record SubjectCreateRequest(
        @NotBlank(message = "{subject.name.notBlank}")
        String name,
        String description
) {
}
