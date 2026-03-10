package com.attendance.flow.repository;

import com.attendance.flow.model.LessonSession;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Optional;

@Repository
public interface LessonSessionRepository extends JpaRepository<LessonSession, Long> {
    boolean existsByLessonIdAndDateTime(Long lessonId, LocalDateTime dateTime);

    @EntityGraph(attributePaths = {"attendances", "attendances.student", "markedBy"})
    Optional<LessonSession> findByLessonIdAndDateTime(Long lessonId, LocalDateTime dateTime);
}
