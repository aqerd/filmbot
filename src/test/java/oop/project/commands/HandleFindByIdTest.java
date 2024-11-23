package oop.project.commands;

import org.junit.jupiter.api.Test;
import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class HandleFindByIdTest extends BaseHandleTest {
    public HandleFindByIdTest() throws SQLException {
        super();
    }

    @Test
    void testHandleFindByIdWithValidId() {
        String validId = "123";
        String response = telegramBot.handleFindById(validId, CHAT_ID);
        assertTrue(response.contains("Vote average:"), "Responses should include film's vote average.");
        assertTrue(response.contains("Runtime:"), "Responses should include film's runtime.");
    }

    @Test
    void testHandleFindByIdWithInvalidId() {
        String invalidId = "invalid";
        String response = telegramBot.handleFindById(invalidId, CHAT_ID);

        // Проверяем, что возвращается сообщение об ошибке валидации ID
        assertEquals("Пожалуйста, введите корректный ID!", response, "Responses should indicate invalid ID format.");
    }

    @Test
    void testHandleFindByIdWithUnknownId() {
        String unknownId = "1212"; // Идентификатор фильма, который не существует
        String response = telegramBot.handleFindById(unknownId, CHAT_ID);

        // Проверяем, что возвращается сообщение о том, что фильм не найден
        assertEquals("Фильм с таким ID не найден", response, "Responses should indicate the film was not found.");
    }

    @Test
    void testHandleFindByIdWithError() {
        String invalidData = "";
        String response = telegramBot.handleFindById(invalidData, CHAT_ID);
        assertEquals("Пожалуйста, введите корректный ID!", response, "Responses should indicate an internal error occurred.");
    }
}
