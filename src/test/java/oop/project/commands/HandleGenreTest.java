package oop.project.commands;

import oop.project.handlers.Message;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import java.sql.SQLException;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class HandleGenreTest extends BaseHandleTest {
    public HandleGenreTest() throws SQLException {
        super();
    }

    @Disabled
    void testHandleGenreWithValidGenre() {
        String testGenreName = "Action";
        String response = Message.handleGenre(testGenreName, CHAT_ID);
        assertTrue(response.contains("Фильмы жанра " + testGenreName + ":"));
    }

    @Disabled
    void testHandleGenreWithUnknownGenre() {
        String response = Message.handleGenre("NonexistentGenre", CHAT_ID);
        assertEquals("Извините, я не знаю такого жанра. Попробуйте другой", response);
    }
}