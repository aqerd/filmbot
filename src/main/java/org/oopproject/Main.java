package org.oopproject;

import io.github.cdimascio.dotenv.Dotenv;
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
        } catch (TelegramApiException err) {
            err.printStackTrace();
            System.out.println("ERROR: Bot is NOT running");
        }
    }
}
