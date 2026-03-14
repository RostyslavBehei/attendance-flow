package com.attendance.flow.controller.rest;

import com.attendance.flow.model.dto.user.*;
import com.attendance.flow.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserRestController {

    private final UserService userService;

    @GetMapping
    public ResponseEntity<Page<UserSummaryResponse>> findAllUsers(
            @PageableDefault(size = 20, sort = "id") Pageable pageable) {
        Page<UserSummaryResponse> users = userService.getAllUsers(pageable);
        return ResponseEntity.ok(users);
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserProfileResponse> getUserProfileById(
            @PathVariable Long id) {
        UserProfileResponse user = userService.getUserProfileById(id);
        return ResponseEntity.ok(user);
    }

    @PostMapping("/register")
    public ResponseEntity<UserProfileResponse> registerUser(
            @Valid @RequestBody UserRegistrationRequest request) {
        UserProfileResponse user = userService.registerUser(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(user);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<UserProfileResponse> updateUserProfileById(
            @PathVariable Long id,
            @Valid @RequestBody UserUpdateRequest request) {
        UserProfileResponse user = userService.updateUserProfileById(id, request);
        return ResponseEntity.status(HttpStatus.OK).body(user);
    }

    @PatchMapping("/{id}/verify")
    public ResponseEntity<UserProfileResponse> verifyPrincipal(
            @PathVariable Long id,
            @Valid @RequestBody ChangeVerificationStatusRequest request) {
        UserProfileResponse user = userService.verifyPrincipal(id, request);
        return ResponseEntity.status(HttpStatus.OK).body(user);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUserById(
            @PathVariable Long id) {
        userService.deleteUserById(id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

}
