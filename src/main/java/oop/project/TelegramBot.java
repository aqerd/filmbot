package oop.project;

import oop.project.deserializers.*;
import oop.project.shared.CommandWaiter;
import oop.project.shared.Genres;
import oop.project.validators.*;
import static oop.project.handlers.Buttons.handleButtons;
import static oop.project.handlers.Keyboards.setKeyboard;
import static oop.project.shared.CommandWaiter.*;
import static oop.project.shared.Config.*;
import static oop.project.shared.Replies.reply;
import static oop.project.shared.Responses.*;
import java.sql.SQLException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ExecutorService;
// import java.lang.reflect.Type;
// import com.google.gson.Gson;
// import com.google.gson.reflect.TypeToken;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardRow;
import org.telegram.telegrambots.meta.generics.TelegramClient;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.client.okhttp.OkHttpTelegramClient;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.longpolling.util.LongPollingSingleThreadUpdateConsumer;

public class TelegramBot implements LongPollingSingleThreadUpdateConsumer {
    private static final Logger LOG = LoggerFactory.getLogger(TelegramBot.class);

    private final TelegramClient TG_CLIENT;
    private final ExecutorService EXECUTOR = Executors.newFixedThreadPool(10);

//    private final Database database = new Database();
//    private final Gson gson = new Gson();

    private final int CONST_NUM = 12;
    private final int SEARCH_NUM = 6;

    private final HashMap<Integer, Integer> yearMovieIndexMap = new HashMap<>();
    private final HashMap<String, Integer> genreMovieIndexMap = new HashMap<>();
    private int popularMovieIndex = 0;
    private final Map<Long, CommandWaiter> COMMAND_WAITER = new ConcurrentHashMap<>();

    public TelegramBot(String botToken) throws SQLException {
        TG_CLIENT = new OkHttpTelegramClient(botToken);
    }

    @Override
    public void consume(Update update) {
        EXECUTOR.submit(() -> handleUpdate(update));
    }

