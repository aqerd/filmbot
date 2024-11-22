package oop.project;

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
            return commandKeyboard();
        } else if (waiter == GENRE) {
            return genreKeyboard();
        }
        return null;
    }

    private static ReplyKeyboardMarkup commandKeyboard() {
        ReplyKeyboardMarkup keyboardMarkup = ReplyKeyboardMarkup.builder().build();
        List<KeyboardRow> keyboard = new ArrayList<>();

        KeyboardRow row1 = new KeyboardRow();
        KeyboardRow row2 = new KeyboardRow();
        KeyboardRow row3 = new KeyboardRow();
        KeyboardRow row4 = new KeyboardRow();
        KeyboardRow row5 = new KeyboardRow();
        KeyboardRow row6 = new KeyboardRow();

        row1.add("Genre");
        row1.add("Year");
        row2.add("Movie Search");
        row2.add("Actor Search");
        row3.add("Similar");
        row3.add("Recommended");
        row4.add("Popular");
        row4.add("Find by ID");
        row5.add("Top Rated");
        row5.add("Set Age");
        row6.add("Help");

        keyboard.add(row1);
        keyboard.add(row2);
        keyboard.add(row3);
        keyboard.add(row4);
        keyboard.add(row5);
        keyboard.add(row6);

        keyboardMarkup.setKeyboard(keyboard);
        keyboardMarkup.setResizeKeyboard(true);
        keyboardMarkup.setOneTimeKeyboard(true);

        return keyboardMarkup;
    }

    private static ReplyKeyboardMarkup genreKeyboard() {
        ReplyKeyboardMarkup keyboardMarkup = ReplyKeyboardMarkup.builder().build();
        List<KeyboardRow> keyboard = new ArrayList<>();

        KeyboardRow row1 = new KeyboardRow();
        KeyboardRow row2 = new KeyboardRow();
        KeyboardRow row3 = new KeyboardRow();
        KeyboardRow row4 = new KeyboardRow();
        KeyboardRow row5 = new KeyboardRow();
        KeyboardRow row6 = new KeyboardRow();
        KeyboardRow row7 = new KeyboardRow();

        row1.add("Fantasy");
        row1.add("Horror");
        row1.add("Action");
        row2.add("Music");
        row2.add("War");
        row2.add("Drama");
        row3.add("Western");
        row3.add("Family");
        row3.add("Comedy");
        row4.add("History");
        row4.add("Crime");
        row4.add("Mystery");
        row5.add("Romance");
        row5.add("Thriller");
        row5.add("TV Movie");
        row6.add("Science Fiction");
        row6.add("Adventure");
        row7.add("Animation");
        row7.add("Documentary");

        keyboard.add(row1);
        keyboard.add(row2);
        keyboard.add(row3);
        keyboard.add(row4);
        keyboard.add(row5);
        keyboard.add(row6);
        keyboard.add(row7);

        keyboardMarkup.setKeyboard(keyboard);
        keyboardMarkup.setResizeKeyboard(true);
        keyboardMarkup.setOneTimeKeyboard(true);

        return keyboardMarkup;
    }
}
