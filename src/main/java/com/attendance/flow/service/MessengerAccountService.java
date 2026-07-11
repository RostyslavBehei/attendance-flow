package com.attendance.flow.service;

import com.attendance.flow.model.User;
import com.attendance.flow.model.dto.attendanceRecord.StudentAttendanceStatsResponse;
import com.attendance.flow.model.dto.messengerAccount.MessengerAccountResponse;
import com.attendance.flow.model.dto.schedule.ScheduleResponse;
import com.attendance.flow.model.enums.Language;

import java.time.DayOfWeek;
import java.util.Optional;

public interface MessengerAccountService {
    Optional<MessengerAccountResponse> registerAccount(String chatId, String username);
    void changeNotificationStatus(String chatId, boolean status);
    void changeLanguage(String chatId, Language language);

    Optional<MessengerAccountResponse> getAccountByChatId(String chatId);
    Optional<User> getUserByChatId(String chatId);
    Optional<MessengerAccountResponse> getAccountWithUserAndGroup(String chatId);
    Optional<ScheduleResponse> getDailySchedule(Long groupId, DayOfWeek dayOfWeek);

    Optional<MessengerAccountResponse> checkPendingToken(String chatId, String token);

    Optional<StudentAttendanceStatsResponse> getStudentStats(String chatId, Long groupId);

    void unlinkMessengerAccount(String chatId);
}
