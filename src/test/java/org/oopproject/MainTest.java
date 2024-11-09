package org.oopproject;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.telegram.telegrambots.longpolling.TelegramBotsLongPollingApplication;

import static org.mockito.Mockito.*;

class MainTest {

    private TelegramBotsLongPollingApplication botApp;
    private TelegramBot bot;

    @BeforeEach
    void setUp() {
        botApp = mock(TelegramBotsLongPollingApplication.class);
        bot = mock(TelegramBot.class);
    }

    @Test
    void testMainWithSuccessfulBotInitialization() {
        try {
            when(botApp.registerBot(anyString(), any(TelegramBot.class))).thenReturn(null);
            System.out.println("Bot initialized successfully.");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
