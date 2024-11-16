package org.oopproject;

//import com.google.gson.Gson;
//import com.google.gson.reflect.TypeToken;
import feign.FeignException;
import org.oopproject.deserializers.ListDeserializer;
import org.oopproject.utils.CommandWaiter;
import org.oopproject.utils.Genres;
import org.oopproject.parameters.MovieParameters;
import org.oopproject.parameters.ParametersBuilder;
import org.oopproject.deserializers.FilmDeserializer;
import static org.oopproject.utils.CommandWaiter.*;
import static org.oopproject.utils.Config.*;
import static org.oopproject.utils.Validators.isCommand;
import static org.oopproject.utils.Replies.getReply;
import static org.oopproject.utils.Validators.printPrettyJson;
//import java.lang.reflect.Type;
import java.sql.SQLException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ExecutorService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
    private final ExecutorService executorService = Executors.newFixedThreadPool(4);

//    private final Database database = new Database();
//    private final Gson gson = new Gson();

    public int nOfFilms = 10;

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
//                case MOVIESEARCH:
//                    responseMessage = handleMovieSearch(messageText, chatId);
//                    commandWaiter.put(chatId, NONE);
//                    break;
//                case ACTORSEARCH:
//                    responseMessage = handleActorSearch(messageText, chatId);
//                    commandWaiter.put(chatId, NONE);
//                    break;
//                case SIMILAR:
//                    responseMessage = handleSimilar(messageText, chatId);
//                    commandWaiter.put(chatId, NONE);
//                    break;
//                case RECOMMENDED:
//                    responseMessage = handleRecommended(messageText, chatId);
//                    commandWaiter.put(chatId, NONE);
//                    break;
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
            case "/moviesearch": case "Movie Search":
                responseMessage = getReply("movie search");
                commandWaiter.put(chatId, MOVIESEARCH);
                break;
            case "/actorsearch": case "Actor Search":
                responseMessage = getReply("actor search");
                commandWaiter.put(chatId, ACTORSEARCH);
                break;
            case "/similar": case "Similar":
                responseMessage = getReply("similar");
                commandWaiter.put(chatId, SIMILAR);
                break;
            case "/recommended": case "Recommended":
                responseMessage = getReply("recommended");
                commandWaiter.put(chatId, RECOMMENDED);
                break;
            case "/popular": case "Popular":
                responseMessage = handlePopular(chatId);
                break;
            case "/findbyid": case "Find by ID":
                responseMessage = getReply("find by id");
                commandWaiter.put(chatId, FINDBYID);
                break;
            case "/setage": case "Set Age":
                responseMessage = getReply("set age");
                commandWaiter.put(chatId, SETAGE);
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
            String genreId = Genres.valueOf(messageText.toUpperCase()).genreId;

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

                for (int i = 0; i < nOfFilms; i++) {
                    FilmDeserializer currentMovie = movies.get((currentIndex + i) % movies.size());
                    movieListBuilder.append(i + 1).append(". ").append(currentMovie.title).append("\n");
                }

                currentIndex = (currentIndex + nOfFilms) % movies.size();
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
            int userYear = Integer.parseInt(messageText);
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

                for (int i = 0; i  < nOfFilms; i++) {
                    FilmDeserializer currentMovie = movies.get((currentIndex + i) % movies.size());
                    movieListBuilder.append(i + 1).append(". ").append(currentMovie.title).append("\n");
                }

                currentIndex = (currentIndex + nOfFilms) % movies.size();
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

    protected String handlePopular(long chatId) {
        String responseMessage;

        try {
            ListDeserializer<FilmDeserializer> popularFilms = tmdbService.getPopularMovies(TMDB_TOKEN);

            if (popularFilms != null && popularFilms.results != null && !popularFilms.results.isEmpty()) {
                List<FilmDeserializer> movies = popularFilms.results;
                StringBuilder moviesListBuilder = new StringBuilder("Популярные фильмы:\n");

                for (int i = 0; i  < nOfFilms; i++) {
                    FilmDeserializer currentMovie = movies.get((popularMovieIndex + i) % movies.size());
                    moviesListBuilder.append(popularMovieIndex + i + 1).append(". ").append(currentMovie.title).append("\n");
                }

                popularMovieIndex = (popularMovieIndex + nOfFilms) % movies.size();
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
            int filmID = Integer.parseInt(messageText);

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
                } else {
                    filmBuilder.append("Link: ").append("https://www.themoviedb.org/movie/").append(filmID).append("\n");
                }
                // если ссылка == нулл то не выводить

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
            int userAge = Integer.parseInt(messageText);

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

        row1.add("Genre");
        row1.add("Year");
        row2.add("Movie Search");
        row2.add("Actor Search");
        row3.add("Similar");
        row3.add("Recommended");
        row4.add("Popular");
        row4.add("Find by ID");
        row5.add("Set Age");
        row5.add("Help");

        keyboard.add(row1);
        keyboard.add(row2);
        keyboard.add(row3);
        keyboard.add(row4);
        keyboard.add(row5);

        keyboardMarkup.setKeyboard(keyboard);
        keyboardMarkup.setResizeKeyboard(true);
        keyboardMarkup.setOneTimeKeyboard(true);

        return keyboardMarkup;
    }
}






/*

    protected String handleTTT(String messageText, long chatId) {
        if (isCommand(messageText)) {
            commandWaiter.put(chatId, NONE);
            return handleCommands(messageText, chatId);
        }

        String responseMessage;

        try {
            // парсинг сообщения
//            int filmID = Integer.parseInt(messageText);

            // вызов реквеста
//
            // обработка
//            if (moviesByYear != null && moviesByYear.results != null && !moviesByYear.results.isEmpty()) {
//                List<FilmDeserializer> movies = moviesByYear.results;
//                int currentIndex = yearMovieIndexMap.getOrDefault(userYear, 0);
//                StringBuilder movieListBuilder = new StringBuilder("Фильмы, выпущенные в " + userYear + " году:\n");
//
//                for (int i = 0; i  < nOfFilms; i++) {
//                    FilmDeserializer currentMovie = movies.get((currentIndex + i) % movies.size());
//                    movieListBuilder.append(i + 1).append(". ").append(currentMovie.title).append("\n");
//                }
//
//                currentIndex = (currentIndex + nOfFilms) % movies.size();
//                yearMovieIndexMap.put(userYear, currentIndex);
//
////                updateYearIndexInDatabase(chatId);
//
//                responseMessage = movieListBuilder.toString();

            } else {
                responseMessage = "Извините, я не нашел фильмов за " + userYear + " год";
            }
            commandWaiter.put(chatId, NONE);
        } catch (NumberFormatException e) {
            responseMessage = "Пожалуйста, введите корректный год!";
        }

        return responseMessage;
    }

*/