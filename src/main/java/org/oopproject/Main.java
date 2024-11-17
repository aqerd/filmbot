package org.oopproject;

import feign.FeignException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.longpolling.TelegramBotsLongPollingApplication;
import static org.oopproject.utils.Config.*;

public class Main {
    private static final Logger logger = LoggerFactory.getLogger(Main.class);

    public static void main(String[] args) {
        try {
            TelegramBotsLongPollingApplication bot = new TelegramBotsLongPollingApplication();
            bot.registerBot(BOT_TOKEN, new TelegramBot(BOT_TOKEN));
            logger.info("Bot is running");
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