package org.oopproject;

import java.util.Set;

public class BotUtils {
    private static final Set<String> COMMANDS = Set.of("/start", "/genre", "/year", "/help", "/setadult");

    public static boolean isCommand(String text) {
        return COMMANDS.contains(text);
    }
}