package org.oopproject.utils;

import com.google.gson.GsonBuilder;
import java.util.Set;

public class Validators {
    private static final Set<String> COMMANDS = Set.of(
            "/start", "Start",
            "/genre", "Genre",
            "/year", "Year",
            "/help", "Help",
            "/setage", "Set Age"
    );

    public static boolean isCommand(String text) {
        return COMMANDS.contains(text);
    }

    public <Deserializer> void printPrettyJson(Deserializer des) {
        String prettyJson = new GsonBuilder()
                .setPrettyPrinting()
                .create()
                .toJson(des);
        System.out.println(prettyJson);
    }
}