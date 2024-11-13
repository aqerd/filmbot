package org.oopproject;

import org.oopproject.deserializers.ListDeserializer;
import org.oopproject.utils.CommandWaiter;
import org.oopproject.utils.Genres;
import org.oopproject.parameters.MovieParameters;
import org.oopproject.parameters.ParametersBuilder;
import org.oopproject.deserializers.FilmDeserializer;
import static org.oopproject.utils.CommandWaiter.*;
import static org.oopproject.utils.Config.tmdbService;
import static org.oopproject.utils.Validators.isCommand;
import static org.oopproject.utils.Replies.getReply;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ExecutorService;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;
import org.telegram.telegrambots.meta.generics.TelegramClient;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.client.okhttp.OkHttpTelegramClient;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.longpolling.util.LongPollingSingleThreadUpdateConsumer;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;

public class TelegramBot implements LongPollingSingleThreadUpdateConsumer {
    private final TelegramClient telegramClient;
    private final ExecutorService executorService = Executors.newFixedThreadPool(4);

    public int nOfFilms = 10;

    private final HashMap<Integer, Integer> yearMovieIndexMap = new HashMap<>();
    private final HashMap<String, Integer> genreMovieIndexMap = new HashMap<>();
    private final Map<Long, CommandWaiter> commandWaiter = new ConcurrentHashMap<>();

    public TelegramBot(String botToken) {
        telegramClient = new OkHttpTelegramClient(botToken);
    }

    @Override
    public void consume(Update update) {
        executorService.submit(() -> handleUpdate(update));
    }

    public void handleUpdate(Update update) {
        if (update.hasMessage() && update.getMessage().hasText()) {
            String messageText = update.getMessage().getText();
            long userID = update.getMessage().getChatId();
            String responseMessage;
            CommandWaiter waiter = commandWaiter.getOrDefault(userID, NONE);

            switch (waiter) {
                case YEAR:
                    responseMessage = handleYear(messageText, userID);
                    commandWaiter.put(userID, NONE);
                    break;
                case GENRE:
                    responseMessage = handleGenre(messageText, userID);
                    commandWaiter.put(userID, NONE);
                    break;
                case SETAGE:
                    responseMessage = handleAge(messageText, userID);
                    commandWaiter.put(userID, NONE);
                    break;
                default:
                    responseMessage = handleCommands(messageText, userID);
                    break;
            }

            SendMessage message = SendMessage.builder()
                    .chatId(userID)
                    .text(responseMessage)
                    .replyMarkup(createCommandKeyboard())
                    .build();

            try {
                telegramClient.execute(message);
            } catch (TelegramApiException err) {
                err.printStackTrace();
            }
        }
    }

    protected String handleCommands(String messageText, long userID) {
        String responseMessage;

        switch (messageText) {
            case "/start": case "Start":
                responseMessage = getReply("start");
                break;
            case "/genre": case "Genre":
                responseMessage = getReply("genre");
                commandWaiter.put(userID, GENRE);
                break;
            case "/year": case "Year":
                responseMessage = getReply("year");
                commandWaiter.put(userID, YEAR);
                break;
            case "/setage": case "Set Age":
                responseMessage = getReply("set age");
                commandWaiter.put(userID, SETAGE);
                break;
            case "/help": case "Help":
                responseMessage = getReply("help");
                break;
            default:
                responseMessage = getReply("unknown");
                break;
        }
        return responseMessage;
    }

    private ReplyKeyboardMarkup createCommandKeyboard() {
        ReplyKeyboardMarkup keyboardMarkup = ReplyKeyboardMarkup.builder().build();
        List<KeyboardRow> keyboard = new ArrayList<>();

        KeyboardRow row1 = new KeyboardRow();
        row1.add("Genre");
        row1.add("Year");

        KeyboardRow row2 = new KeyboardRow();
        row2.add("Set Age");
        row2.add("Help");

        keyboard.add(row1);
        keyboard.add(row2);

        keyboardMarkup.setKeyboard(keyboard);
        keyboardMarkup.setResizeKeyboard(true);
        keyboardMarkup.setOneTimeKeyboard(true);

        return keyboardMarkup;
    }

