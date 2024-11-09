package org.oopproject;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.oopproject.utils.Replies.getReply;

class TelegramBotTest extends TelegramBot {

    private TelegramBot telegramBot;

    public TelegramBotTest() {
        super("dummy_token");
    }

    @BeforeEach
    void setUp() {
        telegramBot = new TelegramBot("dummy_token");
    }

    @Test
    void testHandleStartCommand() {
        String response = telegramBot.handleCommands("/start", 12345L);
        assertEquals(getReply("start"), response);
    }

    @Test
    void testHandleGenreCommand() {
        String response = telegramBot.handleCommands("/genre", 12345L);
        assertEquals(getReply("genre"), response);
    }

    @Test
    void testHandleUnknownCommand() {
        String response = telegramBot.handleCommands("/unknown", 12345L);
        assertEquals(getReply("unknown"), response);
    }

    @Test
    void testHandleYearWithValidYear() {
        String testYear = "2005";
        String response = telegramBot.handleYear(testYear, 12345L);
        assertTrue(response.contains("Фильмы, выпущенные в " + testYear + " году:"));
    }

    @Test
    void testHandleYearWithInvalidYear() {
        String response = telegramBot.handleYear("1800", 12345L);
        assertEquals("Пожалуйста, введите год в диапазоне от 1900 до 2024", response);
    }

    @Test
    void testHandleYearWithNonNumericInput() {
        String response = telegramBot.handleYear("abcd", 12345L);
        assertEquals("Пожалуйста, введите корректный год!", response);
    }

    @Test
    void testHandleGenreWithValidGenre() {
        String testGenreName = "Action";
        String response = telegramBot.handleGenre(testGenreName, 12345L);
        System.out.println(response);
        assertTrue(response.contains("Фильмы жанра " + testGenreName + ":"));
    }

    @Test
    void testHandleGenreWithUnknownGenre() {
        String response = telegramBot.handleGenre("NonexistentGenre", 12345L);
        assertEquals("Извините, я не знаю такого жанра. Попробуйте другой", response);
    }

    @Test
    void testHandleAgeWithValidAge() {
        String response = telegramBot.handleAge("25", 12345L);
        assertEquals("Спасибо! Учтем ваш ответ", response);
    }

    @Test
    void testHandleAgeWithInvalidAge() {
        String response = telegramBot.handleAge("150", 12345L);
        assertEquals("Пожалуйста, введите корректное число (от 0 до 100)", response);
    }

    @Test
    void testHandleAgeWithNonNumericInput() {
        String response = telegramBot.handleAge("invalidAge", 12345L);
        assertEquals("Пожалуйста, введите число", response);
    }

}
