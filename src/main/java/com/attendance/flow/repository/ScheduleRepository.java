package com.attendance.flow.repository;

import com.attendance.flow.model.Schedule;
import com.attendance.flow.model.dto.schedule.ScheduleResponse;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.DayOfWeek;
import java.util.List;

@Repository
public interface ScheduleRepository extends JpaRepository<Schedule, Long> {
    List<Schedule> findByGroupId(Long groupId);
    boolean existsByGroupIdAndDayOfWeek(Long groupId, DayOfWeek dayOfWeek);
}
