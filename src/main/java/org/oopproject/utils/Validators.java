package org.oopproject.utils;

import com.google.gson.GsonBuilder;
import java.util.Set;

public class Validators {
    protected static final Set<String> COMMANDS = Set.of(
            "/start", "start",
            "/genre", "genre",
            "/year", "year",
            "/moviesearch", "movie search",
            "/actorsearch", "actor search",
            "/similar", "similar",
            "/recommended", "recommended",
            "/popular", "popular",
            "/findbyid", "find by id",
            "/setage", "set age",
            "/help", "help"
    );

    public static boolean isCommand(String text) {
        return COMMANDS.contains(text.toLowerCase());
    }

    public static <Deserializer> void printPrettyJson(Deserializer des) {
        String prettyJson = new GsonBuilder()
                .setPrettyPrinting()
                .create()
                .toJson(des);
        System.out.println(prettyJson);
    }
}