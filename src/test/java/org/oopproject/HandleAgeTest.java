package org.oopproject;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class HandleAgeTest extends TelegramBot {
    private TelegramBot telegramBot;

    public HandleAgeTest() {
        super("dummy_token");
    }

    @BeforeEach
    void setUp() {
        telegramBot = new TelegramBot("dummy_token");
    }

    @Test
    void testHandleAgeWithValidAge() {
        String response = telegramBot.handleAge("25", 1L);
        assertEquals("Спасибо! Учтем ваш ответ", response);
    }

    @Test
    void testHandleAgeWithInvalidAge() {
        String response = telegramBot.handleAge("150", 1L);
        assertEquals("Пожалуйста, введите корректное число (от 0 до 100)", response);
    }

    @Test
    void testHandleAgeWithNonNumericInput() {
        String response = telegramBot.handleAge("invalidAge", 1L);
        assertEquals("Пожалуйста, введите число", response);
    }
}
