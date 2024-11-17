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
                        /setage - Установить возрастное ограничение
                        Введи команду!""");
        replies.put("help", """
                        Доступны следующие команды:
                        /genre - Поиск по жанру
                        /year - Поиск по году
                        /setage - Установить возрастное ограничение""");
        replies.put("genre", "Введите жанр, и я найду фильмы по нему");
        replies.put("year", "Введите год, и я найду фильмы, выпущенные в этом году");
        replies.put("movie search", "вывод команды");
        replies.put("actor search", "вывод команды");
        replies.put("similar", "вывод команды");
        replies.put("recommended", "вывод команды");
        replies.put("find by id", "вывод команды");
        replies.put("set age", "Введите ваш полный возраст");
        replies.put("unknown", "Команда не распознана. Попробуйте /help для получения списка команд");
    }

    public static String getReply(String command) {
        return replies.getOrDefault(command, "Вы не должны это видеть \uD83D\uDC40");
    }
}