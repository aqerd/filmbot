package org.oopproject;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import feign.Feign;
import feign.gson.GsonDecoder;
import io.github.cdimascio.dotenv.Dotenv;
import org.oopproject.movies.ListOfResults;
import org.oopproject.movies.SiteRequests;
import org.telegram.telegrambots.longpolling.TelegramBotsLongPollingApplication;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

public class Main {
    public static void main(String[] args) {
        try {
            Dotenv dotenv = Dotenv.configure().directory("assets").filename("token.env").load();
            String tgBotToken = dotenv.get("TELEGRAM_BOT_TOKEN");
            String tmdbToken = dotenv.get("TMDB_ACCESS_TOKEN");

            TelegramBotsLongPollingApplication bot = new TelegramBotsLongPollingApplication();
            bot.registerBot(tgBotToken, new MyBot(tgBotToken));
            System.out.println("SUCCESS: Bot is running");

            SiteRequests tmdbService = Feign
                .builder()
                .decoder(new GsonDecoder())
                .target(SiteRequests.class, "https://api.themoviedb.org/3");

            ListOfResults popularMoviesResults = tmdbService.getPopularMovies(tmdbToken);

//            System.out.println(popularMoviesResults);
//            Gson gson = new GsonBuilder().setPrettyPrinting().create();
//            String formattedJson = gson.toJson(popularMoviesResults);
//            System.out.println(formattedJson);

            popularMoviesResults.results.forEach(movie -> {
                System.out.println("Object: " + movie);
                System.out.println("Title: " + movie.title);
                System.out.println("Overview: " + movie.overview);
                System.out.println("Id: " + movie.id);
                System.out.println("Rating: " + movie.rating);
                System.out.println();
            });

        } catch (TelegramApiException err) {
            err.printStackTrace();
            System.out.println("ERROR: Bot is NOT running");
        }
    }
}
