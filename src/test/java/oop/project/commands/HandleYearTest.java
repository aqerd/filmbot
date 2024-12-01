package oop.project.commands;

import oop.project.handlers.Message;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import java.sql.SQLException;
import java.time.Year;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class HandleYearTest extends BaseHandleTest {
    public HandleYearTest() throws SQLException {
        super();
    }

    @Disabled
    void testHandleYearWithValidYear() {
        String testYear = "2005";
        String response = Message.handleYear(testYear, CHAT_ID);
        assertTrue(response.contains("Фильмы, выпущенные в " + testYear + " году:"));
    }

    @Test
    void testHandleYearWithInvalidYear() {
        String response = Message.handleYear("1800", CHAT_ID);
        int maxYear = Year.now().getValue() + 5;
        String expected = "Пожалуйста, введите год в диапазоне от 1895 до " + maxYear;
        assertEquals(expected, response);
    }

    @Test
    void testHandleYearWithNonNumericInput() {
        String response = Message.handleYear("Invalid year", CHAT_ID);
        int maxYear = Year.now().getValue() + 5;
        String expected = "Пожалуйста, введите год в диапазоне от 1895 до " + maxYear;
        assertEquals(expected, response);
    }
}
