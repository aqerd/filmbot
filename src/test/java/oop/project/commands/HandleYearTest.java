package oop.project.commands;

import oop.project.handlers.Commands;
import org.junit.jupiter.api.Test;
import java.sql.SQLException;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class HandleYearTest extends BaseHandleTest {
    public HandleYearTest() throws SQLException {
        super();
    }

    @Test
    void testHandleYearWithValidYear() {
        String testYear = "2005";
        String response = Commands.handleYear(testYear, CHAT_ID);
        assertTrue(response.contains("Фильмы, выпущенные в " + testYear + " году:"));
    }

    @Test
    void testHandleYearWithInvalidYear() {
        String response = Commands.handleYear("1800", CHAT_ID);
        assertEquals("Пожалуйста, введите год в диапазоне от 1895 до 2024", response);
    }

    @Test
    void testHandleYearWithNonNumericInput() {
        String response = Commands.handleYear("Invalid year", CHAT_ID);
        assertEquals("Пожалуйста, введите год в диапазоне от 1895 до 2024", response);
    }
}
