package com.attendance.flow.model.dto.appGroup;

import jakarta.validation.constraints.NotBlank;

public record AppGroupJoinRequest(
        @NotBlank(message = "{appGroup.inviteCode.notBlank}")
        String inviteCode
) {
}
