package org.oopproject;

import java.util.Set;

public class BotUtils {
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
}