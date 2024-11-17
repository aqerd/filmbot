package org.oopproject;

//import com.google.gson.Gson;
//import com.google.gson.reflect.TypeToken;
import feign.FeignException;
import org.oopproject.deserializers.CreditsDeserializer;
import org.oopproject.deserializers.ListDeserializer;
import org.oopproject.deserializers.PersonDeserializer;
import org.oopproject.utils.CommandWaiter;
import org.oopproject.utils.Genres;
import org.oopproject.parameters.MovieParameters;
import org.oopproject.parameters.ParametersBuilder;
import org.oopproject.deserializers.FilmDeserializer;

import static java.lang.Integer.parseInt;
import static org.oopproject.utils.CommandWaiter.*;
import static org.oopproject.utils.Config.*;
import static org.oopproject.utils.Validators.isCommand;
import static org.oopproject.utils.Replies.getReply;
//import java.lang.reflect.Type;
import java.sql.SQLException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ExecutorService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.telegram.telegrambots.meta.api.methods.AnswerCallbackQuery;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardRow;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;
import org.telegram.telegrambots.meta.generics.TelegramClient;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.client.okhttp.OkHttpTelegramClient;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.longpolling.util.LongPollingSingleThreadUpdateConsumer;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;

public class TelegramBot implements LongPollingSingleThreadUpdateConsumer {
    private static final Logger logger = LoggerFactory.getLogger(TelegramBot.class);

    private final TelegramClient telegramClient;
    private final ExecutorService executorService = Executors.newFixedThreadPool(10);

//    private final Database database = new Database();
//    private final Gson gson = new Gson();

    private final int constNumber = 12;
    private final int searchNumber = 6;

    private final HashMap<Integer, Integer> yearMovieIndexMap = new HashMap<>();
    private final HashMap<String, Integer> genreMovieIndexMap = new HashMap<>();
    private int popularMovieIndex = 0;
    private final Map<Long, CommandWaiter> commandWaiter = new ConcurrentHashMap<>();

    public TelegramBot(String botToken) throws SQLException {
        telegramClient = new OkHttpTelegramClient(botToken);
    }

    @Override
    public void consume(Update update) {
        executorService.submit(() -> handleUpdate(update));
    }

    public void handleUpdate(Update update) {
        handleButtons(update);

        if (update.hasMessage() && update.getMessage().hasText()) {
            long chatId = update.getMessage().getChatId();
//            database.insertChatId(chatId);
//            loadGenreIndexFromDatabase(chatId);
//            loadYearIndexFromDatabase(chatId);

            String messageText = update.getMessage().getText();
            String responseMessage;
            CommandWaiter waiter = commandWaiter.getOrDefault(chatId, NONE);

            logger.info("ID: {}, Message: {}, Waiter: {}", chatId, messageText, waiter);

            switch (waiter) {
                case GENRE:
                    responseMessage = handleGenre(messageText, chatId);
                    commandWaiter.put(chatId, NONE);
                    break;
                case YEAR:
                    responseMessage = handleYear(messageText, chatId);
                    commandWaiter.put(chatId, NONE);
                    break;
                case MOVIESEARCH:
                    responseMessage = handleMovieSearch(messageText, chatId);
                    commandWaiter.put(chatId, NONE);
                    break;
                case ACTORSEARCH:
                    responseMessage = handleActorSearch(messageText, chatId);
                    commandWaiter.put(chatId, NONE);
                    break;
                case SIMILAR:
                    responseMessage = handleSimilar(messageText, chatId);
                    commandWaiter.put(chatId, NONE);
                    break;
                case RECOMMENDED:
                    responseMessage = handleRecommended(messageText, chatId);
                    commandWaiter.put(chatId, NONE);
                    break;
                case FINDBYID:
                    responseMessage = handleFindById(messageText, chatId);
                    commandWaiter.put(chatId, NONE);
                    break;
                case SETAGE:
                    responseMessage = handleSetAge(messageText, chatId);
                    commandWaiter.put(chatId, NONE);
                    break;
                default:
                    responseMessage = handleCommands(messageText, chatId);
                    break;
            }

            CommandWaiter updatedWaiter = commandWaiter.getOrDefault(chatId, NONE);
            SendMessage message = SendMessage.builder()
                    .chatId(chatId)
                    .text(responseMessage)
                    .replyMarkup(getKeyboardByWaiter(updatedWaiter))
                    .build();

            try {
                telegramClient.execute(message);
            } catch (TelegramApiException err) {
                err.printStackTrace();
            }
        }
    }

