package org.oopproject;

import org.oopproject.helpers.GenreHelper;
import org.oopproject.responses.FilmResponse;
import org.telegram.telegrambots.client.okhttp.OkHttpTelegramClient;
import org.telegram.telegrambots.longpolling.util.LongPollingSingleThreadUpdateConsumer;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.generics.TelegramClient;

import feign.Feign;
import feign.gson.GsonDecoder;
import org.oopproject.responses.ListResponse;
import io.github.cdimascio.dotenv.Dotenv;

import java.util.HashMap;
import java.util.List;

public class MyBot implements LongPollingSingleThreadUpdateConsumer {
    private final TelegramClient telegramClient;
    // Хранение текущего индекса для каждого года
    private final HashMap<Integer, Integer> yearMovieIndexMap = new HashMap<>();
    private final HashMap<Integer, Integer> genreMovieIndexMap = new HashMap<>();


    private final SiteRequests tmdbService;
    private final String TMDB_TOKEN;
    private boolean waitingForYear = false;  // Флаг ожидания ввода года
    private boolean waitingForGenre = false;  // Флаг ожидания ввода года


    public MyBot(String botToken) {
        telegramClient = new OkHttpTelegramClient(botToken);

        // Подключаем TMDB сервис через Feign
        Dotenv dotenv = Dotenv.configure().directory("assets").filename("token.env").load();
        TMDB_TOKEN = dotenv.get("TMDB_ACCESS_TOKEN");
        final String API_URL = "https://api.themoviedb.org/3";

        tmdbService = Feign
                .builder()
                .decoder(new GsonDecoder())
                .target(SiteRequests.class, API_URL);
    }

    @Override
    public void consume(Update update) {
        if (update.hasMessage() && update.getMessage().hasText()) {
            String message_text = update.getMessage().getText();
            long chat_id = update.getMessage().getChatId();
            String responseMessage;

            if (waitingForYear) {
                // Пользователь ввел год
                try {
                    int year = Integer.parseInt(message_text); // Преобразуем ввод в год
                    ListResponse moviesByYear = tmdbService.findMovie(
                            TMDB_TOKEN, false, "ru", 1,
                            "1900-01-01", "2100-01-01", "popularity.desc", 0, 10, "", 0, year
                    );

                    if (moviesByYear != null && moviesByYear.results != null && !moviesByYear.results.isEmpty()) {
                        // Получаем название первого фильма в списке
                        List<FilmResponse> movies = moviesByYear.results;

                        // Получение текущего индекса для этого года (по умолчанию 0)
                        int currentIndex = yearMovieIndexMap.getOrDefault(year, 0);

                        // Получаем фильм по текущему индексу
                        FilmResponse currentMovie = movies.get(currentIndex);

                        // Формируем сообщение с названием фильма
                        responseMessage = "Фильм, выпущенный в " + year + " году: " + currentMovie.title;

                        // Увеличиваем индекс для следующего фильма
                        currentIndex = (currentIndex + 1) % movies.size();  // Если индекс превышает размер списка, сбрасываем на 0

                        // Обновляем индекс для этого года в HashMap
                        yearMovieIndexMap.put(year, currentIndex);


                    } else {
                        responseMessage = "Извините, я не нашел фильмов за " + year + " год.";
                    }
                } catch (NumberFormatException e) {
                    responseMessage = "Пожалуйста, введите корректный год!";
                }
                waitingForYear = false; // Сбрасываем флаг ожидания года
            }
                else if (waitingForGenre) {
                    // Пользователь ввел название жанра
                    String genreName = message_text.toLowerCase();
                    String genreId = GenreHelper.getGenreId(genreName); // Получаем ID жанра

                    if (genreId != null) {
                        // Выполняем запрос к TMDB с указанным жанром
                        ListResponse moviesByGenre = tmdbService.findMovie(
                                TMDB_TOKEN, false, "ru", 1,
                                "1900-01-01", "2100-01-01", "popularity.desc", 0, 10,
                                genreId, 0, 0
                        );

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
                            genreMovieIndexMap.put(Integer.valueOf(genreId), currentIndex);
                        } else {
                            responseMessage = "Извините, я не нашел фильмов для жанра " + genreName + ".";
                        }
                    } else {
                        // Если жанр не найден
                        responseMessage = "Извините, я не знаю такого жанра. Попробуйте другой.";
                    }

                    // Сбрасываем флаг ожидания жанра
                    waitingForGenre = false;
                }
                else {
                // Обрабатываем команды
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
                        waitingForGenre=true;
                        break;
                    case "/help":
                        responseMessage = """
                                Доступны следующие команды:
                                                                
                                /genre - Поиск по жанру
                                /year - Поиск по году""";
                        break;
                    case "/year":
                        responseMessage = "Введите год, и я найду фильмы, выпущенные в этом году";
                        waitingForYear = true; // Устанавливаем флаг ожидания ввода года
                        break;
                    default:
                        responseMessage = "Извините, я не понимаю эту команду. Попробуйте /start для получения списка команд";
                        break;
                }
            }

            // Создаем сообщение
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
