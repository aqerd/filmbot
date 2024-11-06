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
                        ANIMATION, COMEDY, CRIME, DOCUMENTARY, DRAMA, FAMILY, FANTASY, HISTORY, HORROR, MUSIC, MYSTERY, ROMANCE, SCIENCE_FICTION, TV_MOVIE, THRILLER, WAR, WESTERN""");
        replies.put("help", """
                        Доступны следующие команды:

                        /genre - Поиск по жанру
                        /year - Поиск по году
                        /setage - Установить возрастное ограничение""");
        replies.put("year", "Введите год, и я найду фильмы, выпущенные в этом году");
        replies.put("set age", "Введите, сколько вам полных лет");
        replies.put("not recognized", "Команда не распознана. Попробуйте /help для получения списка команд");
    }

    public static String getReply(String command) {
        return replies.getOrDefault(command, "Вы не должны это видеть \uD83D\uDC40");
    }
}