    public void handleButtons(Update update) {
        if (update.hasCallbackQuery()) {
            String callbackData = update.getCallbackQuery().getData();
            long chatId = update.getCallbackQuery().getMessage().getChatId();

            if (callbackData.startsWith("movie_")) {
                int id = parseInt(callbackData.substring(6));
                FilmDeserializer film = tmdbService.getMovieById(TMDB_TOKEN, id);

                StringBuilder filmBuilder = new StringBuilder(film.title);
                if (!Objects.equals(film.original_language, "en")) {
                    filmBuilder.append(" / ").append(film.original_title);
                }
                filmBuilder.append(" (").append(film.release_date, 0, 4).append(", ")
                        .append(film.origin_country[0]).append(")").append("\n\n")
                        .append(film.overview).append("\n\n")
                        .append("Vote average: ").append(film.vote_average).append("/10\n")
                        .append("Runtime: ").append(film.runtime).append(" min \n");
                if (film.homepage != null) {
                    filmBuilder.append("Link: ").append(film.homepage).append("\n");
                }
                String responseMessage = filmBuilder.toString();

                SendMessage response = SendMessage
                        .builder()
                        .chatId(String.valueOf(chatId))
                        .text("Выберите фильм:")
                        .build();
                response.setChatId(String.valueOf(chatId));
                response.setText(responseMessage);

                try {
                    telegramClient.execute(response);
                } catch (TelegramApiException e) {
                    e.printStackTrace();
                }

                AnswerCallbackQuery answerCallbackQuery = AnswerCallbackQuery.builder()
                        .callbackQueryId(update.getCallbackQuery().getId())
                        .text("Вы выбрали фильм " + film.title)
                        .showAlert(false)
                        .build();
                answerCallbackQuery.setCallbackQueryId(update.getCallbackQuery().getId());

                try {
                    telegramClient.execute(answerCallbackQuery);
                } catch (TelegramApiException e) {
                    e.printStackTrace();
                }
            } else if (callbackData.startsWith("actor_")) {
                int id = parseInt(callbackData.substring(6));
                String responseMessage;
                List<FilmDeserializer> movies;
                CreditsDeserializer actorsFilms = tmdbService.getActorsFilms(TMDB_TOKEN, id);
                PersonDeserializer actor = tmdbService.getActor(TMDB_TOKEN, id);

                StringBuilder actorsData = new StringBuilder(actor.name).append(" (").append(actor.birthday, 0, 4);
                if (actor.deathday != null) {
                    actorsData.append(" - ").append(actor.deathday, 0, 4);
                }
                actorsData.append(")").append("\n").append("Place of birth: ").append(actor.place_of_birth)
                        .append("\n").append("Popularity: ").append(actor.popularity)
                        .append("\n").append("ID: ").append(actor.id).append("\n\n")
                        .append(actor.biography).append("\n\n");

                if ((actorsFilms.cast != null || actorsFilms.crew != null) && actor.known_for_department != null) {
                    StringBuilder actorsFilmsBuilder = new StringBuilder("Фильмы с участием " + actor.name + ":\n");
                    if (Objects.equals(actor.known_for_department, "Acting")) {
                        movies = actorsFilms.cast;
                    } else {
                        movies = actorsFilms.crew;
                    }

                    for (int i = 0; i < constNumber; i++) {
                        FilmDeserializer currentMovie = movies.get(i);
                        actorsFilmsBuilder.append(i + 1).append(". ").append(currentMovie.title).append("\n");
                    }

                    responseMessage = actorsData.append(actorsFilmsBuilder).toString();
                } else {
                    responseMessage = "Данные не найдены";
                }

                SendMessage response = SendMessage
                        .builder()
                        .chatId(String.valueOf(chatId))
                        .text("Выберите актёра:")
                        .build();
                response.setChatId(String.valueOf(chatId));
                response.setText(responseMessage);

                try {
                    telegramClient.execute(response);
                } catch (TelegramApiException e) {
                    e.printStackTrace();
                }

                AnswerCallbackQuery answerCallbackQuery = AnswerCallbackQuery.builder()
                        .callbackQueryId(update.getCallbackQuery().getId())
                        .text("Вы выбрали актёра " + actor.name)
                        .showAlert(false)
                        .build();
                answerCallbackQuery.setCallbackQueryId(update.getCallbackQuery().getId());

                try {
                    telegramClient.execute(answerCallbackQuery);
                } catch (TelegramApiException e) {
                    e.printStackTrace();
                }
            }
        }
    }

//    private void loadGenreIndexFromDatabase(long chatId) {
//        String jsonGenreString = database.getGenreIndexesJson(chatId);
//        if (jsonGenreString != null) {
//            Type type = new TypeToken<HashMap<String, Integer>>(){}.getType();
//            genreMovieIndexMap.putAll(gson.fromJson(jsonGenreString, type));
//        }
//    }
//
//    private void loadYearIndexFromDatabase(long chatId) {
//        String jsonYearString = database.getYearIndexesJson(chatId);
//        if (jsonYearString != null) {
//            Type type = new TypeToken<HashMap<Integer, Integer>>(){}.getType();
//            yearMovieIndexMap.putAll(gson.fromJson(jsonYearString, type));
//        }
//    }

