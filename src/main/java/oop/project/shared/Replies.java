package oop.project.shared;

import java.util.HashMap;
import java.util.Map;

public class Replies {
    private static final Map<String, String> REPLIES = new HashMap<>();

    static {
        REPLIES.put("start", """
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
                        /findbyid - Поиск фильма по ID TMDB
                        /setage - Установить возрастное ограничение
                        /help - Справка""");
        REPLIES.put("help", """
                        Доступны следующие команды:
                        /genre - Поиск по жанру
                        /year - Поиск по году
                        /moviesearch - Поиск фильма
                        /actorsearch - Поиск актёра
                        /similar - Похожие фильмы
                        /recommended - Рекомендуемые фильмы
                        /popular - Популярные фильмы
                        /toprated - Высоко-оценённые фильмы
                        /findbyid - Поиск фильма по ID TMDB
                        /setage - Установить возрастное ограничение""");
        REPLIES.put("year", "Введите год, и я найду фильмы, выпущенные в этом году");
        REPLIES.put("movie search", "Введите фильм который вы хотите найти");
        REPLIES.put("actor search", "Введите актёра которого вы хотите найти");
        REPLIES.put("similar", "Введите ID фильма к которому вы хотите найти похожие фильмы");
        REPLIES.put("recommended", "Введите ID фильма к которому вы хотите найти рекомендации");
        REPLIES.put("find by id", "Введите ID фильма из сервиса TMDB");
        REPLIES.put("subscribe", "Введите 1, чтобы подписаться на рассылку, или 0, чтобы отписаться");
        REPLIES.put("set age", "Введите ваш полный возраст");
        REPLIES.put("genre", "Введите жанр, и я найду фильмы по нему");
        REPLIES.put("invalid", "Введите правильные данные!");
        REPLIES.put("unexpected", "Не удалось обработать запрос");
        REPLIES.put("no data", "Ничего не найдено");
        REPLIES.put("unknown", "Команда не распознана. Введите /help для получения списка команд");
    }

    public static String reply(String command) {
        return REPLIES.getOrDefault(command, "Вы не должны это видеть \uD83D\uDC40");
    }
}