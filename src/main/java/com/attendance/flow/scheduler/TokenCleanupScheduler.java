package com.attendance.flow.scheduler;

import com.attendance.flow.repository.UserRepository;
import com.attendance.flow.repository.VerificationTokenRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
public class TokenCleanupScheduler {

    private final VerificationTokenRepository verificationTokenRepository;
    private final UserRepository userRepository;

    @Scheduled(cron = "0 0 3 * * *")
    @Transactional
    public void cleanUpExpiredTokens() {
        LocalDateTime now = LocalDateTime.now();
        verificationTokenRepository.deleteAllExpiredSince(now);

        LocalDateTime cutoffTime = LocalDateTime.now().minusHours(24);
        userRepository.deleteUnverifiedUsersOlderThan(cutoffTime);
    }
}
