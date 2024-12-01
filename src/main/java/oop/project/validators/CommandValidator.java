package oop.project.validators;

import java.util.Set;

public class CommandValidator implements Validator<String> {
    private static final Set<String> COMMANDS = Set.of(
            "/start", "start",
            "/genre", "genre",
            "/year", "year",
            "/moviesearch", "movie search",
            "/actorsearch", "actor search",
            "/similar", "similar",
            "/recommended", "recommended",
            "/popular", "popular",
            "/toprated", "top rated",
            "/findbyid", "find by id",
            "/setage", "set age",
            "/help", "help"
    );

    @Override
    public boolean isValid(String input) {
        return input != null && COMMANDS.contains(input.toLowerCase());
    }

    @Override
    public String getErrorMessage() {
        return "Invalid command. Please try again.";
    }
}