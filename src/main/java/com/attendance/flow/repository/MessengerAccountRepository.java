package com.attendance.flow.repository;

import com.attendance.flow.model.MessengerAccount;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MessengerAccountRepository extends JpaRepository<MessengerAccount, Long> {
    Optional<MessengerAccount> findByChatId(String chatId);

    @Query("select t from TelegramAccount t left join fetch t.user u left join fetch u.groups where t.chatId = :chatId")
    Optional<MessengerAccount> findWithUserAndGroup(@Param("chatId") String chatId);

    List<MessengerAccount> findAllByUserId(Long userId);
}
