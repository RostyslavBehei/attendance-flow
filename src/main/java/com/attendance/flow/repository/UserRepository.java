package com.attendance.flow.repository;

import com.attendance.flow.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);

    boolean existsByEmail(String email);

    boolean existsByPhoneNumber(String phoneNumber);

    @Modifying
    @Query("delete from User u where u.enabled = false and u.createdAt <= :cutoffTime")
    void deleteUnverifiedUsersOlderThan(@Param("cutoffTime") LocalDateTime cutoffTime);
}
