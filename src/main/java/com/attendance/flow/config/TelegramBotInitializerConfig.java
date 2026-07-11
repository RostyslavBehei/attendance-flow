package com.attendance.flow.config;

import com.attendance.flow.bot.TelegramBotController;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

@Component
@RequiredArgsConstructor
public class TelegramBotInitializerConfig {

    private final TelegramBotController telegramBotController;

    @EventListener({ContextRefreshedEvent.class})
    public void init() {
        try {
            TelegramBotsApi telegramBotsApi = new TelegramBotsApi(DefaultBotSession.class);
            telegramBotsApi.registerBot(telegramBotController);
            System.out.println("TelegramBotsApi successfully launched!");
        } catch (TelegramApiException e) {
            System.out.println("TelegramBotsApi failed to launch: " + e.getMessage());
        }
    }
}
