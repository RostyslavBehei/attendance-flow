package com.attendance.flow.service.impl;

import com.attendance.flow.exception.AlreadyExistsException;
import com.attendance.flow.exception.NotFoundException;
import com.attendance.flow.exception.VerificationStatusException;
import com.attendance.flow.model.User;
import com.attendance.flow.model.dto.user.*;
import com.attendance.flow.model.enums.Role;
import com.attendance.flow.model.enums.VerificationStatus;
import com.attendance.flow.repository.UserRepository;
import com.attendance.flow.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional(readOnly = true)
    public Page<UserSummaryResponse> getAllUsers(Pageable pageable) {
        Page<User> users = userRepository.findAll(pageable);
        return users.map(this::mapToSummaryDto);
    }

    @Override
    @Transactional
    public UserProfileResponse registerUser(UserRegistrationRequest request) {
        if (userRepository.existsByEmail(request.email())) {
            throw new AlreadyExistsException("User with email already exists: " + request.email());
        }

        VerificationStatus verificationStatus = (request.role() == Role.ROLE_PRINCIPAL) ? VerificationStatus.PENDING : VerificationStatus.NOT_REQUIRED;

        String hashedPassword = passwordEncoder.encode(request.password());

        User newUser = User.builder()
                .firstName(request.firstName())
                .lastName(request.lastName())
                .middleName(request.middleName())
                .birthday(request.birthday())
                .phoneNumber(request.phoneNumber())
                .email(request.email())
                .password(hashedPassword)
                .role(request.role())
                .enabled(true)
                .verificationStatus(verificationStatus)
                .build();

        User savedUser = userRepository.save(newUser);

        return mapToDto(savedUser);
    }

    @Override
    @Transactional(readOnly = true)
    public UserProfileResponse getUserProfileById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("User with id " + id + " not found"));
        return mapToDto(user);
    }

    @Override
    @Transactional
    public UserProfileResponse updateUserProfileById(Long id, UserUpdateRequest request) {
        User existingUser = userRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("User with id " + id + " not found"));

        if (request.firstName() != null) existingUser.setFirstName(request.firstName());
        if (request.lastName() != null) existingUser.setLastName(request.lastName());
        if (request.middleName() != null) existingUser.setMiddleName(request.middleName());
        if (request.birthday() != null) existingUser.setBirthday(request.birthday());
        if (request.phoneNumber() != null) existingUser.setPhoneNumber(request.phoneNumber());

        if (request.email() != null && !request.email().equals(existingUser.getEmail())) {
            if (userRepository.existsByEmail(request.email())) {
                throw new AlreadyExistsException("User with email already exists: " + request.email());
            }
            existingUser.setEmail(request.email());
        }

        if (request.password() != null) existingUser.setPassword(passwordEncoder.encode(request.password()));
        if (request.avatarUrl() != null) existingUser.setAvatarUrl(request.avatarUrl());

        User savedUser = userRepository.save(existingUser);
        return mapToDto(savedUser);
    }

    @Override
    @Transactional
    @PreAuthorize("hasAnyRole('OWNER', 'ADMIN')")
    public UserProfileResponse verifyPrincipal(Long principalId, ChangeVerificationStatusRequest request) {
        User principal = userRepository.findById(principalId)
                .orElseThrow(() -> new NotFoundException("Principal with id " + principalId + " not found"));

        if (principal.getRole() != Role.ROLE_PRINCIPAL) {
            throw new VerificationStatusException("User with id " + principalId + " is not principal");
        }

        principal.setVerificationStatus(request.status());
        User savedUser = userRepository.save(principal);

        return mapToDto(savedUser);
    }

    @Override
    @Transactional
    public void deleteUserById(Long id) {
        User existingUser = userRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("User with id " + id + " not found"));
        userRepository.delete(existingUser);
    }

    private UserProfileResponse mapToDto(User user) {
        return new UserProfileResponse(
                user.getId(),
                user.getFirstName(),
                user.getLastName(),
                user.getMiddleName(),
                user.getBirthday(),
                user.getAvatarUrl(),
                user.getPhoneNumber(),
                user.getEmail(),
                user.getRole().name(),
                user.getGroups(),
                user.isEnabled(),
                user.getVerificationStatus().name(),
                user.getCreatedAt(),
                user.getUpdatedAt()
        );
    }

    private UserSummaryResponse mapToSummaryDto(User user) {
        return new UserSummaryResponse(
                user.getId(),
                user.getFirstName(),
                user.getLastName(),
                user.getMiddleName(),
                user.getAvatarUrl(),
                user.getRole().name(),
                user.isEnabled()
        );
    }
}
