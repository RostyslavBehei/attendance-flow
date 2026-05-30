package com.attendance.flow.service;

import com.attendance.flow.model.User;
import com.attendance.flow.model.enums.TokenAction;

import java.util.Optional;

public interface VerificationTokenService {
    String generateToken(User user, TokenAction tokenAction, int expirationMinutes, boolean isShort);

    Optional<User> validateToken(String tokenString, TokenAction tokenAction);
    boolean isTokenValid(String tokenString, TokenAction tokenAction);
}
