package oop.project;

import oop.project.deserializers.*;
import org.telegram.telegrambots.meta.api.methods.AnswerCallbackQuery;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.generics.TelegramClient;
import java.util.List;
import java.util.Objects;
import static java.lang.Integer.parseInt;
import static oop.project.shared.Config.*;

public class HandleButtons {
    public static void handleButtons(Update update, TelegramClient telegramClient) {
        if (update.hasCallbackQuery()) {
            String callbackData = update.getCallbackQuery().getData();
            long chatId = update.getCallbackQuery().getMessage().getChatId();

            if (callbackData.startsWith("movie_")) {
                handleMovieButtons(callbackData, chatId, update, telegramClient);
            } else if (callbackData.startsWith("actor_")) {
                handleActorButtons(callbackData, chatId, update, telegramClient)
            }
        }
    }

    public static void handleMovieButtons(String callbackData, long chatId, Update update, TelegramClient telegramClient) {
        int id = parseInt(callbackData.substring(6));
        FilmDeserializer film = tmdbService().getMovieById(apiToken(), id);
        ListDeserializer<VideoDeserializer> videos = tmdbService().getVideosForMovie(apiToken(), id);

        StringBuilder filmBuilder = new StringBuilder("**" + film.getTitle() + "**");
        if (!Objects.equals(film.getOriginal_language(), "en")) {
            filmBuilder.append(" / ").append(film.getOriginal_title());
        }
        filmBuilder.append(" (").append(film.getRelease_date(), 0, 4).append(", ")
                .append(film.getOrigin_country()[0]).append(")").append("\n\n")
                .append(film.getOverview().replace("\n", " ")).append("\n\n")
                .append("Vote average: ").append(film.getVote_average()).append("/10\n")
                .append("Runtime: ").append(film.getRuntime()).append(" min \n");
        if (film.getHomepage() != null && !film.getHomepage().isEmpty()) {
            filmBuilder.append("[Homepage](").append(film.getHomepage()).append(") ");
        }

        for (int i = 0; i < videos.getResults().size(); i++) {
            if (videos.getResults().get(i).getId() != null &&
                    Objects.equals(videos.getResults().get(i).getName(), "Official Trailer") &&
                    Objects.equals(videos.getResults().get(i).getSite(), "YouTube") &&
                    Objects.equals(videos.getResults().get(i).getType(), "Trailer") &&
                    videos.getResults().get(i).isOfficial()) {
                filmBuilder.append("/ [Youtube](").append(youtubeUrl()).append(videos.getResults().get(i).getKey()).append(")").append("\n");
            }
        }

        String responseMessage = filmBuilder.toString();

        SendMessage response = SendMessage
                .builder()
                .chatId(String.valueOf(chatId))
                .text("Выберите фильм:")
                .build();
        response.setChatId(String.valueOf(chatId));
        response.enableMarkdown(true);
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
    }

    public static void handleActorButtons(String callbackData, long chatId, Update update, TelegramClient telegramClient) {
        int id = parseInt(callbackData.substring(6));
        String responseMessage;
        List<FilmDeserializer> movies;
        CreditsDeserializer actorsFilms = tmdbService().getActorsMovies(apiToken(), id).sortByPopularity();
        PersonDeserializer actor = tmdbService().getActorById(apiToken(), id);

        StringBuilder actorsData = new StringBuilder("**" + actor.getName() + "**").append(" (").append(actor.getBirthday(), 0, 4);
        if (actor.getDeathday() != null) {
            actorsData.append(" - ").append(actor.getDeathday(), 0, 4);
        }
        actorsData.append(")").append("\n").append("Place of birth: ").append(actor.getPlace_of_birth().trim())
                .append("\n").append("Popularity: ").append(actor.getPopularity())
                .append("\n").append("ID: ").append(actor.getId()).append("\n\n");
        if (!Objects.equals(actor.getBiography().replace("\n", " "), "")) {
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
                int CONST_NUM = 12;
                int filmsToDisplay = Math.min(CONST_NUM, movies.size());
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
        response.enableMarkdown(true);
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
