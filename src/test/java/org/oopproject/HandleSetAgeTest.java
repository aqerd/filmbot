package org.oopproject;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.sql.SQLException;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class HandleSetAgeTest extends TelegramBot {
    private TelegramBot telegramBot;

    public HandleSetAgeTest() throws SQLException {
        super("dummy_token");
    }

    @BeforeEach
    void setUp() throws SQLException {
        telegramBot = new TelegramBot("dummy_token");
    }

    @Test
    void testHandleAgeWithValidAge() {
        String response = telegramBot.handleSetAge("25", 1L);
        assertEquals("Спасибо! Учтем ваш ответ", response);
    }

    @Test
    void testHandleAgeWithInvalidAge() {
        String response = telegramBot.handleSetAge("150", 1L);
        assertEquals("Пожалуйста, введите корректное число (от 0 до 100)", response);
    }

    @Test
    void testHandleAgeWithNonNumericInput() {
        String response = telegramBot.handleSetAge("invalidAge", 1L);
        assertEquals("Пожалуйста, введите число", response);
    }
}
