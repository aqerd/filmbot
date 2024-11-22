package oop.project;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.sql.SQLException;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class HandleYearTest extends TelegramBot {
    private TelegramBot telegramBot;
    private final long CHAT_ID = 1L;

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
        String response = telegramBot.handleYear(testYear, CHAT_ID);
        assertTrue(response.contains("Фильмы, выпущенные в " + testYear + " году:"));
    }

    @Test
    void testHandleYearWithInvalidYear() {
        String response = telegramBot.handleYear("1800", CHAT_ID);
        assertEquals("Пожалуйста, введите год в диапазоне от 1895 до 2024", response);
    }

    @Test
    void testHandleYearWithNonNumericInput() {
        String response = telegramBot.handleYear("Invalid year", CHAT_ID);
        assertEquals("Пожалуйста, введите год в диапазоне от 1895 до 2024", response);
    }
}
