package com.attendance.flow.listener;

import com.attendance.flow.bot.AttendanceTelegramBot;
import com.attendance.flow.model.MessengerAccount;
import com.attendance.flow.model.dto.attendanceRecord.AttendanceMarkRequest;
import com.attendance.flow.model.dto.attendanceRecord.AttendanceMarkedEvent;
import com.attendance.flow.model.dto.attendanceRecord.AttendanceResponse;
import com.attendance.flow.model.enums.AttendanceStatus;
import com.attendance.flow.repository.MessengerAccountRepository;
import lombok.RequiredArgsConstructor;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import java.util.List;

@Component
@RequiredArgsConstructor
public class AttendanceNotificationListener {

    private final MessengerAccountRepository messengerAccountRepository;
    private final AttendanceTelegramBot attendanceTelegramBot;

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void notifyIfIssie(AttendanceMarkedEvent event) {


        if (event.status() == AttendanceStatus.PRESENT) {
            return;
        }

        List<MessengerAccount> accounts = messengerAccountRepository.findAllByUserId(event.studentId());

        if (accounts.isEmpty()) {
            return;
        }

        String messageText = switch (event.status()) {
            case ABSENT -> "\uD83D\uDEA8 **Attention!** You have received a pass (H).";
            case LATE -> "\uD83C\uDFC3 **Please note!** Delay has been noted.";
            case EXCUSE -> "\uD83C\uDFE5 An absence for a valid reason has been noted.";
            default -> "ℹ️ Your attendance status has been updated.";
        };

        for (MessengerAccount messengerAccount : accounts) {
            if (messengerAccount.isNotification()) {
                attendanceTelegramBot.sendNotification(messengerAccount.getChatId(), messageText);
            }
        }
    }
}
