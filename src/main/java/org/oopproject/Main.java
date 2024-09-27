package org.oopproject;

import io.github.cdimascio.dotenv.Dotenv;
import org.telegram.telegrambots.longpolling.TelegramBotsLongPollingApplication;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

public class Main {
    public static void main(String[] args) {
        try {
            Dotenv dotenv = Dotenv.configure().directory("assets").filename("token.env").load();
            String TOKEN = dotenv.get("TELEGRAM_BOT_TOKEN");
            TelegramBotsLongPollingApplication bot = new TelegramBotsLongPollingApplication();
            bot.registerBot(TOKEN, new MyBot(TOKEN));
            System.out.println("SUCCESS: Bot is running");
        } catch (TelegramApiException err) {
            err.printStackTrace();
            System.out.println("ERROR: Bot is NOT running");
        }
    }
}