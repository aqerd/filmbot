package org.oopproject;

import feign.FeignException;
import org.telegram.telegrambots.longpolling.TelegramBotsLongPollingApplication;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import static org.oopproject.Config.*;

public class Main {
    public static void main(String[] args) {
        try {

            TelegramBotsLongPollingApplication bot = new TelegramBotsLongPollingApplication();
            bot.registerBot(BOT_TOKEN, new MyBot(BOT_TOKEN));
            System.out.println("SUCCESS: Bot is running");

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
