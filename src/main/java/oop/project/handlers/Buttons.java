package oop.project.handlers;

import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.generics.TelegramClient;
import static java.lang.Integer.parseInt;
import static oop.project.shared.Responses.responseWithMovie;
import static oop.project.shared.Responses.responseWithPerson;

public class Buttons {
    public static void handleButtons(Update update, TelegramClient tgClient) {
        if (update.hasCallbackQuery()) {
            String callbackData = update.getCallbackQuery().getData();
            long chatId = update.getCallbackQuery().getMessage().getChatId();

            if (callbackData.startsWith("movie_")) {
                handleMovieButtons(callbackData, chatId, update, tgClient);
            } else if (callbackData.startsWith("actor_")) {
                handleActorButtons(callbackData, chatId, update, tgClient);
            }
        }
    }

    public static void handleMovieButtons(String callbackData, long chatId, Update update, TelegramClient tgClient) {
        int id = parseInt(callbackData.substring(6));
        String response = responseWithMovie(id);

        SendMessage message = SendMessage
                .builder()
                .chatId(String.valueOf(chatId))
                .text(response)
                .build();
        message.enableMarkdown(true);

        try {
            tgClient.execute(message);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    public static void handleActorButtons(String callbackData, long chatId, Update update, TelegramClient tgClient) {
        int id = parseInt(callbackData.substring(6));
        String response = responseWithPerson(id);

        SendMessage message = SendMessage
                .builder()
                .chatId(String.valueOf(chatId))
                .text(response)
                .build();
        message.enableMarkdown(true);

        try {
            tgClient.execute(message);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }
}
