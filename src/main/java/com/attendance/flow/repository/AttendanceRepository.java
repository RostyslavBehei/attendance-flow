package com.attendance.flow.repository;

import com.attendance.flow.model.Attendance;
import com.attendance.flow.model.enums.AttendanceStatus;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AttendanceRepository extends JpaRepository<Attendance, Long> {
    long countByStudentIdAndStatus(Long studentId, AttendanceStatus status);

    @Query("""
SELECT COUNT('ls') from LessonSession ls
where ls.lesson.schedule.group.id = :groupId
""")
    long countTotalSessionsForGroup(@Param("groupId") Long groupId);

    @EntityGraph(attributePaths = {"lessonSession", "lessonSession.lesson.subject"})
    List<Attendance> findAllByStudentId(Long studentId);
}
