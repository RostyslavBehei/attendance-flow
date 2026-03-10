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

    void deleteUserById(Long id);
}
