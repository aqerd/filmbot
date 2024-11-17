package org.oopproject;

import org.junit.jupiter.api.Test;
import org.oopproject.utils.Validators;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertFalse;

class ValidatorsTest extends Validators{
    @Test
    void testValidCommands() {
        for (String command : Validators.COMMANDS) {
            assertTrue(Validators.isCommand(command), "Command should be valid: " + command);
        }
    }

    @Test
    void testInvalidCommands() {
        String[] invalidCommands = {"/unknown", "RandomCommand", " ", "setage", "/unset", "/find"};

        for (String command : invalidCommands) {
            assertFalse(Validators.isCommand(command), "Command should be invalid: " + command);
        }
    }

    @Test
    void testCaseInsensitiveMatching() {
        assertTrue(Validators.isCommand("/start".toUpperCase()), "Command should be valid: /START (case insensitive)");
        assertTrue(Validators.isCommand("start".toLowerCase()), "Command should be valid: start (case insensitive)");
        assertTrue(Validators.isCommand("Set Age".toUpperCase()), "Command should be valid: Set Age (case insensitive)");
    }
}
