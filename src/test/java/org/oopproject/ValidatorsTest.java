package org.oopproject;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.oopproject.utils.Validators.isCommand;

class ValidatorsTest {
    @Test
    void testValidCommands() {
        assertTrue(isCommand("/start"));
        assertTrue(isCommand("Start"));
        assertTrue(isCommand("/genre"));
        assertTrue(isCommand("Genre"));
        assertTrue(isCommand("/year"));
        assertTrue(isCommand("Year"));
        assertTrue(isCommand("/help"));
        assertTrue(isCommand("Help"));
        assertTrue(isCommand("/setage"));
        assertTrue(isCommand("Set Age"));
    }

    @Test
    void testInvalidCommands() {
        assertFalse(isCommand("/unknown"));
        assertFalse(isCommand("RandomCommand"));
        assertFalse(isCommand(" "));
        assertFalse(isCommand("setage"));
    }
}
