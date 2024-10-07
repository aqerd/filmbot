package org.oopproject;

import org.telegram.telegrambots.client.okhttp.OkHttpTelegramClient;
import org.telegram.telegrambots.longpolling.util.LongPollingSingleThreadUpdateConsumer;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.generics.TelegramClient;

public class MyBot implements LongPollingSingleThreadUpdateConsumer {
    private final TelegramClient telegramClient;

    public MyBot(String botToken) {
        telegramClient = new OkHttpTelegramClient(botToken);
    }

    @Override
    public void consume(Update update) {
        if (update.hasMessage() && update.getMessage().hasText()) {
            // Получаем текст сообщения и ID чата
            String message_text = update.getMessage().getText();
            long chat_id = update.getMessage().getChatId();
            String responseMessage;

            // Обработка команд
            switch (message_text) {
                case "/start":
                    responseMessage = """
                        Привет! Я бот по поиску фильмов.
                        У меня есть следующие команды:
                        /genre - Поиск по жанру
                        /year - Поиск по году
                        /help - Справка
                        Попробуй ввести команду!""";
                    break;
                case "/genre":
                    responseMessage = "Введите жанр, и я найду фильмы по нему";
                    break;
                case "/help":
                    responseMessage = """
                            Доступны следующие команды:
                            
                            /genre - Поиск по жанру
                            /year - Поиск по году""";
                    break;
                case "/year":
                    responseMessage = "Введите год, и я найду фильмы, выпущенные в этом году";
                    break;
                default:
                    responseMessage = "Извините, я не понимаю эту команду. Попробуйте /start для получения списка команд";
                    break;
            }

            // Создание сообщения
            SendMessage message = SendMessage.builder()
                .chatId(chat_id)
                .text(responseMessage)
                .build();

            try {
                telegramClient.execute(message);
            } catch (TelegramApiException err) {
                err.printStackTrace();
            }
        }
    }
}
