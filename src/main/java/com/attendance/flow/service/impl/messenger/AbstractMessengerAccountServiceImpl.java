package com.attendance.flow.service.impl.messenger;

import com.attendance.flow.model.AppGroup;
import com.attendance.flow.model.MessengerAccount;
import com.attendance.flow.model.User;
import com.attendance.flow.model.dto.appGroup.AppGroupSummaryResponse;
import com.attendance.flow.model.dto.attendanceRecord.StudentAttendanceStatsResponse;
import com.attendance.flow.model.dto.messengerAccount.MessengerAccountResponse;
import com.attendance.flow.model.dto.schedule.ScheduleResponse;
import com.attendance.flow.model.enums.BotState;
import com.attendance.flow.model.enums.Language;
import com.attendance.flow.model.enums.TokenAction;
import com.attendance.flow.repository.AppGroupRepository;
import com.attendance.flow.repository.MessengerAccountRepository;
import com.attendance.flow.service.AttendanceService;
import com.attendance.flow.service.MessengerAccountService;
import com.attendance.flow.service.ScheduleService;
import com.attendance.flow.service.VerificationTokenService;
import org.springframework.transaction.annotation.Transactional;

import java.time.DayOfWeek;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;

public abstract class AbstractMessengerAccountServiceImpl<T extends MessengerAccount> implements MessengerAccountService {

    protected final MessengerAccountRepository messengerAccountRepository;
    protected final AppGroupRepository appGroupRepository;

    protected final AttendanceService attendanceService;
    protected final VerificationTokenService verificationTokenService;
    protected final ScheduleService scheduleService;

    private final Supplier<T> accountSupplier;

    protected AbstractMessengerAccountServiceImpl(
            MessengerAccountRepository messengerAccountRepository,
            AppGroupRepository appGroupRepository,
            AttendanceService attendanceService,
            VerificationTokenService verificationTokenService,
            ScheduleService scheduleService,
            Supplier<T> accountSupplier) {

        this.messengerAccountRepository = messengerAccountRepository;
        this.appGroupRepository = appGroupRepository;
        this.attendanceService = attendanceService;
        this.verificationTokenService = verificationTokenService;
        this.scheduleService = scheduleService;
        this.accountSupplier = accountSupplier;
    }

    @Override
    @Transactional
    public Optional<MessengerAccountResponse> registerAccount(String chatId, String username) {
        Optional<MessengerAccount> accountOpt = messengerAccountRepository.findByChatId(chatId);

        if (accountOpt.isPresent()) {
            return Optional.of(mapToDto(accountOpt.get()));
        }

        T newAccount = accountSupplier.get();

        newAccount.setChatId(chatId);
        newAccount.setUsername(username);
        newAccount.setBotState(BotState.AWAITING_TOKEN);
        newAccount.setNotification(true);

        messengerAccountRepository.save(newAccount);

        return Optional.of(mapToDto(newAccount));
    }

    @Override
    @Transactional
    public void changeNotificationStatus(String chatId, boolean status) {
        MessengerAccount account = messengerAccountRepository.findByChatId(chatId).orElse(null);
        if (account == null) {
            return;
        }
        account.setNotification(status);
        messengerAccountRepository.save(account);
    }

    @Override
    @Transactional
    public void changeLanguage(String chatId, Language language) {
        MessengerAccount account = messengerAccountRepository.findByChatId(chatId).orElse(null);
        if (account == null) {
            return;
        }
        account.setLanguage(language);
        messengerAccountRepository.save(account);
    }

    @Override
    @Transactional
    public Optional<MessengerAccountResponse> getAccountByChatId(String chatId) {
        MessengerAccount account = messengerAccountRepository.findByChatId(chatId).orElse(null);
        if (account == null) {
            return Optional.empty();
        }
        return Optional.of(mapToDto(account));
    }

    @Override
    public Optional<User> getUserByChatId(String chatId) {
        return Optional.empty();
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<MessengerAccountResponse> getAccountWithUserAndGroup(String chatId) {
        MessengerAccount account = messengerAccountRepository.findByChatId(chatId).orElse(null);

        if (account == null || account.getUser() == null) {
            return Optional.empty();
        }

        return Optional.of(mapToDto(account));
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<ScheduleResponse> getDailySchedule(Long groupId, DayOfWeek dayOfWeek) {
        return Optional.ofNullable(scheduleService.getDailyScheduleForMessengerAccount(groupId, dayOfWeek));
    }

    @Override
    @Transactional
    public Optional<MessengerAccountResponse> checkPendingToken(String chatId, String token) {
        MessengerAccount account = messengerAccountRepository.findByChatId(chatId).orElse(null);

        Optional<User> userOpt = verificationTokenService.validateToken(token, TokenAction.LINK_TELEGRAM);

        if (account != null && BotState.AWAITING_TOKEN.equals(account.getBotState())) {
            if (userOpt.isPresent()) {
                User user = userOpt.get();

                account.setUser(user);
                account.setBotState(BotState.NORMAL);
                messengerAccountRepository.save(account);
                return Optional.of(mapToDto(account));
            }
        }
        return Optional.empty();
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<StudentAttendanceStatsResponse> getStudentStats(String chatId, Long groupId) {
        MessengerAccount account = messengerAccountRepository.findByChatId(chatId).orElse(null);

        if (account == null || account.getUser() == null) {
            return Optional.empty();
        }

        return Optional.ofNullable(attendanceService.getStudentStats(account.getUser().getId(), groupId));
    }

    @Override
    @Transactional
    public void unlinkMessengerAccount(String chatId) {
        messengerAccountRepository.findByChatId(chatId).ifPresent(account -> {
            if (account.getUser() != null) {
                account.setUser(null);
                account.setBotState(BotState.AWAITING_TOKEN);
                messengerAccountRepository.save(account);
            }
        });
    }

    private MessengerAccountResponse mapToDto(MessengerAccount account) {
        List<AppGroupSummaryResponse> appGroupSummaryResponse = new ArrayList<>();
        Long userId = null;
        String firstName = null;
        String lastName = null;

        if (account.getUser() != null) {
            userId = account.getUser().getId();
            firstName = account.getUser().getFirstName();
            lastName = account.getUser().getLastName();
            List<AppGroup> groups = appGroupRepository.findAllByStudentsId(userId);

            for (AppGroup group : groups) {
                appGroupSummaryResponse.add(new AppGroupSummaryResponse(
                        group.getId(),
                        group.getName(),
                        group.getDescription(),
                        0
                ));
            }
        }

        return new MessengerAccountResponse(
                account.getId(),
                account.getChatId(),
                account.getUsername(),
                account.getBotState(),
                account.isNotification(),
                account.getLanguage(),
                userId,
                firstName,
                lastName,
                appGroupSummaryResponse
        );
    }
}
