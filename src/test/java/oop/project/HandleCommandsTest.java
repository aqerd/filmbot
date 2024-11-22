package oop.project;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.sql.SQLException;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static oop.project.shared.Replies.reply;

public class HandleCommandsTest extends TelegramBot {
    private TelegramBot telegramBot;
    private final long CHAT_ID = 1L;

    public HandleCommandsTest() throws SQLException {
        super("dummy_token");
    }

    @BeforeEach
    void setUp() throws SQLException {
        telegramBot = new TelegramBot("dummy_token");
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
