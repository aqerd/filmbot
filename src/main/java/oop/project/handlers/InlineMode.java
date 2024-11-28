package oop.project.handlers;

import oop.project.deserializers.FilmDeserializer;
import oop.project.deserializers.ListDeserializer;
import org.telegram.telegrambots.meta.api.methods.AnswerInlineQuery;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.inlinequery.inputmessagecontent.InputMessageContent;
import org.telegram.telegrambots.meta.api.objects.inlinequery.inputmessagecontent.InputTextMessageContent;
import org.telegram.telegrambots.meta.api.objects.inlinequery.result.InlineQueryResult;
import org.telegram.telegrambots.meta.api.objects.inlinequery.result.InlineQueryResultArticle;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.generics.TelegramClient;
import java.util.ArrayList;
import java.util.List;
import static oop.project.TelegramBot.getTelegramClient;
import static oop.project.shared.Config.apiToken;
import static oop.project.shared.Config.tmdbService;
import static oop.project.shared.Responses.responseWithMovie;

public class InlineMode {
    private static final int SEARCH_NUM = 6;
    private static final TelegramClient TG_CLIENT = getTelegramClient();

    public static void handleInlineQuery(Update update) {
        String query = update.getInlineQuery().getQuery();

        if (query.isEmpty()) {
            return;
        }

        ListDeserializer<FilmDeserializer> films = tmdbService()
                .searchMovie(apiToken(), query, "en-US", 1).sortByPopularity();

        List<FilmDeserializer> movies = films.getResults();
        int filmsToProcess = Math.min(SEARCH_NUM, movies.size());

        if (filmsToProcess == 0) {
            AnswerInlineQuery emptyResponse = AnswerInlineQuery.builder()
                    .inlineQueryId(update.getInlineQuery().getId())
                    .results(new ArrayList<>())
                    .cacheTime(1)
                    .build();
            try {
                TG_CLIENT.execute(emptyResponse);
            } catch (TelegramApiException e) {
                e.printStackTrace();
            }
            return;
        }

        List<InlineQueryResult> results = new ArrayList<>();

        for (int i = 0; i < filmsToProcess; i++) {
            FilmDeserializer movie = movies.get(i);
            String title = movie.getTitle();
            String year = movie.getRelease_date().substring(0, 4);
            String country = movie.getOrigin_country().get(0);

            String formattedTitle = title + " (" + year + " / " + country + ")";

            InputMessageContent messageContent = InputTextMessageContent.builder()
                    .messageText(responseWithMovie(movie.getId()))
                    .build();

            InlineQueryResultArticle result = InlineQueryResultArticle.builder()
                    .id(String.valueOf(movie.getId()))
                    .title(formattedTitle)
                    .description(movie.getOverview())
                    .inputMessageContent(messageContent)
                    .build();

            results.add(result);
        }

        AnswerInlineQuery answer = AnswerInlineQuery.builder()
                .inlineQueryId(update.getInlineQuery().getId())
                .results(results)
                .cacheTime(1)
                .build();

        try {
            TG_CLIENT.execute(answer);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }
}