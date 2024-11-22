package oop.project.shared;

import com.google.gson.Gson;
import feign.Feign;
import feign.gson.GsonDecoder;
import io.github.cdimascio.dotenv.Dotenv;
import oop.project.SiteRequests;

public class Config {
    private static final String BOT_TOKEN;
    private static final String TMDB_TOKEN;
    private static final String BASE_URL;
    private static final SiteRequests TMDB_SERVICE;

    static {
        Dotenv dotenv = Dotenv.configure().directory("assets").filename("token.env").load();
        BOT_TOKEN = dotenv.get("TELEGRAM_BOT_TOKEN");
        TMDB_TOKEN = dotenv.get("TMDB_ACCESS_TOKEN");
        BASE_URL = "https://api.themoviedb.org/3";
        TMDB_SERVICE = Feign.builder()
                .decoder(new GsonDecoder(new Gson()))
                .target(SiteRequests.class, BASE_URL);
    }

    public static String getBotToken() {
        return BOT_TOKEN;
    }

    public static String getTmdbToken() {
        return TMDB_TOKEN;
    }

    public static String getBaseUrl() {
        return BASE_URL;
    }

    public static SiteRequests getTmdbService() {
        return TMDB_SERVICE;
    }
}
