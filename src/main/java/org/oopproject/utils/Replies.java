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
                        /moviesearch - Поиск фильма
                        /actorsearch - Поиск актёра
                        /similar - Похожие фильмы
                        /recommended - Рекомендуемые фильмы
                        /popular - Популярные фильмы
                        /toprated - Высоко-оценённые фильмы
                        /findbyid - поиск фильма по ID TMDB
                        /setage - Установить возрастное ограничение
                        /help - Справка""");
        replies.put("help", """
                        Доступны следующие команды:
                        /genre - Поиск по жанру
                        /year - Поиск по году
                        /moviesearch - Поиск фильма
                        /actorsearch - Поиск актёра
                        /similar - Похожие фильмы
                        /recommended - Рекомендуемые фильмы
                        /popular - Популярные фильмы
                        /toprated - Высоко-оценённые фильмы
                        /findbyid - поиск фильма по ID TMDB
                        /setage - Установить возрастное ограничение""");
        replies.put("year", "Введите год, и я найду фильмы, выпущенные в этом году");
        replies.put("movie search", "Введите фильм который вы хотите найти");
        replies.put("actor search", "Введите актёра который вы хотите найти");
        replies.put("similar", "Введите ID фильма к которому вы хотите найти похожие фильмы");
        replies.put("recommended", "Введите ID фильма к которому вы хотите найти рекомендации");
        replies.put("find by id", "Введите ID фильма из сервиса TMDB");
        replies.put("set age", "Введите ваш полный возраст");
        replies.put("genre", "Введите жанр, и я найду фильмы по нему");
        replies.put("unknown", "Команда не распознана. Введите /help для получения списка команд");
    }

    public static String getReply(String command) {
        return replies.getOrDefault(command, "Вы не должны это видеть \uD83D\uDC40");
    }
}