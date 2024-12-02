package oop.project.handlers;

import oop.project.shared.CommandWaiter;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;
import java.util.ArrayList;
import java.util.List;
import static oop.project.shared.CommandWaiter.GENRE;
import static oop.project.shared.CommandWaiter.NONE;

public class Keyboards {
    public static ReplyKeyboardMarkup setKeyboard(CommandWaiter waiter) {
        if (waiter == NONE) {
            return buildCommandKeyboard();
        } else if (waiter == GENRE) {
            return buildGenreKeyboard();
        }
        return null;
    }

    public static ReplyKeyboardMarkup createKeyboard(List<String> commands, int columns) {
        List<KeyboardRow> keyboard = new ArrayList<>();
        for (int i = 0; i < commands.size(); i += columns) {
            KeyboardRow row = new KeyboardRow();
            for (int j = 0; j < columns && (i + j) < commands.size(); j++) {
                row.add(commands.get(i + j));
            }
            keyboard.add(row);
        }

        ReplyKeyboardMarkup keyboardMarkup = ReplyKeyboardMarkup.builder().build();
        keyboardMarkup.setKeyboard(keyboard);
        keyboardMarkup.setResizeKeyboard(true);
        keyboardMarkup.setOneTimeKeyboard(true);

        return keyboardMarkup;
    }

    private static ReplyKeyboardMarkup buildCommandKeyboard() {
        List<String> commands = List.of("Genre", "Year", "Movie Search", "Actor Search",
                "Similar", "Recommended", "Popular", "Find by ID", "Top Rated", "Subscribe", "Set Age", "Help");
        int columns = 2;
        return createKeyboard(commands, columns);
    }

    private static ReplyKeyboardMarkup buildGenreKeyboard() {
        List<String> commands = List.of("Fantasy", "Horror", "Action", "Music", "War",
                "Drama", "Western", "Family", "Comedy", "History", "Crime", "Mystery", "Romance",
                "Thriller", "TV Movie", "Adventure", "Animation", "Documentary", "Science Fiction");
        int columns = 3;
        return createKeyboard(commands, columns);
    }
}