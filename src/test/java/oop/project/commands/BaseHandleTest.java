package oop.project.commands;

import oop.project.TelegramBot;
import org.junit.jupiter.api.BeforeEach;
import java.sql.SQLException;

public abstract class BaseHandleTest {
    protected TelegramBot telegramBot;
    protected final long CHAT_ID = 1L;

    public BaseHandleTest() throws SQLException {
        telegramBot = new TelegramBot("dummy_token");
    }

    @BeforeEach
    void setUp() throws SQLException {
        telegramBot = new TelegramBot("dummy_token");
    }
}
