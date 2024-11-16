package org.oopproject;

import feign.FeignException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.longpolling.TelegramBotsLongPollingApplication;
import static org.oopproject.utils.Config.*;

/****************************** PLAYGROUND ******************************/
import static org.oopproject.utils.Validators.printPrettyJson;
import org.oopproject.deserializers.FilmDeserializer;
import org.oopproject.deserializers.ListDeserializer;
import org.oopproject.deserializers.AuthDeserializer;
import org.oopproject.parameters.MovieParameters;
import org.oopproject.parameters.ParametersBuilder;
/****************************** PLAYGROUND ******************************/

public class Main {
    private static final Logger logger = LoggerFactory.getLogger(Main.class);

    public static void main(String[] args) {
        try {
            TelegramBotsLongPollingApplication bot = new TelegramBotsLongPollingApplication();
            bot.registerBot(BOT_TOKEN, new TelegramBot(BOT_TOKEN));
            logger.info("Bot is running");

            /****************************** PLAYGROUND ******************************/
            // OK 200: "725201" (The Gray Man)
            // NOT FOUND 404: "1212"

            MovieParameters params = new ParametersBuilder()
                    .withGenres("28")
                    .withCertificationLte("PG-13")
                    .withCertificationCountry("US")
                    .build();

//            FilmDeserializer m1 = tmdbService.getMovieById(TMDB_TOKEN, "725201");
//            ListDeserializer m2 = tmdbService.getPopularMovies(TMDB_TOKEN);
//            AuthDeserializer m3 = tmdbService.checkAuthStatus(TMDB_TOKEN);
//            ListDeserializer m5 = tmdbService.getSimilarMovies(TMDB_TOKEN, "725201");
//            ListDeserializer m4 = tmdbService.findMovie(params);
//            ListDeserializer m6 = tmdbService.getRecommendationsForMovie(TMDB_TOKEN, "725201");
//            ListDeserializer m7 = tmdbService.searchMovie(TMDB_TOKEN, "Oppen", "en-US", 1, "2023");
            ListDeserializer m8 = tmdbService.searchPerson(TMDB_TOKEN, "ryan", "en-US", 1);

            printPrettyJson(m8);
            /****************************** PLAYGROUND ******************************/

        } catch (TelegramApiException e) {
            e.printStackTrace();
            logger.error("Bot is NOT running. Something wrong went with Telegram API");
        } catch (FeignException e) {
            if (e.status() == 401) {
                logger.error("Invalid TMDB TOKEN");
            } else if (e.status() == 404) {
                logger.error("Could not find by user's parameters: {}", e.getMessage());
            } else {
                logger.error("Something wrong went with Feign: {}", e.getMessage());
            }
        } catch (Exception e) {
            logger.error("Unexpected error {}", e.getMessage());
        }
    }
}