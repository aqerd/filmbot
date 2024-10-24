package org.oopproject;

import org.oopproject.enums.Genres;
import org.oopproject.responses.FilmResponse;
import org.oopproject.responses.ListResponse;
import static org.oopproject.Config.tmdbService;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ExecutorService;
import org.telegram.telegrambots.meta.generics.TelegramClient;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.client.okhttp.OkHttpTelegramClient;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.longpolling.util.LongPollingSingleThreadUpdateConsumer;

public class TelegramBot implements LongPollingSingleThreadUpdateConsumer {
    private final TelegramClient telegramClient;
    private final ExecutorService executorService = Executors.newFixedThreadPool(10);

    private final HashMap<Integer, Integer> yearMovieIndexMap = new HashMap<>();
    private final HashMap<String, Integer> genreMovieIndexMap = new HashMap<>();
    private final Map<Long, Boolean> waitingForYearMap = new ConcurrentHashMap<>();
    private final Map<Long, Boolean> waitingForGenreMap = new ConcurrentHashMap<>();

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
            long chatId = update.getMessage().getChatId();
            String responseMessage;

            boolean waitingForYear = waitingForYearMap.getOrDefault(chatId, false);
            boolean waitingForGenre = waitingForGenreMap.getOrDefault(chatId, false);

            if (waitingForYear) {
                responseMessage = handleYear(messageText, chatId);
                waitingForYearMap.put(chatId, false); // сбрасываем состояние
            } else if (waitingForGenre) {
                responseMessage = handleGenre(messageText, chatId);
                waitingForGenreMap.put(chatId, false); // сбрасываем состояние
            } else {
                responseMessage = handleCommands(messageText, chatId);
            }

            SendMessage message = SendMessage.builder()
                    .chatId(chatId)
                    .text(responseMessage)
                    .build();

            try {
                telegramClient.execute(message);
            } catch (TelegramApiException err) {
                err.printStackTrace();
            }
        }
    }

    private String handleCommands(String messageText, long chatId) {
        String responseMessage;
        switch (messageText) {
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
                waitingForGenreMap.put(chatId, true);
                break;
            case "/help":
                responseMessage = """
                        Доступны следующие команды:

                        /genre - Поиск по жанру
                        /year - Поиск по году""";
                break;
            case "/year":
                responseMessage = "Введите год, и я найду фильмы, выпущенные в этом году";
                waitingForYearMap.put(chatId, true);
                break;
            default:
                responseMessage = "Извините, я не понимаю эту команду. Попробуйте /help для получения списка команд";
                break;
        }
        return responseMessage;
    }

    private String handleGenre(String messageText, long chatId) {
        // Пользователь ввел название жанра
        String responseMessage;

        String genreName = messageText.toLowerCase();
        try {
            String genreId = Genres.valueOf(genreName.toUpperCase()).genreId; // Получаем ID жанра

            // Выполняем запрос к TMDB с указанным жанром
//            ListResponse moviesByGenre = tmdbService.findMovie(
//                    TMDB_TOKEN, false, "ru", 1, "1900-01-01",
//                    "2100-01-01", "popularity.desc", 0,
//                    10, genreId, "US", 0, 0
//            );

            // Новый вызов запроса
            MovieParameters params = new MovieParameters()
                    .withLanguage("ru")
                    .withGenres(genreId);
            ListResponse moviesByGenre = tmdbService.findMovie(params);

            if (moviesByGenre != null && moviesByGenre.results != null && !moviesByGenre.results.isEmpty()) {
                // Получаем фильмы по жанру
                List<FilmResponse> movies = moviesByGenre.results;

                // Получаем текущий индекс для данного жанра
                int currentIndex = genreMovieIndexMap.getOrDefault(genreId, 0);

                // Получаем фильм по текущему индексу
                FilmResponse currentMovie = movies.get(currentIndex);

                // Формируем сообщение с названием фильма
                responseMessage = "Фильм жанра " + genreName + ": " + currentMovie.title;

                // Увеличиваем индекс для следующего фильма
                currentIndex = (currentIndex + 1) % movies.size(); // Цикличный просмотр фильмов

                // Обновляем индекс для жанра в HashMap
                genreMovieIndexMap.put(genreId, currentIndex);
            } else {
                responseMessage = "Извините, я не нашел фильмов для жанра " + genreName + ".";
            }
            waitingForGenreMap.put(chatId, false);
        } catch (IllegalArgumentException e) {
            // Если жанр не найден
            responseMessage = "Извините, я не знаю такого жанра. Попробуйте другой.";
        }

        return responseMessage;
    }

    private String handleYear(String messageText, long chatId) {
        String responseMessage;
        // Пользователь ввел год
        try {
            int userYear = Integer.parseInt(messageText); // Преобразуем ввод в год
//            ListResponse moviesByYear = tmdbService.findMovie(
//                    TMDB_TOKEN, false, "ru", 1,
//                    "1900-01-01", "2100-01-01", "popularity.desc", 0,
//                    10, "", "US", 0, userYear
//            );

            // Новый вызов запроса
            MovieParameters params = new MovieParameters()
                    .withLanguage("ru")
                    .withYear(userYear);
            ListResponse moviesByYear = tmdbService.findMovie(params);

            if (moviesByYear != null && moviesByYear.results != null && !moviesByYear.results.isEmpty()) {
                // Получаем название первого фильма в списке
                List<FilmResponse> movies = moviesByYear.results;

                // Получение текущего индекса для этого года (по умолчанию 0)
                int currentIndex = yearMovieIndexMap.getOrDefault(userYear, 0);

                // Получаем фильм по текущему индексу
                FilmResponse currentMovie = movies.get(currentIndex);

                // Формируем сообщение с названием фильма
                responseMessage = "Фильм, выпущенный в " + userYear + " году: " + currentMovie.title;

                // Увеличиваем индекс для следующего фильма, Если индекс превышает размер списка, сбрасываем на 0
                currentIndex = (currentIndex + 1) % movies.size();

                // Обновляем индекс для этого года в HashMap
                yearMovieIndexMap.put(userYear, currentIndex);


            } else {
                responseMessage = "Извините, я не нашел фильмов за " + userYear + " год.";
            }
            waitingForYearMap.put(chatId, false);
        } catch (NumberFormatException e) {
            responseMessage = "Пожалуйста, введите корректный год!";
        }

//        waitingForYear = false; // Сбрасываем флаг ожидания года (ЛЕГАСИ)

        return responseMessage;
    }
}
