package org.oopproject;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.oopproject.deserializers.ListDeserializer;
import org.oopproject.services.BroadcastingService;
import org.oopproject.utils.CommandWaiter;
import org.oopproject.utils.Genres;
import org.oopproject.parameters.MovieParameters;
import org.oopproject.parameters.ParametersBuilder;
import org.oopproject.deserializers.FilmDeserializer;

import static org.oopproject.utils.AgeRating.getRatingForAge;
import static org.oopproject.utils.CommandWaiter.*;
import static org.oopproject.utils.Config.tmdbService;
import static org.oopproject.utils.Validators.isCommand;
import static org.oopproject.utils.Replies.getReply;
import java.lang.reflect.Type;
import java.util.*;
import java.util.concurrent.*;

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

    private final ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();
    private final Database database = new Database();
    private final Gson gson = new Gson();

    public int nOfFilms = 10;

    private final HashMap<Integer, Integer> yearMovieIndexMap = new HashMap<>();
    private final HashMap<String, Integer> genreMovieIndexMap = new HashMap<>();
    private final Map<Long, CommandWaiter> commandWaiter = new ConcurrentHashMap<>();

    public TelegramBot(String botToken) {
        telegramClient = new OkHttpTelegramClient(botToken);
        BroadcastingService broadcastingService = new BroadcastingService(database, telegramClient);
        broadcastingService.startBroadcasting();    }

    @Override
    public void consume(Update update) {
        executorService.submit(() -> handleUpdate(update));
    }

    public void handleUpdate(Update update) {
        if (update.hasMessage() && update.getMessage().hasText()) {
            long chatId = update.getMessage().getChatId();
            database.insertChatId(chatId);
            Integer userAge=getUserAge(chatId);
            loadGenreIndexFromDatabase(chatId);
            loadYearIndexFromDatabase(chatId);

            String messageText = update.getMessage().getText();
            String responseMessage;
            CommandWaiter waiter = commandWaiter.getOrDefault(chatId, NONE);

            switch (waiter) {
                case YEAR:
                    responseMessage = handleYear(messageText, chatId);
                    commandWaiter.put(chatId, NONE);
                    break;
                case GENRE:
                    responseMessage = handleGenre(messageText, chatId);
                    commandWaiter.put(chatId, NONE);
                    break;
                case SETAGE:
                    responseMessage = handleAge(messageText, chatId);
                    commandWaiter.put(chatId, NONE);
                    break;
                case SUBSCRIBE:
                    responseMessage = handleSubscription(messageText, chatId);
                    commandWaiter.put(chatId, NONE);
                    break;
                default:
                    responseMessage = handleCommands(messageText, chatId);
                    break;
            }

            SendMessage message = SendMessage.builder()
                    .chatId(chatId)
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

    private void loadGenreIndexFromDatabase(long chatId) {
        String jsonGenreString = database.getGenreIndexesJson(chatId);
        if (jsonGenreString != null) {
            Type type = new TypeToken<HashMap<String, Integer>>(){}.getType();
            genreMovieIndexMap.putAll(gson.fromJson(jsonGenreString, type));
        }
    }

    private void loadYearIndexFromDatabase(long chatId) {
        String jsonYearString = database.getYearIndexesJson(chatId);
        if (jsonYearString != null) {
            Type type = new TypeToken<HashMap<Integer, Integer>>(){}.getType();
            yearMovieIndexMap.putAll(gson.fromJson(jsonYearString, type));
        }
    }

    protected String handleCommands(String messageText, long chatId) {
        String responseMessage;

        switch (messageText) {
            case "/start": case "Start":
                responseMessage = getReply("start");
                break;
            case "/genre": case "Genre":
                responseMessage = getReply("genre");
                commandWaiter.put(chatId, GENRE);
                break;
            case "/year": case "Year":
                responseMessage = getReply("year");
                commandWaiter.put(chatId, YEAR);
                break;
            case "/setage": case "Set Age":
                responseMessage = getReply("set age");
                commandWaiter.put(chatId, SETAGE);
                break;
            case "/subscribe": case "Subscribe":
                responseMessage = getReply("subscribe");
                commandWaiter.put(chatId, SUBSCRIBE);
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

        KeyboardRow row3 = new KeyboardRow();
        row3.add("Subscribe");

        keyboard.add(row1);
        keyboard.add(row2);
        keyboard.add(row3);

        keyboardMarkup.setKeyboard(keyboard);
        keyboardMarkup.setResizeKeyboard(true);
        keyboardMarkup.setOneTimeKeyboard(true);

        return keyboardMarkup;
    }

    private void updateGenreIndexInDatabase(long chatId) {
        String jsonGenreString = gson.toJson(genreMovieIndexMap);
        database.updateGenreIndexesJson(chatId, jsonGenreString);
    }

    protected String handleGenre(String messageText, long chatId) {
        if (isCommand(messageText)) {
            commandWaiter.put(chatId, NONE);
            return handleCommands(messageText, chatId);
        }

        String responseMessage;

        try {
            String genreId = Genres.valueOf(messageText.toUpperCase()).genreId;
            String userRating = getRatingForAge(getUserAge(chatId));

            MovieParameters params = new ParametersBuilder()
                    .withGenres(genreId)
                    .withCertificationLte(userRating)
                    .build();
            ListDeserializer moviesByGenre = tmdbService.findMovie(params);

            if (moviesByGenre != null && moviesByGenre.results != null && !moviesByGenre.results.isEmpty()) {
                List<FilmDeserializer> movies = moviesByGenre.results;
                int currentIndex = genreMovieIndexMap.getOrDefault(genreId, 0);
                StringBuilder movieListBuilder = new StringBuilder("Фильмы жанра " + messageText + ":" + "\n");

                for (int i = 0; i < nOfFilms; i++) {
                    FilmDeserializer currentMovie = movies.get((currentIndex + i) % movies.size());
                    movieListBuilder.append(i + 1).append(". ").append(currentMovie.title).append("\n");
                }

                currentIndex = (currentIndex + nOfFilms) % movies.size();
                genreMovieIndexMap.put(genreId, currentIndex);

                updateGenreIndexInDatabase(chatId);

                responseMessage = movieListBuilder.toString();

            } else {
                responseMessage = "Извините, я не нашел фильмов для жанра " + messageText;
            }
            commandWaiter.put(chatId, NONE);
        } catch (IllegalArgumentException e) {
            responseMessage = "Извините, я не знаю такого жанра. Попробуйте другой";
        }

        return responseMessage;
    }

    private void updateYearIndexInDatabase(long chatId) {
        String jsonYearString = gson.toJson(yearMovieIndexMap);
        database.updateYearIndexesJson(chatId, jsonYearString);
    }

    protected String handleYear(String messageText, long chatId) {
        if (isCommand(messageText)) {
            commandWaiter.put(chatId, NONE);
            return handleCommands(messageText, chatId);
        }

        String responseMessage;

        try {
            int userYear = Integer.parseInt(messageText);
            int currentYear = java.time.Year.now().getValue();
            String userRating = getRatingForAge(getUserAge(chatId));
            if (userYear < 1900 || userYear > currentYear) {
                responseMessage = "Пожалуйста, введите год в диапазоне от 1900 до " + currentYear;
                return responseMessage;
            }

            MovieParameters params = new ParametersBuilder()
                    .withYear(userYear)
                    .withCertificationLte(userRating)
                    .build();
            ListDeserializer moviesByYear = tmdbService.findMovie(params);

            if (moviesByYear != null && moviesByYear.results != null && !moviesByYear.results.isEmpty()) {
                List<FilmDeserializer> movies = moviesByYear.results;
                int currentIndex = yearMovieIndexMap.getOrDefault(userYear, 0);
                StringBuilder movieListBuilder = new StringBuilder("Фильмы, выпущенные в " + userYear + " году:" + "\n");

                for (int i = 0; i  < nOfFilms; i++) {
                    FilmDeserializer currentMovie = movies.get((currentIndex + i) % movies.size());
                    movieListBuilder.append(i + 1).append(". ").append(currentMovie.title).append("\n");
                }

                currentIndex = (currentIndex + nOfFilms) % movies.size();
                yearMovieIndexMap.put(userYear, currentIndex);

                updateYearIndexInDatabase(chatId);

                responseMessage = movieListBuilder.toString();

            } else {
                responseMessage = "Извините, я не нашел фильмов за " + userYear + " год";
            }
            commandWaiter.put(chatId, NONE);
        } catch (NumberFormatException e) {
            responseMessage = "Пожалуйста, введите корректный год!";
        }

        return responseMessage;
    }

    protected void handleSubscribe(long chatId) {
        database.updateSubscribe(chatId, true);
    }

    protected void handleUnsubscribe(long chatId) {
        database.updateSubscribe(chatId, false);
    }

    protected String handleAge(String messageText, long chatId) {
        if (isCommand(messageText)) {
            commandWaiter.put(chatId, NONE);
            return handleCommands(messageText, chatId);
        }

        String responseMessage;

        try {
            int userAge = Integer.parseInt(messageText);

            if (userAge >= 0 && userAge <= 100) {
                responseMessage = "Спасибо! Учтем ваш ответ";
                commandWaiter.put(chatId, NONE);
                database.updateUserAge(chatId, userAge);
            } else {
                responseMessage = "Пожалуйста, введите корректное число (от 0 до 100)";
            }
        } catch (NumberFormatException e) {
            responseMessage = "Пожалуйста, введите число";
        }
//        handleGetAge(chatId);
        return responseMessage;
    }

    public Integer getUserAge(long chatId) {
        return database.getUserAge(chatId);
    }



    private void sendMessage(long chatId, String text) {
        SendMessage message=SendMessage.builder()
                .chatId(chatId)
                .text(text)
                .build();
        try {
            telegramClient.execute(message);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    public String handleSubscription(String messageText, long chatId) {
        if (isCommand(messageText)) {
            commandWaiter.put(chatId, NONE);
            return handleCommands(messageText, chatId);
        }

        String responseMessage;

        try {
            int userInput = Integer.parseInt(messageText);

            if (userInput == 1) {
                handleSubscribe(chatId);
                responseMessage = "Вы успешно подписались на рассылку!";
            } else if (userInput == 0) {
                handleUnsubscribe(chatId);
                responseMessage = "Вы успешно отписались от рассылки.";
            } else {
                responseMessage = "Пожалуйста, введите 1 для подписки или 0 для отписки.";
            }
        } catch (NumberFormatException e) {
            responseMessage = "Пожалуйста, введите число (1 для подписки, 0 для отписки).";
        }
        return responseMessage;
    }
}