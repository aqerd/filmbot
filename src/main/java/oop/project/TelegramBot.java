package oop.project;

import static oop.project.handlers.Commands.handleMessage;
import static oop.project.handlers.InlineMode.handleInlineQuery;
import static oop.project.handlers.Buttons.handleButtons;
import java.sql.SQLException;
import java.util.concurrent.Executors;
import java.util.concurrent.ExecutorService;
import org.telegram.telegrambots.meta.generics.TelegramClient;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.client.okhttp.OkHttpTelegramClient;
import org.telegram.telegrambots.longpolling.util.LongPollingSingleThreadUpdateConsumer;

public class TelegramBot implements LongPollingSingleThreadUpdateConsumer {
    private final ExecutorService EXECUTOR = Executors.newFixedThreadPool(10);
    private static TelegramClient telegramClient = null;

    public TelegramBot(String botToken) throws SQLException {
        telegramClient = new OkHttpTelegramClient(botToken);
    }

    public static TelegramClient getTelegramClient() {
        return telegramClient;
    }

    @Override
    public void consume(Update update) {
        EXECUTOR.submit(() -> handleUpdate(update));
    }

    public void handleUpdate(Update update) {
        if (update.hasMessage() && update.getMessage().hasText()) {
            handleMessage(update);
        } else if (update.hasCallbackQuery()) {
            handleButtons(update);
        } else if (update.hasInlineQuery()) {
            handleInlineQuery(update);
        }
    }
}