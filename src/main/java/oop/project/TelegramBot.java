package oop.project;

// import com.google.gson.Gson;
// import com.google.gson.reflect.TypeToken;
import feign.FeignException;
import oop.project.deserializers.*;
import oop.project.shared.CommandWaiter;
import oop.project.shared.Genres;
import oop.project.parameters.MovieParameters;
import oop.project.parameters.ParametersBuilder;
import static java.lang.Integer.parseInt;
import static oop.project.shared.CommandWaiter.*;
import static oop.project.shared.Config.*;
import static oop.project.shared.Utils.isCommand;
import static oop.project.shared.Replies.getReply;
// import java.lang.reflect.Type;
import java.sql.SQLException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ExecutorService;
import oop.project.validators.AgeValidator;
import oop.project.validators.IdValidator;
import oop.project.validators.Validator;
import oop.project.validators.YearValidator;
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
                FilmDeserializer film = getTmdbService().getMovieById(getTmdbToken(), id);
                ListDeserializer<VideoDeserializer> videos = getTmdbService().getVideosForFilm(getTmdbToken(), id);

                StringBuilder filmBuilder = new StringBuilder(film.getTitle());
                if (!Objects.equals(film.getOriginal_language(), "en")) {
                    filmBuilder.append(" / ").append(film.getOriginal_title());
                }
                filmBuilder.append(" (").append(film.getRelease_date(), 0, 4).append(", ")
                        .append(film.getOrigin_country()[0]).append(")").append("\n\n")
                        .append(film.getOverview()).append("\n\n")
                        .append("Vote average: ").append(film.getVote_average()).append("/10\n")
                        .append("Runtime: ").append(film.getRuntime()).append(" min \n");
                if (film.getHomepage() != null) {
                    filmBuilder.append("Link: ").append(film.getHomepage()).append("\n");
                }

                for (int i = 0; i < videos.getResults().size(); i++) {
                    if (videos.getResults().get(i).getId() != null &&
                            Objects.equals(videos.getResults().get(i).getName(), "Official Trailer") &&
                            Objects.equals(videos.getResults().get(i).getSite(), "YouTube") &&
                            Objects.equals(videos.getResults().get(i).getType(), "Trailer") &&
                            videos.getResults().get(i).isOfficial()) {
                        filmBuilder.append("Trailer: ").
                                append("https://youtube.com/watch?v=").append(videos.getResults().get(i).getKey()).append("\n");
                    }
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
                        .text("Выполнен поиск по " + film.getTitle())
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
                CreditsDeserializer actorsFilms = getTmdbService().getActorsFilms(getTmdbToken(), id);
                PersonDeserializer actor = getTmdbService().getActor(getTmdbToken(), id);

                StringBuilder actorsData = new StringBuilder(actor.getName()).append(" (").append(actor.getBirthday(), 0, 4);
                if (actor.getDeathday() != null) {
                    actorsData.append(" - ").append(actor.getDeathday(), 0, 4);
                }
                actorsData.append(")").append("\n").append("Place of birth: ").append(actor.getPlace_of_birth().trim())
                        .append("\n").append("Popularity: ").append(actor.getPopularity().trim())
                        .append("\n").append("ID: ").append(actor.getId()).append("\n\n");
                if (!Objects.equals(actor.getBiography(), "")) {
                    actorsData.append(actor.getBiography()).append("\n\n");
                }

                if ((actorsFilms.getCast() != null || actorsFilms.getCrew() != null) && actor.getKnown_for_department() != null) {
                    StringBuilder actorsFilmsBuilder = new StringBuilder("Фильмы с участием " + actor.getName() + ":\n");

                    if (Objects.equals(actor.getKnown_for_department(), "Acting")) {
                        movies = actorsFilms.getCast();
                    } else {
                        movies = actorsFilms.getCrew();
                    }

                    if (movies != null && !movies.isEmpty()) {
                        int filmsToDisplay = Math.min(constNumber, movies.size());
                        for (int i = 0; i < filmsToDisplay; i++) {
                            FilmDeserializer currentMovie = movies.get(i);
                            actorsFilmsBuilder.append(i + 1).append(". ").append(currentMovie.getTitle()).append("\n");
                        }
                    } else {
                        actorsFilmsBuilder.append("Фильмов не найдено.\n");
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
                        .text("Выполнен поиск по " + actor.getName())
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
            case "/toprated": case "top rated":
                responseMessage = handleTopRated(chatId);
                break;
            case "/findbyid": case "find by id":
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
            ListDeserializer<FilmDeserializer> moviesByGenre = getTmdbService().findMovie(params);

            if (moviesByGenre != null && moviesByGenre.getResults() != null && !moviesByGenre.getResults().isEmpty()) {
                List<FilmDeserializer> movies = moviesByGenre.getResults();
                int currentIndex = genreMovieIndexMap.getOrDefault(genreId, 0);
                StringBuilder movieListBuilder = new StringBuilder("Фильмы жанра " + messageText + ":\n");

                for (int i = 0; i < constNumber; i++) {
                    FilmDeserializer currentMovie = movies.get((currentIndex + i) % movies.size());
                    movieListBuilder.append(i + 1).append(". ").append(currentMovie.getTitle()).append("\n");
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
        Validator<String> validator = new YearValidator();

        if (!validator.isValid(messageText)) {
            return validator.getErrorMessage();
        }

        int userYear = Integer.parseInt(messageText);
        String responseMessage;

        try {
            MovieParameters params = new ParametersBuilder()
                    .withYear(userYear)
                    .withCertificationLte("PG-13")
                    .withCertificationCountry("US")
                    .build();
            ListDeserializer<FilmDeserializer> moviesByYear = getTmdbService().findMovie(params);

            if (moviesByYear != null && moviesByYear.getResults() != null && !moviesByYear.getResults().isEmpty()) {
                List<FilmDeserializer> movies = moviesByYear.getResults();
                int currentIndex = yearMovieIndexMap.getOrDefault(userYear, 0);
                StringBuilder movieListBuilder = new StringBuilder("Фильмы, выпущенные в " + userYear + " году:\n");

                for (int i = 0; i < constNumber; i++) {
                    FilmDeserializer currentMovie = movies.get((currentIndex + i) % movies.size());
                    movieListBuilder.append(i + 1).append(". ").append(currentMovie.getTitle()).append("\n");
                }

                currentIndex = (currentIndex + constNumber) % movies.size();
                yearMovieIndexMap.put(userYear, currentIndex);

                responseMessage = movieListBuilder.toString();
            } else {
                responseMessage = "Извините, я не нашел фильмов за " + userYear + " год";
            }
            commandWaiter.put(chatId, NONE);
        } catch (Exception e) {
            responseMessage = "Что-то пошло не так";
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
        ListDeserializer<FilmDeserializer> films = getTmdbService().searchMovie(getTmdbToken(), messageText, "en-US", 1);

        List<FilmDeserializer> movies = films.getResults();
        int filmsToProcess = Math.min(searchNumber, movies.size());

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
        ListDeserializer<PersonDeserializer> humans = getTmdbService().searchPerson(getTmdbToken(), messageText, "en-US", 1);

        List<PersonDeserializer> people = humans.getResults();
        int actorsToProcess = Math.min(searchNumber, people.size());

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
            telegramClient.execute(response);
        } catch (TelegramApiException e) {
            logger.error("Error while sending message to chatId {}: {}", chatId, e.getMessage(), e);
        }
        return "";
    }

    protected String handleSimilar(String messageText, long chatId) {
        Validator<String> validator = new IdValidator();

        if (!validator.isValid(messageText)) {
            return validator.getErrorMessage();
        }

        String responseMessage;
        try {
            int filmId = Integer.parseInt(messageText);

            ListDeserializer<FilmDeserializer> films = getTmdbService().getSimilarMovies(getTmdbToken(), filmId);
            FilmDeserializer requestedFilm = getTmdbService().getMovieById(getTmdbToken(), filmId);

            if (films != null && films.getResults() != null && !films.getResults().isEmpty()) {
                List<FilmDeserializer> movies = films.getResults();
                StringBuilder movieListBuilder = new StringBuilder("Похожие фильмы для " + requestedFilm.getTitle() + ":\n");

                for (int i = 0; i < constNumber; i++) {
                    FilmDeserializer currentMovie = movies.get(i);
                    movieListBuilder.append(i + 1).append(". ").append(currentMovie.getTitle()).append("\n");
                }
                responseMessage = movieListBuilder.toString();
            } else {
                responseMessage = "Фильм с индексом " + filmId + " не найден";
            }
            commandWaiter.put(chatId, NONE);
        } catch (Exception e) {
            responseMessage = "Что-то пошло не так";
        }

        return responseMessage;
    }


    protected String handleRecommended(String messageText, long chatId) {
        Validator<String> validator = new IdValidator();

        if (!validator.isValid(messageText)) {
            return validator.getErrorMessage();
        }

        String responseMessage;
        try {
            int filmId = Integer.parseInt(messageText);

            ListDeserializer<FilmDeserializer> films = getTmdbService().getRecommendationsForMovie(getTmdbToken(), filmId);
            FilmDeserializer requestedFilm = getTmdbService().getMovieById(getTmdbToken(), filmId);

            if (films != null && films.getResults() != null && !films.getResults().isEmpty()) {
                List<FilmDeserializer> movies = films.getResults();
                StringBuilder movieListBuilder = new StringBuilder("Рекомендуемые фильмы для фильма " + requestedFilm.getTitle() + ":\n");

                for (int i = 0; i < constNumber; i++) {
                    FilmDeserializer currentMovie = movies.get(i);
                    movieListBuilder.append(i + 1).append(". ").append(currentMovie.getTitle()).append("\n");
                }
                responseMessage = movieListBuilder.toString();
            } else {
                responseMessage = "Фильм с индексом " + filmId + " не найден";
            }
            commandWaiter.put(chatId, NONE);
        } catch (Exception e) {
            responseMessage = "Что-то пошло не так";
        }

        return responseMessage;
    }

    protected String handlePopular(long chatId) {
        String responseMessage;

        try {
            ListDeserializer<FilmDeserializer> popularFilms = getTmdbService().getPopularMovies(getTmdbToken());

            if (popularFilms != null && popularFilms.getResults() != null && !popularFilms.getResults().isEmpty()) {
                List<FilmDeserializer> movies = popularFilms.getResults();
                StringBuilder moviesListBuilder = new StringBuilder("Популярные фильмы:\n");

                for (int i = 0; i < constNumber; i++) {
                    FilmDeserializer currentMovie = movies.get((popularMovieIndex + i) % movies.size());
                    moviesListBuilder.append(popularMovieIndex + i + 1).append(". ").append(currentMovie.getTitle()).append("\n");
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
            ListDeserializer<FilmDeserializer> popularFilms = getTmdbService().getTopRated(getTmdbToken());
            if (popularFilms != null && popularFilms.getResults() != null && !popularFilms.getResults().isEmpty()) {
                List<FilmDeserializer> movies = popularFilms.getResults();
                StringBuilder moviesListBuilder = new StringBuilder("Высоко-оцененные фильмы:\n");
                for (int i = 0; i < constNumber; i++) {
                    FilmDeserializer currentMovie = movies.get((popularMovieIndex + i) % movies.size());
                    moviesListBuilder.append(popularMovieIndex + i + 1).append(". ").append(currentMovie.getTitle()).append("\n");
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
        Validator<String> validator = new IdValidator();

        if (!validator.isValid(messageText)) {
            return validator.getErrorMessage();
        }

        String responseMessage;
        try {
            int filmID = Integer.parseInt(messageText);
            FilmDeserializer film = getTmdbService().getMovieById(getTmdbToken(), filmID);
            ListDeserializer<VideoDeserializer> videos = getTmdbService().getVideosForFilm(getTmdbToken(), filmID);

            if (film != null) {
                StringBuilder filmBuilder = new StringBuilder(film.getTitle());
                if (!Objects.equals(film.getOriginal_language(), "en")) {
                    filmBuilder.append(" / ").append(film.getOriginal_title());
                }
                filmBuilder.append(" (").append(film.getRelease_date(), 0, 4).append(", ")
                        .append(film.getOrigin_country()[0]).append(")").append("\n\n")
                        .append(film.getOverview()).append("\n\n")
                        .append("Vote average: ").append(film.getVote_average()).append("/10\n")
                        .append("Runtime: ").append(film.getRuntime()).append(" min \n");

                if (film.getHomepage() != null) {
                    filmBuilder.append("Link: ").append(film.getHomepage()).append("\n");
                }

                for (int i = 0; i < videos.getResults().size(); i++) {
                    if (videos.getResults().get(i).getId() != null &&
                            Objects.equals(videos.getResults().get(i).getName(), "Official Trailer") &&
                            Objects.equals(videos.getResults().get(i).getSite(), "YouTube") &&
                            Objects.equals(videos.getResults().get(i).getType(), "Trailer") &&
                            videos.getResults().get(i).isOfficial()) {
                        filmBuilder.append("Trailer: ").
                                append("https://youtube.com/watch?v=").append(videos.getResults().get(i).getKey()).append("\n");
                    }
                }

                responseMessage = filmBuilder.toString();
            } else {
                responseMessage = "Извините, я не нашел фильм";
            }
            commandWaiter.put(chatId, NONE);
        } catch (Exception e) {
            responseMessage = "Что-то пошло не так";
        }

        return responseMessage;
    }


    protected String handleSetAge(String messageText, long chatId) {
        Validator<String> validator = new AgeValidator();

        if (!validator.isValid(messageText)) {
            return validator.getErrorMessage();
        }

        return "Спасибо! Учтем ваш ответ";
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