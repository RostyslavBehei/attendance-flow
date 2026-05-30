package com.attendance.flow.service.impl;

import com.attendance.flow.model.User;
import com.attendance.flow.model.VerificationToken;
import com.attendance.flow.model.enums.TokenAction;
import com.attendance.flow.repository.VerificationTokenRepository;
import com.attendance.flow.service.VerificationTokenService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class VerificationTokenServiceImpl implements VerificationTokenService {

    private final VerificationTokenRepository verificationTokenRepository;

    @Override
    @Transactional
    public String generateToken(User user, TokenAction tokenAction, int expirationMinutes, boolean isShort) {
        verificationTokenRepository.deleteByUserAndTokenAction(user, tokenAction);

        String tokenString;

        if (isShort) {
            tokenString = UUID.randomUUID().toString().substring(0, 6).toUpperCase();
        } else {
            tokenString = UUID.randomUUID().toString();
        }

        VerificationToken verificationToken = VerificationToken.builder()
                .token(tokenString)
                .user(user)
                .tokenAction(tokenAction)
                .expiryDate(LocalDateTime.now().plusMinutes(expirationMinutes))
                .build();

        verificationTokenRepository.save(verificationToken);
        return tokenString;
    }

    @Override
    @Transactional
    public Optional<User> validateToken(String tokenString, TokenAction tokenAction) {
        Optional<VerificationToken> tokenOpt = verificationTokenRepository.findByTokenAndTokenAction(tokenString, tokenAction);

        if (tokenOpt.isEmpty() || tokenOpt.get().isExpired()) {
            return Optional.empty();
        }

        User user = tokenOpt.get().getUser();

        verificationTokenRepository.delete(tokenOpt.get());

        return Optional.of(user);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean isTokenValid(String tokenString, TokenAction tokenAction) {
        Optional<VerificationToken> tokenOpt = verificationTokenRepository.findByTokenAndTokenAction(tokenString, tokenAction);
        return tokenOpt.isPresent() && !tokenOpt.get().isExpired();
    }
}
