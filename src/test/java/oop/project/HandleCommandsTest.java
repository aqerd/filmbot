package oop.project;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.sql.SQLException;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static oop.project.shared.Replies.reply;

public class HandleCommandsTest extends TelegramBot {
    private TelegramBot telegramBot;

    public HandleCommandsTest() throws SQLException {
        super("dummy_token");
    }

    @BeforeEach
    void setUp() throws SQLException {
        telegramBot = new TelegramBot("dummy_token");
    }

    @Test
    void testHandleStartCommand() {
        String response = telegramBot.handleCommands("/start", 1L);
        assertEquals(reply("start"), response);
    }

    @Test
    void testHandleGenreCommand() {
        String response = telegramBot.handleCommands("/genre", 1L);
        assertEquals(reply("genre"), response);
    }

    @Test
    void testHandleYearCommand() {
        String response = telegramBot.handleCommands("/year", 1L);
        assertEquals(reply("year"), response);
    }

    @Test
    void testHandleUnknownCommand() {
        String response = telegramBot.handleCommands("/unknown", 1L);
        assertEquals(reply("unknown"), response);
    }
}