    protected String handleCommands(String messageText, long chatId) {
        String responseMessage;

        switch (messageText.toLowerCase()) {
            case "/start": case "start":
                responseMessage = getReply("start");
                break;
            case "/genre": case "genre":
                responseMessage = getReply("genre");
                commandWaiter.put(chatId, GENRE);
                break;
            case "/year": case "year":
                responseMessage = getReply("year");
                commandWaiter.put(chatId, YEAR);
                break;
            case "/moviesearch": case "movie search":
                responseMessage = getReply("movie search");
                commandWaiter.put(chatId, MOVIESEARCH);
                break;
            case "/actorsearch": case "actor search":
                responseMessage = getReply("actor search");
                commandWaiter.put(chatId, ACTORSEARCH);
                break;
            case "/similar": case "similar":
                responseMessage = getReply("similar");
                commandWaiter.put(chatId, SIMILAR);
                break;
            case "/recommended": case "recommended":
                responseMessage = getReply("recommended");
                commandWaiter.put(chatId, RECOMMENDED);
                break;
            case "/popular": case "popular":
                responseMessage = handlePopular(chatId);
                break;
            case "/toprated": case "Top Rated":
                responseMessage = handleTopRated(chatId);
                break;
            case "/findbyid": case "Find by ID":
                responseMessage = getReply("find by id");
                commandWaiter.put(chatId, FINDBYID);
                break;
            case "/setage": case "set age":
                responseMessage = getReply("set age");
                commandWaiter.put(chatId, SETAGE);
                break;
            case "/help": case "help":
                responseMessage = getReply("help");
                break;
            default:
                responseMessage = getReply("unknown");
                break;
        }
        return responseMessage;
    }

//    private void updateGenreIndexInDatabase(long chatId) {
//        String jsonGenreString = gson.toJson(genreMovieIndexMap);
//        database.updateGenreIndexesJson(chatId, jsonGenreString);
//    }

