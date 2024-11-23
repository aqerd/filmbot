package oop.project.commands;

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
        String response = telegramBot.handleCommands("/start", CHAT_ID);
        assertEquals(reply("start"), response);
    }

    @Test
    void testHandleGenreCommand() {
        String response = telegramBot.handleCommands("/genre", CHAT_ID);
        assertEquals(reply("genre"), response);
    }

    @Test
    void testHandleYearCommand() {
        String response = telegramBot.handleCommands("/year", CHAT_ID);
        assertEquals(reply("year"), response);
    }

    @Test
    void testHandleUnknownCommand() {
        String response = telegramBot.handleCommands("/unknown", CHAT_ID);
        assertEquals(reply("unknown"), response);
    }
}