    protected String handleGenre(String messageText, long userID) {
        if (isCommand(messageText)) {
            commandWaiter.put(userID, NONE);
            return handleCommands(messageText, userID);
        }

        String responseMessage;

        try {
            String genreId = Genres.valueOf(messageText.toUpperCase()).genreId;

            MovieParameters params = new ParametersBuilder()
                    .withGenres(genreId)
                    .withCertificationLte("PG-13")
                    .withCertificationCountry("US")
                    .build();
            ListDeserializer moviesByGenre = tmdbService.findMovie(params);

            if (moviesByGenre != null && moviesByGenre.results != null && !moviesByGenre.results.isEmpty()) {
                List<FilmDeserializer> movies = moviesByGenre.results;
                int currentIndex = genreMovieIndexMap.getOrDefault(genreId, 0);
                StringBuilder movieListBuilder = new StringBuilder("Фильмы жанра " + messageText + ":\n");

                for (int i = 0; i < nOfFilms; i++) {
                    FilmDeserializer currentMovie = movies.get((currentIndex + i) % movies.size());
                    movieListBuilder.append(i + 1).append(". ").append(currentMovie.title).append("\n");
                }

                currentIndex = (currentIndex + nOfFilms) % movies.size(); // Цикличный просмотр фильмов
                genreMovieIndexMap.put(genreId, currentIndex);
                responseMessage = movieListBuilder.toString();

            } else {
                responseMessage = "Извините, я не нашел фильмов для жанра " + messageText;
            }
            commandWaiter.put(userID, NONE);
        } catch (IllegalArgumentException e) {
            responseMessage = "Извините, я не знаю такого жанра. Попробуйте другой";
        }

        return responseMessage;
    }

    protected String handleYear(String messageText, long userID) {
        if (isCommand(messageText)) {
            commandWaiter.put(userID, NONE);
            return handleCommands(messageText, userID);
        }

        String responseMessage;

        try {
            int userYear = Integer.parseInt(messageText);
            int currentYear = java.time.Year.now().getValue();

            if (userYear < 1900 || userYear > currentYear) {
                responseMessage = "Пожалуйста, введите год в диапазоне от 1900 до " + currentYear;
                return responseMessage;
            }

            MovieParameters params = new ParametersBuilder()
                    .withLanguage("en")
                    .withYear(userYear)
                    .withCertificationLte("PG-13")
                    .withCertificationCountry("US")
                    .build();
            ListDeserializer moviesByYear = tmdbService.findMovie(params);

            if (moviesByYear != null && moviesByYear.results != null && !moviesByYear.results.isEmpty()) {
                List<FilmDeserializer> movies = moviesByYear.results;
                int currentIndex = yearMovieIndexMap.getOrDefault(userYear, 0);
                StringBuilder movieListBuilder = new StringBuilder("Фильмы, выпущенные в " + userYear + " году:\n");

                for (int i = 0; i  < nOfFilms; i++) {
                    FilmDeserializer currentMovie = movies.get((currentIndex + i) % movies.size());
                    movieListBuilder.append(i + 1).append(". ").append(currentMovie.title).append("\n");
                }

                currentIndex = (currentIndex + nOfFilms) % movies.size();
                yearMovieIndexMap.put(userYear, currentIndex);
                responseMessage = movieListBuilder.toString();

            } else {
                responseMessage = "Извините, я не нашел фильмов за " + userYear + " год";
            }
            commandWaiter.put(userID, NONE);
        } catch (NumberFormatException e) {
            responseMessage = "Пожалуйста, введите корректный год!";
        }

        return responseMessage;
    }

    protected String handleAge(String messageText, long userID) {
        if (isCommand(messageText)) {
            commandWaiter.put(userID, NONE);
            return handleCommands(messageText, userID);
        }

        String responseMessage;

        try {
            int userAge = Integer.parseInt(messageText);

            if (userAge >= 0 && userAge <= 100) {
                responseMessage = "Спасибо! Учтем ваш ответ";
                commandWaiter.put(userID, NONE);
            } else {
                responseMessage = "Пожалуйста, введите корректное число (от 0 до 100)";
            }
        } catch (NumberFormatException e) {
            responseMessage = "Пожалуйста, введите число";
        }

        return responseMessage;
    }
}
