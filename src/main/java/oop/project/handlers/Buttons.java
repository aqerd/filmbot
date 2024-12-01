package oop.project.handlers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.generics.TelegramClient;
import static java.lang.Integer.parseInt;
import static oop.project.TelegramBot.getTelegramClient;
import static oop.project.shared.Responses.responseWithMovie;
import static oop.project.shared.Responses.responseWithPerson;

public class Buttons {
    private static final Logger LOG = LoggerFactory.getLogger(Buttons.class);
    private static final TelegramClient TG_CLIENT = getTelegramClient();

    public static void handleButtons(Update update) {
        String callbackData = update.getCallbackQuery().getData();
        long chatId = update.getCallbackQuery().getMessage().getChatId();
        LOG.info("BUTTONS, Callback Data: {}, ID: {}", callbackData, chatId);

        if (callbackData.startsWith("movie_")) {
            handleMovieButtons(callbackData, chatId);
        } else if (callbackData.startsWith("actor_")) {
            handleActorButtons(callbackData, chatId);
        }
    }

    public static void handleMovieButtons(String callbackData, long chatId) {
        int id = parseInt(callbackData.substring(6));
        String response = responseWithMovie(id);

        SendMessage message = SendMessage
                .builder()
                .chatId(String.valueOf(chatId))
                .text(response)
                .build();
        message.enableMarkdown(true);

        try {
            TG_CLIENT.execute(message);
        } catch (TelegramApiException e) {
            LOG.error("Unexpected Telegram API error, Callback Data: {}, ID: {}", callbackData, chatId, e);
        }
    }

    public static void handleActorButtons(String callbackData, long chatId) {
        int id = parseInt(callbackData.substring(6));
        String response = responseWithPerson(id);

        SendMessage message = SendMessage
                .builder()
                .chatId(String.valueOf(chatId))
                .text(response)
                .build();
        message.enableMarkdown(true);

        try {
            TG_CLIENT.execute(message);
        } catch (TelegramApiException e) {
            LOG.error("Unexpected Telegram API error, Callback Data: {}, ID: {}", callbackData, chatId, e);
        }
    }
}
