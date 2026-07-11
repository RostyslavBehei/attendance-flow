package com.attendance.flow.model.dto.messengerAccount;

import com.attendance.flow.model.dto.appGroup.AppGroupSummaryResponse;
import com.attendance.flow.model.enums.BotState;
import com.attendance.flow.model.enums.Language;

import java.util.List;

public record MessengerAccountResponse(
        Long id,
        String chatId,
        String username,
        BotState botState,
        boolean notification,
        Language language,
        Long userId,
        String firstName,
        String lastName,
        List<AppGroupSummaryResponse> groups
) {
}
