package org.oopproject;

import org.oopproject.responses.AuthResponse;
import org.oopproject.responses.FilmResponse;
import org.oopproject.responses.ListResponse;
import feign.Feign;
import feign.FeignException;
import feign.gson.GsonDecoder;
import com.google.gson.GsonBuilder;
import io.github.cdimascio.dotenv.Dotenv;
import org.telegram.telegrambots.longpolling.TelegramBotsLongPollingApplication;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

public class Main {
    public static void main(String[] args) {
        try {
            Dotenv dotenv = Dotenv.configure().directory("assets").filename("token.env").load();
            final String BOT_TOKEN = dotenv.get("TELEGRAM_BOT_TOKEN");
            final String TMDB_TOKEN = dotenv.get("TMDB_ACCESS_TOKEN");
            final String API_URL = "https://api.themoviedb.org/3";

            TelegramBotsLongPollingApplication bot = new TelegramBotsLongPollingApplication();
            bot.registerBot(BOT_TOKEN, new MyBot(BOT_TOKEN));
            System.out.println("SUCCESS: Bot is running");

            SiteRequests tmdbService = Feign
                    .builder()
                    .decoder(new GsonDecoder())
                    .target(SiteRequests.class, API_URL);

            ListResponse popularMoviesResults = tmdbService.getPopularMovies(TMDB_TOKEN);
            FilmResponse movieById = tmdbService.getMovieById(TMDB_TOKEN, "725201");
            AuthResponse auth = tmdbService.checkAuthStatus(TMDB_TOKEN);
            ListResponse findMovie = tmdbService.findMovie(TMDB_TOKEN, true, "ru", 1,
                    "1900-01-01", "2100-01-01", "popularity.desc", 7,
                    10, "28|18", 90, 2022);

            String gsonFindMovie = new GsonBuilder()
                    .setPrettyPrinting()
                    .create()
                    .toJson(findMovie);
            System.out.println(gsonFindMovie);

//            String gsonPopular = new GsonBuilder()
//                    .setPrettyPrinting()
//                    .create()
//                    .toJson(popularMoviesResults.results);
//            System.out.println(gsonPopular);
//            System.out.println("-------------------------");
//            String gsonId = new GsonBuilder()
//                    .setPrettyPrinting()
//                    .create()
//                    .toJson(movieById);
//            System.out.println(gsonId);
//            System.out.println("-------------------------");
//            String gsonAuth = new GsonBuilder()
//                    .setPrettyPrinting()
//                    .create()
//                    .toJson(auth);
//            System.out.println(gsonAuth);
//
//            popularMoviesResults.results.forEach(movie -> {
//                System.out.println("Object: " + movie);
//                System.out.println("Title: " + movie.title);
//                System.out.println("Overview: " + movie.overview);
//                System.out.println("Id: " + movie.id);
//                System.out.println("Rating: " + movie.vote_average);
//                System.out.println();
//            });

        } catch (TelegramApiException e) {
            e.printStackTrace();
            System.out.println("ERROR: Bot is NOT running. Something wrong went with Telegram API");
        } catch (FeignException e) {
            if (e.status() == 401) {
                System.out.println("ERROR: Invalid TMDB TOKEN");
            } else {
                System.out.println("ERROR: Something wrong went with Feign: " + e.getMessage());
            }
        } catch (Exception e) {
            System.out.println("ERROR: Unexpected error" + e.getMessage());
        }
    }
}
