package org.oopproject;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.sql.SQLException;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class HandleYearTest extends TelegramBot {
    private TelegramBot telegramBot;

    public HandleYearTest() throws SQLException {
        super("dummy_token");
    }

    @BeforeEach
    void setUp() throws SQLException {
        telegramBot = new TelegramBot("dummy_token");
    }

    @Test
    void testHandleYearWithValidYear() {
        String testYear = "2005";
        String response = telegramBot.handleYear(testYear, 1L);
        assertTrue(response.contains("Фильмы, выпущенные в " + testYear + " году:"));
    }

    @Test
    void testHandleYearWithInvalidYear() {
        String response = telegramBot.handleYear("1800", 1L);
        assertEquals("Пожалуйста, введите год в диапазоне от 1900 до 2024", response);
    }

    @Test
    void testHandleYearWithNonNumericInput() {
        String response = telegramBot.handleYear("Invalid year", 1L);
        assertEquals("Пожалуйста, введите корректный год!", response);
    }
}
