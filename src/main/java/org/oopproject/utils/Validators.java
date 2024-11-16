package org.oopproject.utils;

import com.google.gson.GsonBuilder;
import java.util.Set;

public class Validators {
    private static final Set<String> COMMANDS = Set.of(
            "/start", "Start",
            "/genre", "Genre",
            "/year", "Year",
            "/moviesearch", "Movie Search",
            "/actorsearch", "Actor Search",
            "/similar", "Similar",
            "/recommended", "Recommended",
            "/popular", "Popular",
            "/findbyid", "Find by ID",
            "/setage", "Set Age",
            "/help", "Help"
    );

    public static boolean isCommand(String text) {
        return COMMANDS.contains(text);
    }

    public static <Deserializer> void printPrettyJson(Deserializer des) {
        String prettyJson = new GsonBuilder()
                .setPrettyPrinting()
                .create()
                .toJson(des);
        System.out.println(prettyJson);
    }
}