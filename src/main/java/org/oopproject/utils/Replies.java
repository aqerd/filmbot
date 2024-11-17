package org.oopproject.utils;

import java.util.HashMap;
import java.util.Map;

public class Replies {
    private static final Map<String, String> replies = new HashMap<>();

    static {
        replies.put("start", """
                        Привет! Я бот по поиску фильмов.
                        У меня есть следующие команды:
                        /genre - Поиск по жанру
                        /year - Поиск по году
                        /help - Справка
                        /stage - Установить возрастное ограничение
                        Введи команду!""");
        replies.put("genre", """
                        Введите жанр, и я найду фильмы по нему.
                        Вот список доступных жанров:
                        animation, comedy, crime, documentary, drama, family, fantasy, history, horror, music, mystery, romance, science_fiction, tv_movie, thriller, war, western""");
        replies.put("help", """
                        Доступны следующие команды:
                        /genre - Поиск по жанру
                        /year - Поиск по году
                        /setage - Установить возрастное ограничение""");
        replies.put("year", "Введите год, и я найду фильмы, выпущенные в этом году");
        replies.put("movie search", "Введите фильм который вы хотите найти");
        replies.put("actor search", "Введите актёра который вы хотите найти");
        replies.put("similar", "Введите фильм к которому вы хотите найти похожие фильмы");
        replies.put("recommended", "Введите фильм к которому вы хотите найти рекомендации");
        replies.put("find by id", "Введите ID фильма из сервиса The Movie Database");
        replies.put("set age", "Введите ваш полный возраст");
        replies.put("unknown", "Команда не распознана. Попробуйте /help для получения списка команд");
    }

    public static String getReply(String command) {
        return replies.getOrDefault(command, "Вы не должны это видеть \uD83D\uDC40");
    }
}