package com.attendance.flow.service;

public interface EmailService {
    void sendWelcomeMessage(String toEmail, String firstName, String confirmationUrl);

    void sendPasswordResetMessage(String toEmail, String resetUrl);
}
