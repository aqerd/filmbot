package org.oopproject.services;

import org.oopproject.Database;
import org.oopproject.deserializers.FilmDeserializer;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.generics.TelegramClient;

import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class BroadcastingService {
    private final Database database;
    private final MovieService movieService;
    private final ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();
    private final TelegramClient telegramClient;

    public BroadcastingService(Database database, TelegramClient telegramClient) {
        this.database = database;
        this.telegramClient = telegramClient;
        this.movieService = new MovieService();
    }

    public void startBroadcasting() {
        scheduler.scheduleAtFixedRate(() -> {
            List<Long> subscribedUsers = database.getSubscribedUsers();
            List<FilmDeserializer> upcomingMovies = movieService.getUpcomingMovies();

            for (Long chatId : subscribedUsers) {
                StringBuilder messageText = new StringBuilder("🎬 Фильмы, которые скоро выйдут:\n");

                if (upcomingMovies.isEmpty()) {
                    messageText.append("К сожалению, нет новых фильмов на данный момент.");
                } else {
                    for (int i = 0; i < upcomingMovies.size(); i++) {
                        FilmDeserializer movie = upcomingMovies.get(i);
                        messageText.append(i + 1)
                                .append(". ")
                                .append(movie.title)
                                .append("\n")
                                .append("Дата выхода: ")
                                .append(movie.release_date)
                                .append("\n")
                                .append("Описание: ")
                                .append(movie.overview != null && !movie.overview.isEmpty() ? movie.overview : "Нет описания")
                                .append("\n");
                        if (movie.trailerUrl != null) {
                            messageText.append("Ссылка на трейлер: ").append(movie.trailerUrl).append("\n");
                        }

                        messageText.append("\n");
                    }
                }
                sendMessage(chatId, messageText.toString());
            }
        }, 0, 3, TimeUnit.DAYS);
    }

    private void sendMessage(long chatId, String text) {
        SendMessage message = SendMessage.builder()
                .chatId(chatId)
                .text(text)
                .build();
        try {
            telegramClient.execute(message);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }
}
