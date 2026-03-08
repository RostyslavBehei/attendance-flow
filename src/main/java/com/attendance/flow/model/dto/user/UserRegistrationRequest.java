package com.attendance.flow.model.dto.user;

import com.attendance.flow.model.enums.Role;
import com.attendance.flow.model.enums.VerificationStatus;
import jakarta.validation.constraints.*;

import java.time.LocalDate;

public record UserRegistrationRequest(
        @NotBlank(message = "{user.firstName.notBlank}")
        String firstName,

        @NotBlank(message = "{user.lastName.notBlank}")
        String lastName,

        String middleName,

        @NotNull(message = "{user.birthday.notNull}")
        @Past(message = "{user.birthday.past}")
        LocalDate birthday,

        @NotBlank(message = "{user.phoneNumber.notBlank}")
        String phoneNumber,

        @NotBlank(message = "{user.email.notBlank}")
        @Email(message = "{user.email.invalid}")
        String email,

        @NotBlank(message = "{user.password.notBlank}")
        @Size(min = 8, max = 24, message = "{user.password.size}")
        String password,

        @NotNull(message = "{user.role.notNull}")
        Role role,

        VerificationStatus verificationStatus
) {
        public static UserRegistrationRequest empty() {
                return new UserRegistrationRequest(
                        null, null, null, null, null, null, null, null, null);
        }
}
