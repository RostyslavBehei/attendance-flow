package com.attendance.flow.repository;

import com.attendance.flow.model.AppGroup;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AppGroupRepository extends JpaRepository<AppGroup, Long> {
    boolean existsByInviteCode(String inviteCode);

    Optional<AppGroup> findByInviteCode(String inviteCode);

    List<AppGroup> findAllByStudentsId(Long studentId);
    List<AppGroup> findAllByTeachersId(Long teacherId);
}
