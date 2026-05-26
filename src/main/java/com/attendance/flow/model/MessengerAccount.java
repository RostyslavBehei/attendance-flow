package com.attendance.flow.model;

import com.attendance.flow.model.enums.BotState;
import com.attendance.flow.model.enums.Language;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "messenger_accounts", schema = "attendance")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "messenger_type", discriminatorType = DiscriminatorType.STRING)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public abstract class MessengerAccount {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "chat_id")
    private String chatId;

    @Column(name = "username")
    private String username;

    @Column(name = "bot_state")
    @Enumerated(EnumType.STRING)
    private BotState botState;

    private boolean notification = true;

    @Enumerated(EnumType.STRING)
    private Language language = Language.ENGLISH;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;
}
