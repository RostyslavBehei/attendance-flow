package com.attendance.flow.service;

import com.attendance.flow.model.dto.user.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface UserService {
    Page<UserSummaryResponse> getAllUsers(Pageable pageable);
    UserProfileResponse getUserProfileById(Long id);

    UserProfileResponse registerUser(UserRegistrationRequest request);
    UserProfileResponse updateUserProfileById(Long id, UserUpdateRequest request);
    UserProfileResponse verifyPrincipal(Long principalId, ChangeVerificationStatusRequest request);

    boolean verifyUserAccount(String token);

    void processForgotPassword(String email);
    boolean isResetPasswordTokenValid(String token);
    void resetPassword(String token, String newPassword);

    void updateAvatar(String email, String avatarUrl);
    void updateVerificationDocument(String email, String documentUrl);

    void deleteUserById(Long id);
}
