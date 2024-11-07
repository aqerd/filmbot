package org.oopproject.utils;

import feign.Feign;
import feign.gson.GsonDecoder;
import io.github.cdimascio.dotenv.Dotenv;
import org.oopproject.SiteRequests;

public class Config {
    public static final String BOT_TOKEN;
    public static final String TMDB_TOKEN;
    public static final String BASE_URL;
    public static SiteRequests tmdbService;

    static {
        Dotenv dotenv = Dotenv.configure().directory("assets").filename("token.env").load();
        BOT_TOKEN = dotenv.get("TELEGRAM_BOT_TOKEN");
        TMDB_TOKEN = dotenv.get("TMDB_ACCESS_TOKEN");
        BASE_URL = "https://api.themoviedb.org/3";

        tmdbService = Feign
                .builder()
                .decoder(new GsonDecoder())
                .target(SiteRequests.class, BASE_URL);
    }
}
