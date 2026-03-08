package com.attendance.flow.model.dto.user;

import jakarta.validation.constraints.*;

import java.time.LocalDate;

public record UserUpdateRequest(
        String firstName,
        String lastName,
        String middleName,
        LocalDate birthday,
        String phoneNumber,

        @Email(message = "{user.email.invalid}")
        String email,

        @Size(min = 8, max = 24, message = "{user.password.size}")
        String password,

        String avatarUrl
) {
}