    public void handleUpdate(Update update) {
        handleButtons(update, TG_CLIENT);

        if (update.hasMessage() && update.getMessage().hasText()) {
            long chatId = update.getMessage().getChatId();
//            database.insertChatId(chatId);
//            loadGenreIndexFromDatabase(chatId);
//            loadYearIndexFromDatabase(chatId);

            String messageText = update.getMessage().getText();
            String responseMessage;
            CommandWaiter waiter = COMMAND_WAITER.getOrDefault(chatId, NONE);

            LOG.info("ID: {}, Message: {}, Waiter: {}", chatId, messageText, waiter);

            switch (waiter) {
                case GENRE:
                    responseMessage = handleGenre(messageText, chatId);
                    COMMAND_WAITER.put(chatId, NONE);
                    break;
                case YEAR:
                    responseMessage = handleYear(messageText, chatId);
                    COMMAND_WAITER.put(chatId, NONE);
                    break;
                case MOVIESEARCH:
                    responseMessage = handleMovieSearch(messageText, chatId);
                    COMMAND_WAITER.put(chatId, NONE);
                    break;
                case ACTORSEARCH:
                    responseMessage = handleActorSearch(messageText, chatId);
                    COMMAND_WAITER.put(chatId, NONE);
                    break;
                case SIMILAR:
                    responseMessage = handleSimilar(messageText, chatId);
                    COMMAND_WAITER.put(chatId, NONE);
                    break;
                case RECOMMENDED:
                    responseMessage = handleRecommended(messageText, chatId);
                    COMMAND_WAITER.put(chatId, NONE);
                    break;
                case FINDBYID:
                    responseMessage = handleFindById(messageText, chatId);
                    COMMAND_WAITER.put(chatId, NONE);
                    break;
                case SETAGE:
                    responseMessage = handleSetAge(messageText, chatId);
                    COMMAND_WAITER.put(chatId, NONE);
                    break;
                default:
                    responseMessage = handleCommands(messageText, chatId);
                    break;
            }

            CommandWaiter updatedWaiter = COMMAND_WAITER.getOrDefault(chatId, NONE);
            SendMessage message = SendMessage.builder()
                    .chatId(chatId)
                    .text(responseMessage)
                    .replyMarkup(setKeyboard(updatedWaiter))
                    .build();
            message.enableMarkdown(true);

            try {
                TG_CLIENT.execute(message);
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

    public String handleCommands(String messageText, long chatId) {
        String responseMessage;

        switch (messageText.toLowerCase()) {
            case "/start": case "start":
                responseMessage = reply("start");
                break;
            case "/genre": case "genre":
                responseMessage = reply("genre");
                COMMAND_WAITER.put(chatId, GENRE);
                break;
            case "/year": case "year":
                responseMessage = reply("year");
                COMMAND_WAITER.put(chatId, YEAR);
                break;
            case "/moviesearch": case "movie search":
                responseMessage = reply("movie search");
                COMMAND_WAITER.put(chatId, MOVIESEARCH);
                break;
            case "/actorsearch": case "actor search":
                responseMessage = reply("actor search");
                COMMAND_WAITER.put(chatId, ACTORSEARCH);
                break;
            case "/similar": case "similar":
                responseMessage = reply("similar");
                COMMAND_WAITER.put(chatId, SIMILAR);
                break;
            case "/recommended": case "recommended":
                responseMessage = reply("recommended");
                COMMAND_WAITER.put(chatId, RECOMMENDED);
                break;
            case "/popular": case "popular":
                responseMessage = handlePopular(chatId);
                break;
            case "/toprated": case "top rated":
                responseMessage = handleTopRated(chatId);
                break;
            case "/findbyid": case "find by id":
                responseMessage = reply("find by id");
                COMMAND_WAITER.put(chatId, FINDBYID);
                break;
            case "/setage": case "set age":
                responseMessage = reply("set age");
                COMMAND_WAITER.put(chatId, SETAGE);
                break;
            case "/help": case "help":
                responseMessage = reply("help");
                break;
            default:
                responseMessage = reply("unknown");
                break;
        }
        return responseMessage;
    }

//    private void updateGenreIndexInDatabase(long chatId) {
//        String jsonGenreString = gson.toJson(genreMovieIndexMap);
//        database.updateGenreIndexesJson(chatId, jsonGenreString);
//    }

    public String handleGenre(String messageText, long chatId) {
        Validator<String> validCommand = new CommandValidator();
        if (validCommand.isValid(messageText)) {
            COMMAND_WAITER.put(chatId, NONE);
            return handleCommands(messageText, chatId);
        }

        String genreId = Genres.valueOf(messageText.toUpperCase().replace(" ", "_")).getGenreId();
        int currentIndex = genreMovieIndexMap.getOrDefault(genreId, 0);
        String responseMessage;
        String moviesListBuilder = "Фильмы жанра " + messageText + ":\n";
        MovieParameters params = MovieParameters.builder().withGenres(genreId).build();
        ListDeserializer<FilmDeserializer> movies = tmdbService().findMovie(params).sortByPopularity();

        try {
            if (movies != null && movies.getResults() != null && !movies.getResults().isEmpty()) {
                responseMessage = responseWithListOfMovies(movies, moviesListBuilder, currentIndex, genreMovieIndexMap, genreId);
//                updateGenreIndexInDatabase(chatId);
            } else {
                responseMessage = "Извините, я не нашел фильмов для жанра " + messageText;
            }
            COMMAND_WAITER.put(chatId, NONE);
        } catch (IllegalArgumentException e) {
            responseMessage = "Извините, я не знаю такого жанра. Попробуйте другой";
        }

        return responseMessage;
    }

//    private void updateYearIndexInDatabase(long chatId) {
//        String jsonYearString = gson.toJson(yearMovieIndexMap);
//        database.updateYearIndexesJson(chatId, jsonYearString);
//    }

    public String handleYear(String messageText, long chatId) {
        Validator<String> validCommand = new CommandValidator();
        if (validCommand.isValid(messageText)) {
            COMMAND_WAITER.put(chatId, NONE);
            return handleCommands(messageText, chatId);
        }

        Validator<String> validYear = new YearValidator();
        if (!validYear.isValid(messageText)) {
            return validYear.getErrorMessage();
        }

        int userYear = Integer.parseInt(messageText);
        int currentIndex = yearMovieIndexMap.getOrDefault(userYear, 0);
        String responseMessage;
        String moviesListBuilder = "Фильмы, выпущенные в " + userYear + " году:\n";
        MovieParameters params = MovieParameters.builder().withYear(userYear).build();
        ListDeserializer<FilmDeserializer> movies = tmdbService().findMovie(params).sortByPopularity();

        try {
            if (movies != null && movies.getResults() != null && !movies.getResults().isEmpty()) {
                responseMessage = responseWithListOfMovies(movies, moviesListBuilder, currentIndex, yearMovieIndexMap, userYear);
//                updateYearIndexInDatabase(chatId);
            } else {
                responseMessage = "Извините, я не нашел фильмов за " + userYear + " год";
            }
            COMMAND_WAITER.put(chatId, NONE);
        } catch (Exception e) {
            responseMessage = "Что-то пошло не так";
        }

        return responseMessage;
    }

    protected String handleMovieSearch(String messageText, long chatId) {
        Validator<String> validCommand = new CommandValidator();
        if (validCommand.isValid(messageText)) {
            COMMAND_WAITER.put(chatId, NONE);
            return handleCommands(messageText, chatId);
        }

        String responseMessage = "Поиск по \"" + messageText + "\"\nВыберите фильм: ";
        List<InlineKeyboardRow> cols = new ArrayList<>();
        ListDeserializer<FilmDeserializer> films = tmdbService()
                .searchMovie(apiToken(), messageText, "en-US", 1).sortByPopularity();

        List<FilmDeserializer> movies = films.getResults();
        int filmsToProcess = Math.min(SEARCH_NUM, movies.size());

        if (filmsToProcess == 0) {
            return "Ничего не найдено";
        }

        for (int i = 0; i < filmsToProcess; i++) {
            String currentId = String.valueOf(movies.get(i).getId());
            String currentTitle = String.valueOf(movies.get(i).getTitle());
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
            TG_CLIENT.execute(response);
        } catch (Exception e) {
            LOG.error("Error while sending message to chatId {}: {}", chatId, e.getMessage(), e);
        }
        return "";
    }

    protected String handleActorSearch(String messageText, long chatId) {
        Validator<String> validCommand = new CommandValidator();
        if (validCommand.isValid(messageText)) {
            COMMAND_WAITER.put(chatId, NONE);
            return handleCommands(messageText, chatId);
        }

        String responseMessage = "Поиск по " + "\"" + messageText + "\"\n" + "Выберите актёра: ";
        List<InlineKeyboardRow> cols = new ArrayList<>();
        ListDeserializer<PersonDeserializer> humans = tmdbService().searchPerson(apiToken(), messageText, "en-US", 1).sortByPopularity();

        List<PersonDeserializer> people = humans.getResults();
        int actorsToProcess = Math.min(SEARCH_NUM, people.size());

        if (actorsToProcess == 0) {
            return "Ничего не найдено";
        }

        for (int i = 0; i < actorsToProcess; i++) {
            String currentName = String.valueOf(people.get(i).getName());
            String currentId = String.valueOf(people.get(i).getId());
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
            TG_CLIENT.execute(response);
        } catch (TelegramApiException e) {
            LOG.error("Error while sending message to chatId {}: {}", chatId, e.getMessage(), e);
        }
        return "";
    }

    protected String handleSimilar(String messageText, long chatId) {
        Validator<String> validCommand = new CommandValidator();
        if (validCommand.isValid(messageText)) {
            COMMAND_WAITER.put(chatId, NONE);
            return handleCommands(messageText, chatId);
        }

        Validator<String> validator = new IdValidator();
        if (!validator.isValid(messageText)) {
            return validator.getErrorMessage();
        }

        String responseMessage;
        int id = Integer.parseInt(messageText);
        ListDeserializer<FilmDeserializer> movies = tmdbService().getSimilar(apiToken(), id).sortByPopularity();
        FilmDeserializer film = tmdbService().getMovieById(apiToken(), id);
        String moviesListBuilder = "Похожие фильмы для " + film.getTitle() + ":\n";

        try {
            if (movies != null && movies.getResults() != null && !movies.getResults().isEmpty()) {
                responseMessage = responseWithListOfMovies(movies, moviesListBuilder);
            } else {
                responseMessage = "Фильм с индексом " + id + " не найден";
            }
            COMMAND_WAITER.put(chatId, NONE);
        } catch (Exception e) {
            responseMessage = "Что-то пошло не так";
        }

        return responseMessage;
    }

    protected String handleRecommended(String messageText, long chatId) {
        Validator<String> validCommand = new CommandValidator();
        if (validCommand.isValid(messageText)) {
            COMMAND_WAITER.put(chatId, NONE);
            return handleCommands(messageText, chatId);
        }

        Validator<String> validId = new IdValidator();
        if (!validId.isValid(messageText)) {
            return validId.getErrorMessage();
        }

        String responseMessage;
        int id = Integer.parseInt(messageText);
        ListDeserializer<FilmDeserializer> movies = tmdbService().getRecommended(apiToken(), id).sortByPopularity();
        FilmDeserializer film = tmdbService().getMovieById(apiToken(), id);
        String moviesListBuilder = "Рекомендуемые фильмы для " + film.getTitle() + ":\n";

        try {
            if (movies != null && movies.getResults() != null && !movies.getResults().isEmpty()) {
                responseMessage = responseWithListOfMovies(movies, moviesListBuilder);
            } else {
                responseMessage = "Фильм с индексом " + id + " не найден";
            }
            COMMAND_WAITER.put(chatId, NONE);
        } catch (Exception e) {
            responseMessage = "Введите корректные данные";
        }

        return responseMessage;
    }

    protected String handlePopular(long chatId) {
        String responseMessage;
        ListDeserializer<FilmDeserializer> movies = tmdbService().getPopular(apiToken()).sortByPopularity();
        String moviesListBuilder = "Популярные фильмы:\n";

        try {
            if (movies != null && movies.getResults() != null && !movies.getResults().isEmpty()) {
                responseMessage = responseWithListOfMovies(movies, moviesListBuilder);
            } else {
                responseMessage = "Данные не найдены";
            }
            COMMAND_WAITER.put(chatId, NONE);
        } catch (Exception e) {
            responseMessage = "Введите корректные данные";
        }

        return responseMessage;
    }

    protected String handleTopRated(long chatId) {
        String responseMessage;
        ListDeserializer<FilmDeserializer> movies = tmdbService().getTopRated(apiToken());
        String moviesListBuilder = "Высоко-оцененные фильмы:\n";

        try {
            if (movies != null && movies.getResults() != null && !movies.getResults().isEmpty()) {
                responseMessage = responseWithListOfMovies(movies, moviesListBuilder);
            } else {
                responseMessage = "Данные не найдены";
            }
            COMMAND_WAITER.put(chatId, NONE);
        } catch (Exception e) {
            responseMessage = "Введите корректные данные";
        }

        return responseMessage;
    }

    public String handleFindById(String messageText, long chatId) {
        Validator<String> validCommand = new CommandValidator();
        if (validCommand.isValid(messageText)) {
            COMMAND_WAITER.put(chatId, NONE);
            return handleCommands(messageText, chatId);
        }

        Validator<String> validId = new IdValidator();
        if (!validId.isValid(messageText)) {
            return validId.getErrorMessage();
        }

        String responseMessage;
        int id = Integer.parseInt(messageText);

        try {
            responseMessage = responseWithMovie(id);
            COMMAND_WAITER.put(chatId, NONE);
        } catch (Exception e) {
            responseMessage = "Фильм с таким ID не найден";
        }
        return responseMessage;
    }

    public String handleSetAge(String messageText, long chatId) {
        Validator<String> validCommand = new CommandValidator();
        if (validCommand.isValid(messageText)) {
            COMMAND_WAITER.put(chatId, NONE);
            return handleCommands(messageText, chatId);
        }

        Validator<String> validAge = new AgeValidator();
        if (!validAge.isValid(messageText)) {
            return validAge.getErrorMessage();
        }

        return "Спасибо! Учтем ваш ответ";
    }
}