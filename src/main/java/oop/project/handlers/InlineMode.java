package oop.project.handlers;

import oop.project.deserializers.FilmDeserializer;
import oop.project.deserializers.ListDeserializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.telegram.telegrambots.meta.api.methods.AnswerInlineQuery;
import org.telegram.telegrambots.meta.api.objects.Update;
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
    private static final Logger LOG = LoggerFactory.getLogger(InlineMode.class);
    private static final int SEARCH_NUM = 6;
    private static final TelegramClient TG_CLIENT = getTelegramClient();

    public static void handleInlineQuery(Update update) {
        if (update.getInlineQuery() == null) {
            return;
        }

        String query = update.getInlineQuery().getQuery();
        LOG.info("INLINE, Query: {}", query);
        ListDeserializer<FilmDeserializer> films = tmdbService()
                .searchMovie(apiToken(), query, "en-US", 1).sortByPopularity();
        List<InlineQueryResult> results = new ArrayList<>();
        int minNum = Math.min(SEARCH_NUM, films.getResults().size());

        for (int i = 0; i < minNum; i++) {
            String id = String.valueOf(i);
            FilmDeserializer currentFilm = films.getResults().get(i);
            InlineQueryResultArticle article = InlineQueryResultArticle.builder()
                    .id("movie_" + id)
                    .title(currentFilm.getTitle())
                    .description(currentFilm.getOverview())
                    .inputMessageContent(InputTextMessageContent.builder()
                            .messageText(responseWithMovie(currentFilm.getId()))
                            .parseMode("Markdown")
                            .build())
                    .build();
            results.add(article);
        }

        AnswerInlineQuery answer = AnswerInlineQuery.builder()
                .inlineQueryId(update.getInlineQuery().getId())
                .results(results)
                .build();

        try {
            TG_CLIENT.execute(answer);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }
}
