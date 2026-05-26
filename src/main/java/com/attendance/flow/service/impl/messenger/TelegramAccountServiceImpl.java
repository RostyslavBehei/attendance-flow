package com.attendance.flow.service.impl.messenger;

import com.attendance.flow.model.TelegramAccount;
import com.attendance.flow.repository.AppGroupRepository;
import com.attendance.flow.repository.MessengerAccountRepository;
import com.attendance.flow.service.AttendanceService;
import com.attendance.flow.service.ScheduleService;
import com.attendance.flow.service.VerificationTokenService;
import org.springframework.stereotype.Service;

@Service("telegramService")
public class TelegramAccountServiceImpl extends AbstractMessengerAccountServiceImpl<TelegramAccount> {

    public TelegramAccountServiceImpl(
            MessengerAccountRepository messengerAccountRepository,
            AppGroupRepository appGroupRepository,
            AttendanceService attendanceService,
            VerificationTokenService verificationTokenService,
            ScheduleService scheduleService) {

        super(messengerAccountRepository, appGroupRepository, attendanceService,
                verificationTokenService, scheduleService, TelegramAccount::new);
    }
}
