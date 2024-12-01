package oop.project.commands;

import oop.project.handlers.Message;
import org.junit.jupiter.api.Test;
import java.sql.SQLException;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class HandleSetAgeTest extends BaseHandleTest {
    public HandleSetAgeTest() throws SQLException {
        super();
    }

    @Test
    void testHandleAgeWithValidAge() {
        String response = Message.handleSetAge("25", CHAT_ID);
        assertEquals("Спасибо! Учтем ваш ответ", response);
    }

    @Test
    void testHandleAgeWithInvalidAge() {
        String response = Message.handleSetAge("150", CHAT_ID);
        assertEquals("Пожалуйста, введите корректное число (от 0 до 100)", response);
    }

    @Test
    void testHandleAgeWithNonNumericInput() {
        String response = Message.handleSetAge("invalidAge", CHAT_ID);
        assertEquals("Пожалуйста, введите корректное число (от 0 до 100)", response);
    }
}
