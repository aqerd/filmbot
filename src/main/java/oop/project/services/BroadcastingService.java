package oop.project.services;

import oop.project.Database;
import oop.project.deserializers.FilmDeserializer;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.generics.TelegramClient;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class BroadcastingService {
    private static Database database = null;
    private static MovieService movieService = null;
    private static final ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();
    private static TelegramClient telegramClient = null;

    public BroadcastingService(Database database, TelegramClient telegramClient) {
        this.database = database;
        this.telegramClient = telegramClient;
        this.movieService = new MovieService();
    }

    public static void startBroadcasting() {
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
                                .append(movie.getTitle())
                                .append("\n")
                                .append("Дата выхода: ")
                                .append(movie.getRelease_date())
                                .append("\n")
                                .append("Описание: ")
                                .append(movie.getOverview() != null && !movie.getOverview().isEmpty() ? movie.getOverview() : "Нет описания")
                                .append("\n");
                        if (movie.getTrailerUrl() != null) {
                            messageText.append("Ссылка на трейлер: ").append(movie.getTrailerUrl()).append("\n");
                        }

                        messageText.append("\n");
                    }
                }
                sendMessage(chatId, messageText.toString());
            }
        }, 0, 1, TimeUnit.MINUTES);
    }

    private static void sendMessage(long chatId, String text) {
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
