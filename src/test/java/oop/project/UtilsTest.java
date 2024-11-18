package oop.project;

import org.junit.jupiter.api.Test;
import oop.project.shared.Utils;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertFalse;

class UtilsTest extends Utils {
    @Test
    void testValidCommands() {
        for (String command : Utils.COMMANDS) {
            assertTrue(Utils.isCommand(command), "Command should be valid: " + command);
        }
    }

    @Test
    void testInvalidCommands() {
        String[] invalidCommands = {"/unknown", "RandomCommand", " ", "setage", "/unset", "/find"};

        for (String command : invalidCommands) {
            assertFalse(Utils.isCommand(command), "Command should be invalid: " + command);
        }
    }

    @Test
    void testCaseInsensitiveMatching() {
        assertTrue(Utils.isCommand("/start".toUpperCase()), "Command should be valid: /START (case insensitive)");
        assertTrue(Utils.isCommand("start".toLowerCase()), "Command should be valid: start (case insensitive)");
        assertTrue(Utils.isCommand("Set Age".toUpperCase()), "Command should be valid: Set Age (case insensitive)");
    }
}