    protected String handleGenre(String messageText, long chatId) {
        if (isCommand(messageText)) {
            commandWaiter.put(chatId, NONE);
            return handleCommands(messageText, chatId);
        }

        String responseMessage;

        try {
            String genreId = Genres.valueOf(messageText.toUpperCase().replace(" ", "_")).genreId;

            MovieParameters params = new ParametersBuilder()
                    .withGenres(genreId)
                    .withCertificationLte("PG-13")
                    .withCertificationCountry("US")
                    .build();
            ListDeserializer<FilmDeserializer> moviesByGenre = tmdbService.findMovie(params);

            if (moviesByGenre != null && moviesByGenre.results != null && !moviesByGenre.results.isEmpty()) {
                List<FilmDeserializer> movies = moviesByGenre.results;
                int currentIndex = genreMovieIndexMap.getOrDefault(genreId, 0);
                StringBuilder movieListBuilder = new StringBuilder("Фильмы жанра " + messageText + ":\n");

                for (int i = 0; i < constNumber; i++) {
                    FilmDeserializer currentMovie = movies.get((currentIndex + i) % movies.size());
                    movieListBuilder.append(i + 1).append(". ").append(currentMovie.title).append("\n");
                }

                currentIndex = (currentIndex + constNumber) % movies.size();
                genreMovieIndexMap.put(genreId, currentIndex);

//                updateGenreIndexInDatabase(chatId);

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

//    private void updateYearIndexInDatabase(long chatId) {
//        String jsonYearString = gson.toJson(yearMovieIndexMap);
//        database.updateYearIndexesJson(chatId, jsonYearString);
//    }

    protected String handleYear(String messageText, long chatId) {
        if (isCommand(messageText)) {
            commandWaiter.put(chatId, NONE);
            return handleCommands(messageText, chatId);
        }

        String responseMessage;

        try {
            int userYear = parseInt(messageText);
            int currentYear = java.time.Year.now().getValue();

            if (userYear < 1900 || userYear > currentYear) {
                responseMessage = "Пожалуйста, введите год в диапазоне от 1900 до " + currentYear;
                return responseMessage;
            }

            MovieParameters params = new ParametersBuilder()
                    .withYear(userYear)
                    .withCertificationLte("PG-13")
                    .withCertificationCountry("US")
                    .build();
            ListDeserializer<FilmDeserializer> moviesByYear = tmdbService.findMovie(params);

            if (moviesByYear != null && moviesByYear.results != null && !moviesByYear.results.isEmpty()) {
                List<FilmDeserializer> movies = moviesByYear.results;
                int currentIndex = yearMovieIndexMap.getOrDefault(userYear, 0);
                StringBuilder movieListBuilder = new StringBuilder("Фильмы, выпущенные в " + userYear + " году:\n");

                for (int i = 0; i  < constNumber; i++) {
                    FilmDeserializer currentMovie = movies.get((currentIndex + i) % movies.size());
                    movieListBuilder.append(i + 1).append(". ").append(currentMovie.title).append("\n");
                }

                currentIndex = (currentIndex + constNumber) % movies.size();
                yearMovieIndexMap.put(userYear, currentIndex);

//                updateYearIndexInDatabase(chatId);

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

    protected String handleMovieSearch(String messageText, long chatId) {
        if (isCommand(messageText)) {
            commandWaiter.put(chatId, NONE);
            return handleCommands(messageText, chatId);
        }

        String responseMessage = "Поиск по \"" + messageText + "\"\nВыберите фильм: ";
        List<InlineKeyboardRow> cols = new ArrayList<>();
        ListDeserializer<FilmDeserializer> films = tmdbService.searchMovie(TMDB_TOKEN, messageText, "en-US", 1);

        List<FilmDeserializer> movies = films.results;
        int filmsToProcess = Math.min(searchNumber, movies.size());

        if (filmsToProcess == 0) {
            return "Ничего не найдено";
        }

        for (int i = 0; i < filmsToProcess; i++) {
            String currentId = String.valueOf(movies.get(i).id);
            String currentTitle = String.valueOf(movies.get(i).title);
            InlineKeyboardButton button = InlineKeyboardButton.builder()
                    .text(currentTitle)
                    .callbackData("movie_" + currentId)
                    .build();
            InlineKeyboardRow row = new InlineKeyboardRow();
            row.add(button);
            cols.add(row);
        }

        InlineKeyboardMarkup markupInline = InlineKeyboardMarkup.builder()
                .keyboard(cols)
                .build();
        markupInline.setKeyboard(cols);

        SendMessage response = SendMessage.builder()
                .chatId(String.valueOf(chatId))
                .text(responseMessage)
                .replyMarkup(markupInline)
                .build();
        try {
            telegramClient.execute(response);
        } catch (Exception e) {
            logger.error("Error while sending message to chatId {}: {}", chatId, e.getMessage(), e);
        }
        return "";
    }

    protected String handleActorSearch(String messageText, long chatId) {
        if (isCommand(messageText)) {
            commandWaiter.put(chatId, NONE);
            return handleCommands(messageText, chatId);
        }

        String responseMessage = "Поиск по \"" + messageText + "\"\nВыберите актёра: ";
        List<InlineKeyboardRow> cols = new ArrayList<>();
        ListDeserializer<PersonDeserializer> humans = tmdbService.searchPerson(TMDB_TOKEN, messageText, "en-US", 1);

        List<PersonDeserializer> people = humans.results;
        int actorsToProcess = Math.min(searchNumber, people.size());

        if (actorsToProcess == 0) {
            return "Ничего не найдено";
        }

        for (int i = 0; i < actorsToProcess; i++) {
            String currentName = String.valueOf(people.get(i).name);
            String currentId = String.valueOf(people.get(i).id);
            InlineKeyboardButton button = InlineKeyboardButton.builder()
                    .text(currentName)
                    .callbackData("actor_" + currentId)
                    .build();
            InlineKeyboardRow row = new InlineKeyboardRow();
            row.add(button);
            cols.add(row);
        }

        InlineKeyboardMarkup markupInline = InlineKeyboardMarkup.builder()
                .keyboard(cols)
                .build();
        markupInline.setKeyboard(cols);

        SendMessage response = SendMessage.builder()
                .chatId(String.valueOf(chatId))
                .text(responseMessage)
                .replyMarkup(markupInline)
                .build();
        try {
            telegramClient.execute(response);
        } catch (TelegramApiException e) {
            logger.error("Error while sending message to chatId {}: {}", chatId, e.getMessage(), e);
        }
        return "";
    }

    protected String handleSimilar(String messageText, long chatId) {
        if (isCommand(messageText)) {
            commandWaiter.put(chatId, NONE);
            return handleCommands(messageText, chatId);
        }

        String responseMessage;

        try {
            int filmId = parseInt(messageText);

            ListDeserializer<FilmDeserializer> films = tmdbService.getSimilarMovies(TMDB_TOKEN, filmId);
            FilmDeserializer requestedFilm = tmdbService.getMovieById(TMDB_TOKEN, filmId);

            if (films != null && films.results != null && !films.results.isEmpty()) {
                List<FilmDeserializer> movies = films.results;
                StringBuilder movieListBuilder = new StringBuilder("Похожие фильмы для " + requestedFilm.title + "\n");
                for (int i = 0; i  < constNumber; i++) {
                    FilmDeserializer currentMovie = movies.get(i);
                    movieListBuilder.append(i + 1).append(". ").append(currentMovie.title).append("\n");
                }
                responseMessage = movieListBuilder.toString();
            } else {
                responseMessage = "Фильм с индексом " + filmId + " не найден";
            }
            commandWaiter.put(chatId, NONE);
        } catch (NumberFormatException e) {
            responseMessage = "Введите корректный ID!";
        }
        return responseMessage;
    }

    protected String handleRecommended(String messageText, long chatId) {
        if (isCommand(messageText)) {
            commandWaiter.put(chatId, NONE);
            return handleCommands(messageText, chatId);
        }

        String responseMessage;

        try {
            int filmId = parseInt(messageText);

            // вызов реквеста
            ListDeserializer<FilmDeserializer> films = tmdbService.getRecommendationsForMovie(TMDB_TOKEN, filmId);
            FilmDeserializer requestedFilm = tmdbService.getMovieById(TMDB_TOKEN, filmId);

            if (films != null && films.results != null && !films.results.isEmpty()) {
                List<FilmDeserializer> movies = films.results;
                StringBuilder movieListBuilder = new StringBuilder("Рекомендуемые фильмы для фильма " + requestedFilm.title + "\n");

                for (int i = 0; i  < constNumber; i++) {
                    FilmDeserializer currentMovie = movies.get(i);
                    movieListBuilder.append(i + 1).append(". ").append(currentMovie.title).append("\n");
                }
                responseMessage = movieListBuilder.toString();
        } else {
            responseMessage = "Фильм с индексом " + filmId + " не найден";
        }
        commandWaiter.put(chatId, NONE);
        } catch (NumberFormatException e) {
            responseMessage = "Введите корректный ID!";
        }
        return responseMessage;
    }

    protected String handlePopular(long chatId) {
        String responseMessage;

        try {
            ListDeserializer<FilmDeserializer> popularFilms = tmdbService.getPopularMovies(TMDB_TOKEN);

            if (popularFilms != null && popularFilms.results != null && !popularFilms.results.isEmpty()) {
                List<FilmDeserializer> movies = popularFilms.results;
                StringBuilder moviesListBuilder = new StringBuilder("Популярные фильмы:\n");

                for (int i = 0; i < constNumber; i++) {
                    FilmDeserializer currentMovie = movies.get((popularMovieIndex + i) % movies.size());
                    moviesListBuilder.append(popularMovieIndex + i + 1).append(". ").append(currentMovie.title).append("\n");
                }

                popularMovieIndex = (popularMovieIndex + constNumber) % movies.size();
                responseMessage = moviesListBuilder.toString();
            } else {
                responseMessage = "Данные не найдены";
            }
            commandWaiter.put(chatId, NONE);
        } catch (FeignException e) {
            if (e.status() == 404) {
                responseMessage = "404: Данные не найдены";
            } else {
                responseMessage = "Что-то пошло не так";
            }
        } catch (Exception e) {
            responseMessage = "Пожалуйста, введите корректные данные";
        }

        return responseMessage;
    }

    protected String handleTopRated(long chatId) {
        String responseMessage;
        try {
            ListDeserializer<FilmDeserializer> popularFilms = tmdbService.getTopRated(TMDB_TOKEN);
            if (popularFilms != null && popularFilms.results != null && !popularFilms.results.isEmpty()) {
                List<FilmDeserializer> movies = popularFilms.results;
                StringBuilder moviesListBuilder = new StringBuilder("Высоко-оцененные фильмы:\n");
                for (int i = 0; i < constNumber; i++) {
                    FilmDeserializer currentMovie = movies.get((popularMovieIndex + i) % movies.size());
                    moviesListBuilder.append(popularMovieIndex + i + 1).append(". ").append(currentMovie.title).append("\n");
                }
                popularMovieIndex = (popularMovieIndex + constNumber) % movies.size();
                responseMessage = moviesListBuilder.toString();
            } else {
                responseMessage = "Данные не найдены";
            }
            commandWaiter.put(chatId, NONE);
        } catch (FeignException e) {
            if (e.status() == 404) {
                responseMessage = "404: Данные не найдены";
            } else {
                responseMessage = "Что-то пошло не так";
            }
        } catch (Exception e) {
            responseMessage = "Пожалуйста, введите корректные данные";
        }
        return responseMessage;
    }

    protected String handleFindById(String messageText, long chatId) {
        if (isCommand(messageText)) {
            commandWaiter.put(chatId, NONE);
            return handleCommands(messageText, chatId);
        }

        String responseMessage;

        try {
            int filmID = parseInt(messageText);
            FilmDeserializer film = tmdbService.getMovieById(TMDB_TOKEN, filmID);

            if (film != null) {
                StringBuilder filmBuilder = new StringBuilder(film.title);
                if (!Objects.equals(film.original_language, "en")) {
                    filmBuilder.append(" / ").append(film.original_title);
                }
                filmBuilder.append(" (").append(film.release_date, 0, 4).append(", ")
                        .append(film.origin_country[0]).append(")").append("\n\n")
                        .append(film.overview).append("\n\n")
                        .append("Vote average: ").append(film.vote_average).append("/10\n")
                        .append("Runtime: ").append(film.runtime).append(" min \n");

                if (film.homepage != null) {
                    filmBuilder.append("Link: ").append(film.homepage).append("\n");
                }
                responseMessage = filmBuilder.toString();
        } else {
            responseMessage = "Извините, я не нашел фильм";
        }
        commandWaiter.put(chatId, NONE);
        } catch (NumberFormatException e) {
            responseMessage = "Пожалуйста, введите корректный ID";
        }
        return responseMessage;
    }

    protected String handleSetAge(String messageText, long chatId) {
        if (isCommand(messageText)) {
            commandWaiter.put(chatId, NONE);
            return handleCommands(messageText, chatId);
        }

        String responseMessage;

        try {
            int userAge = parseInt(messageText);

            if (userAge >= 0 && userAge <= 100) {
                responseMessage = "Спасибо! Учтем ваш ответ";
                commandWaiter.put(chatId, NONE);
            } else {
                responseMessage = "Пожалуйста, введите корректное число (от 0 до 100)";
            }
        } catch (NumberFormatException e) {
            responseMessage = "Пожалуйста, введите число";
        }

        return responseMessage;
    }

    private ReplyKeyboardMarkup createCommandKeyboard() {
        ReplyKeyboardMarkup keyboardMarkup = ReplyKeyboardMarkup.builder().build();
        List<KeyboardRow> keyboard = new ArrayList<>();

        KeyboardRow row1 = new KeyboardRow();
        KeyboardRow row2 = new KeyboardRow();
        KeyboardRow row3 = new KeyboardRow();
        KeyboardRow row4 = new KeyboardRow();
        KeyboardRow row5 = new KeyboardRow();
        KeyboardRow row6 = new KeyboardRow();

        row1.add("Genre");
        row1.add("Year");
        row2.add("Movie Search");
        row2.add("Actor Search");
        row3.add("Similar");
        row3.add("Recommended");
        row4.add("Popular");
        row4.add("Find by ID");
        row5.add("Top Rated");
        row5.add("Set Age");
        row6.add("Help");

        keyboard.add(row1);
        keyboard.add(row2);
        keyboard.add(row3);
        keyboard.add(row4);
        keyboard.add(row5);
        keyboard.add(row6);

        keyboardMarkup.setKeyboard(keyboard);
        keyboardMarkup.setResizeKeyboard(true);
        keyboardMarkup.setOneTimeKeyboard(true);

        return keyboardMarkup;
    }

    private ReplyKeyboardMarkup createGenreKeyboard() {
        ReplyKeyboardMarkup keyboardMarkup = ReplyKeyboardMarkup.builder().build();
        List<KeyboardRow> keyboard = new ArrayList<>();

        KeyboardRow row1 = new KeyboardRow();
        KeyboardRow row2 = new KeyboardRow();
        KeyboardRow row3 = new KeyboardRow();
        KeyboardRow row4 = new KeyboardRow();
        KeyboardRow row5 = new KeyboardRow();
        KeyboardRow row6 = new KeyboardRow();
        KeyboardRow row7 = new KeyboardRow();

        row1.add("Fantasy");
        row1.add("Horror");
        row1.add("Action");
        row2.add("Music");
        row2.add("War");
        row2.add("Drama");
        row3.add("Western");
        row3.add("Family");
        row3.add("Comedy");
        row4.add("History");
        row4.add("Crime");
        row4.add("Mystery");
        row5.add("Romance");
        row5.add("Thriller");
        row5.add("TV Movie");
        row6.add("Science Fiction");
        row6.add("Adventure");
        row7.add("Animation");
        row7.add("Documentary");

        keyboard.add(row1);
        keyboard.add(row2);
        keyboard.add(row3);
        keyboard.add(row4);
        keyboard.add(row5);
        keyboard.add(row6);
        keyboard.add(row7);

        keyboardMarkup.setKeyboard(keyboard);
        keyboardMarkup.setResizeKeyboard(true);
        keyboardMarkup.setOneTimeKeyboard(true);

        return keyboardMarkup;
    }

    private ReplyKeyboardMarkup getKeyboardByWaiter(CommandWaiter waiter) {
        if (waiter == NONE) {
            return createCommandKeyboard();
        } else if (waiter == GENRE) {
            return createGenreKeyboard();
        }
        return null;
    }
}