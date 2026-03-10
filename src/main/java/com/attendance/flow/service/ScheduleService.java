package com.attendance.flow.service;

import com.attendance.flow.model.dto.schedule.ScheduleCreateRequest;
import com.attendance.flow.model.dto.schedule.ScheduleResponse;
import com.attendance.flow.model.dto.schedule.ScheduleUpdateRequest;

import java.util.List;

public interface ScheduleService {
    List<ScheduleResponse> getGroupSchedules(Long groupId);
    ScheduleResponse createSchedule(Long groupId, ScheduleCreateRequest request);
    ScheduleResponse updateSchedule(Long scheduleId, ScheduleUpdateRequest request);
    void deleteSchedule(Long id);
}
