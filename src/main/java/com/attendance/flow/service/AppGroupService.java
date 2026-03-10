package com.attendance.flow.service;

import com.attendance.flow.model.dto.appGroup.*;

import java.util.List;

public interface AppGroupService {
    AppGroupDetailedResponse createAppGroup(Long principalId, AppGroupCreateRequest request);
    AppGroupDetailedResponse joinAppGroup(Long studentId, AppGroupJoinRequest request);
    AppGroupDetailedResponse addTeacherToGroup(Long adminId, Long groupId, AddTeacherRequest request);
    List<AppGroupSummaryResponse> getMyGroups(Long userId);
}
