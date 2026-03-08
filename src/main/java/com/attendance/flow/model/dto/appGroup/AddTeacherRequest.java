package com.attendance.flow.model.dto.appGroup;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record AddTeacherRequest(
        @NotBlank(message = "{appGroup.teacherEmail.notBlank}")
        @Email(message = "{appGroup.teacherEmail.invalid}")
        String teacherEmail
) {
}
