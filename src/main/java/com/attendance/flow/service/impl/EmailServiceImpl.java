package com.attendance.flow.service.impl;

import com.attendance.flow.service.EmailService;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EmailServiceImpl implements EmailService {

    private final JavaMailSender mailSender;

    @Override
    public void sendWelcomeMessage(String toEmail, String firstName, String confirmationUrl) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom("attendanceflowcorp@gmail.com");
        message.setTo(toEmail);
        message.setSubject("Registration confirmation - Attendance Flow");
        message.setText("Hello, " + firstName + "!\n\n" +
                "Thank you for registering with Attendance Flow.\n" +
                "To activate your account, please follow the link below:\n\n" +
                confirmationUrl + "\n\n" +
                "The link is valid for 24 hours.\n" +
                "If you haven't registered, just ignore this email.");

        mailSender.send(message);
    }

    @Override
    public void sendPasswordResetMessage(String toEmail, String resetUrl) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom("attendanceflowcorp@gmail.com");
        message.setTo(toEmail);
        message.setSubject("Password reset confirmation - Attendance Flow");
        message.setText("\"Hello!\\n\\n\" +\n" +
                "\"We have received a request to reset your password for your account.\\n\" +\n" +
                "\"To create a new password, please follow the link:\\n\\n\" +\n" +
                resetUrl + "\\n\\n\" +\n" +
                "\"The link is valid for 1 hour. If you did not make this request, simply ignore this email.");
    }
}
