package oop.project.commands;

import oop.project.handlers.Commands;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import java.sql.SQLException;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class HandleGenreTest extends BaseHandleTest {
    public HandleGenreTest() throws SQLException {
        super();
    }

    @Test
    void testHandleGenreWithValidGenre() {
        String testGenreName = "Action";
        String response = Commands.handleGenre(testGenreName, CHAT_ID);
        assertTrue(response.contains("Фильмы жанра " + testGenreName + ":"));
    }

    @Disabled
    void testHandleGenreWithUnknownGenre() {
        String response = Commands.handleGenre("NonexistentGenre", CHAT_ID);
        assertEquals("Извините, я не знаю такого жанра. Попробуйте другой", response);
    }
}