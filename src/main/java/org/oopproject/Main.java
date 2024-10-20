package org.oopproject;

//import org.oopproject.responses.AuthResponse;
//import org.oopproject.responses.FilmResponse;
//import org.oopproject.responses.ListResponse;
//import feign.Feign;
import feign.FeignException;
//import feign.gson.GsonDecoder;
//import com.google.gson.GsonBuilder;
//import io.github.cdimascio.dotenv.Dotenv;
import org.telegram.telegrambots.longpolling.TelegramBotsLongPollingApplication;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import static org.oopproject.Config.*;

public class Main {
    public static void main(String[] args) {
        try {
//            Dotenv dotenv = Dotenv.configure().directory("assets").filename("token.env").load();
//            final String BOT_TOKEN = dotenv.get("TELEGRAM_BOT_TOKEN");
//            final String TMDB_TOKEN = dotenv.get("TMDB_ACCESS_TOKEN");
//            final String API_URL = "https://api.themoviedb.org/3";

            TelegramBotsLongPollingApplication bot = new TelegramBotsLongPollingApplication();
            bot.registerBot(BOT_TOKEN, new MyBot(BOT_TOKEN));
            System.out.println("SUCCESS: Bot is running");

//            ListResponse popularMoviesResults = tmdbService.getPopularMovies(TMDB_TOKEN);
//            FilmResponse movieById = tmdbService.getMovieById(TMDB_TOKEN, "725201");
//            AuthResponse auth = tmdbService.checkAuthStatus(TMDB_TOKEN);

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
