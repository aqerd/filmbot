package org.oopproject.helpers;

import java.util.HashMap;
import java.util.Map;

public class GenreHelper {
    private static final Map<String, String> genreMap = new HashMap<>();

    static {
        genreMap.put("action", "28");
        genreMap.put("drama", "18");
        genreMap.put("comedy", "35");
        genreMap.put("thriller", "53");
        // Добавьте другие жанры по необходимости
    }

    public static String getGenreId(String genreName) {
        return genreMap.get(genreName.toLowerCase());
    }
}
