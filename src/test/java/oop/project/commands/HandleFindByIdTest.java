package oop.project.commands;

import oop.project.handlers.Message;
import org.junit.jupiter.api.Test;
import java.sql.SQLException;
import static oop.project.shared.Replies.reply;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class HandleFindByIdTest extends BaseHandleTest {
    public HandleFindByIdTest() throws SQLException {
        super();
    }

    @Test
    void testHandleFindByIdWithValidId() {
        String validId = "123";
        String response = Message.handleFindById(validId, CHAT_ID);
        assertTrue(response.contains("Vote average:"), "Responses should include film's vote average.");
        assertTrue(response.contains("Runtime:"), "Responses should include film's runtime.");
    }

    @Test
    void testHandleFindByIdWithInvalidId() {
        String invalidId = "invalid";
        String response = Message.handleFindById(invalidId, CHAT_ID);

        // Проверяем, что возвращается сообщение об ошибке валидации ID
        assertEquals("Пожалуйста, введите корректный ID!", response, "Responses should indicate invalid ID format.");
    }

    @Test
    void testHandleFindByIdWithUnknownId() {
        String unknownId = "1212";
        String response = Message.handleFindById(unknownId, CHAT_ID);
        assertEquals(reply("unexpected"), response, "Responses should indicate the film was not found.");
    }

    @Test
    void testHandleFindByIdWithError() {
        String invalidData = "";
        String response = Message.handleFindById(invalidData, CHAT_ID);
        assertEquals("Пожалуйста, введите корректный ID!", response, "Responses should indicate an internal error occurred.");
    }
}
