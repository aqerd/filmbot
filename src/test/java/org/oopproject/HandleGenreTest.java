package org.oopproject;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class HandleGenreTest extends TelegramBot {
    private TelegramBot telegramBot;

    public HandleGenreTest() {
        super("dummy_token");
    }

    @BeforeEach
    void setUp() {
        telegramBot = new TelegramBot("dummy_token");
    }

    @Test
    void testHandleGenreWithValidGenre() {
        String testGenreName = "Action";
        String response = telegramBot.handleGenre(testGenreName, 1L);
        assertTrue(response.contains("Фильмы жанра " + testGenreName + ":"));
    }

    @Test
    void testHandleGenreWithUnknownGenre() {
        String response = telegramBot.handleGenre("NonexistentGenre", 1L);
        assertEquals("Извините, я не знаю такого жанра. Попробуйте другой", response);
    }
}