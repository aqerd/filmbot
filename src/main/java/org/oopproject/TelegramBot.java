package org.oopproject;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import feign.Feign;
import feign.Logger;
import feign.gson.GsonDecoder;
import feign.gson.GsonEncoder;
import org.oopproject.deserializers.ListDeserializer;
import org.oopproject.deserializers.MovieVideosResponse;
import org.oopproject.deserializers.VideoDeserializer;
import org.oopproject.utils.CommandWaiter;
import org.oopproject.utils.Genres;
import org.oopproject.parameters.MovieParameters;
import org.oopproject.parameters.ParametersBuilder;
import org.oopproject.deserializers.FilmDeserializer;
import static org.oopproject.utils.CommandWaiter.*;
import static org.oopproject.utils.Config.tmdbService;
import static org.oopproject.utils.Validators.isCommand;
import static org.oopproject.utils.Replies.getReply;
import java.lang.reflect.Type;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.*;
import java.util.stream.Collectors;

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

    public TelegramBot(String botToken) throws SQLException {
        telegramClient = new OkHttpTelegramClient(botToken);
        BroadcastingService broadcastingService = new BroadcastingService();  // Initialize BroadcastingService
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

            MovieParameters params = new ParametersBuilder()
                    .withGenres(genreId)
                    .withCertificationLte("PG-13")
                    .withCertificationCountry("US")
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

            if (userYear < 1900 || userYear > currentYear) {
                responseMessage = "Пожалуйста, введите год в диапазоне от 1900 до " + currentYear;
                return responseMessage;
            }

            MovieParameters params = new ParametersBuilder()
                    .withYear(userYear)
                    .withCertificationLte("PG-13")
                    .withCertificationCountry("US")
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

    public class MovieService {

        private SiteRequests siteRequests;
        private int currentPage;

        public MovieService() {
            siteRequests = Feign.builder()
                    .encoder(new GsonEncoder())
                    .decoder(new GsonDecoder())
                    .logLevel(Logger.Level.FULL)
                    .target(SiteRequests.class, "https://api.themoviedb.org/3");
            this.currentPage = 1;
        }

        private boolean isUpcoming(String releaseDate) {
            try {
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                Date release = sdf.parse(releaseDate);
                Date now = new Date();
                return release.after(now);
            } catch (ParseException e) {
                e.printStackTrace();
            }
            return false;
        }
        public List<FilmDeserializer> getUpcomingMovies() {
            try {
                ListDeserializer upcomingMovies = siteRequests.findUpcomingMovies(
                        "240e7fef369901fb314c80d53d1532d1",
                        "ru",
                        currentPage
                );

                if (upcomingMovies != null && upcomingMovies.results != null) {
                    List<FilmDeserializer> resultList = upcomingMovies.results.stream()
                            .filter(movie -> isUpcoming(movie.release_date))
                            .limit(10) // Ограничиваем до 10 фильмов
                            .collect(Collectors.toList());
                    for (FilmDeserializer movie : resultList) {
                        MovieVideosResponse videoResponse = siteRequests.getMovieVideos(
                                "240e7fef369901fb314c80d53d1532d1",
                                String.valueOf(movie.id),
                                "ru"
                        );

                        if (videoResponse != null && videoResponse.results != null && !videoResponse.results.isEmpty()) {
                            // Найдем первый трейлер (если он есть)
                            VideoDeserializer trailer = videoResponse.results.stream()
                                    .filter(v -> v.site.equals("YouTube"))
                                    .findFirst()
                                    .orElse(null);

                            if (trailer != null) {
                                // Формируем ссылку на трейлер
                                movie.trailerUrl = "https://www.youtube.com/watch?v=" + trailer.key;
                            }
                        }
                    }
                    currentPage++;
                    return resultList;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return Collections.emptyList();
        }
    }

    public class BroadcastingService {

        private MovieService movieService;

        public BroadcastingService() {
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
            }, 0, 1, TimeUnit.MINUTES);
        }
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