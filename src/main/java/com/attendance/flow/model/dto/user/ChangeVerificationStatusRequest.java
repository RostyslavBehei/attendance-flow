package com.attendance.flow.model.dto.user;

import com.attendance.flow.model.enums.VerificationStatus;
import jakarta.validation.constraints.NotNull;

public record ChangeVerificationStatusRequest(
        @NotNull(message = "{user.verificationStatus.notNull}")
        VerificationStatus status
) {
}
