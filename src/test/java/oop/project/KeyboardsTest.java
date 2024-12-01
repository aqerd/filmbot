package oop.project;

import oop.project.handlers.Keyboards;
import org.junit.jupiter.api.Test;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import static oop.project.shared.CommandWaiter.GENRE;
import static oop.project.shared.CommandWaiter.NONE;
import static org.junit.jupiter.api.Assertions.*;

class KeyboardsTest {
    @Test
    void testSetKeyboardNone() {
        ReplyKeyboardMarkup keyboard = Keyboards.setKeyboard(NONE);
        assertNotNull(keyboard, "Keyboard should not be null");
        assertEquals(6, keyboard.getKeyboard().size(), "Keyboard should have 6 rows");
        assertEquals(2, keyboard.getKeyboard().get(0).size(), "Each row should have 2 buttons max");
    }

    @Test
    void testSetKeyboardGenre() {
        ReplyKeyboardMarkup keyboard = Keyboards.setKeyboard(GENRE);
        assertNotNull(keyboard, "Keyboard should not be null");
        assertEquals(7, keyboard.getKeyboard().size(), "Keyboard should have 7 rows");
        assertEquals(3, keyboard.getKeyboard().get(0).size(), "Each row should have 3 buttons max");
    }

    @Test
    void testSetKeyboardInvalidWaiter() {
        ReplyKeyboardMarkup keyboard = Keyboards.setKeyboard(null);
        assertNull(keyboard, "Keyboard should be null for unsupported CommandWaiter");
    }
}
