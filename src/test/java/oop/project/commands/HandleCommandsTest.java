package oop.project.commands;

import oop.project.handlers.Message;
import org.junit.jupiter.api.Test;
import java.sql.SQLException;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static oop.project.shared.Replies.reply;

public class HandleCommandsTest extends BaseHandleTest {
    public HandleCommandsTest() throws SQLException {
        super();
    }

    @Test
    void testHandleStartCommand() {
        String response = Message.handleCommands("/start", CHAT_ID); // Вызов через класс
        assertEquals(reply("start"), response);
    }

    @Test
    void testHandleGenreCommand() {
        String response = Message.handleCommands("/genre", CHAT_ID); // Вызов через класс
        assertEquals(reply("genre"), response);
    }

    @Test
    void testHandleYearCommand() {
        String response = Message.handleCommands("/year", CHAT_ID); // Вызов через класс
        assertEquals(reply("year"), response);
    }

    @Test
    void testHandleUnknownCommand() {
        String response = Message.handleCommands("/unknown", CHAT_ID); // Вызов через класс
        assertEquals(reply("unknown"), response);
    }
}
