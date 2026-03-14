package com.attendance.flow.controller.rest;

import com.attendance.flow.model.dto.appGroup.*;
import com.attendance.flow.service.AppGroupService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/groups")
@RequiredArgsConstructor
public class AppGroupRestController {

    private final AppGroupService appGroupService;

    @GetMapping
    public ResponseEntity<List<AppGroupSummaryResponse>> getMyGroups(
            @RequestParam Long userId) {
        List<AppGroupSummaryResponse> appGroups = appGroupService.getMyGroups(userId);
        return ResponseEntity.ok(appGroups);
    }

    @PostMapping
    public ResponseEntity<AppGroupDetailedResponse> createAppGroup(
            @RequestParam Long adminId,
            @Valid @RequestBody AppGroupCreateRequest request) {
        AppGroupDetailedResponse appGroup = appGroupService.createAppGroup(adminId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(appGroup);
    }

    @PostMapping("/join")
    public ResponseEntity<AppGroupDetailedResponse> joinAppGroup(
            @RequestParam Long studentId,
            @Valid @RequestBody AppGroupJoinRequest request) {
        AppGroupDetailedResponse appGroup = appGroupService.joinAppGroup(studentId, request);
        return ResponseEntity.ok(appGroup);
    }

    @PostMapping("/{groupId}/teachers")
    public ResponseEntity<AppGroupDetailedResponse> addTeacherToGroup(
            @RequestParam Long adminId,
            @PathVariable Long groupId,
            @Valid @RequestBody AddTeacherRequest request) {
        AppGroupDetailedResponse appGroup = appGroupService.addTeacherToGroup(adminId, groupId, request);
        return ResponseEntity.ok(appGroup);
    }
}
