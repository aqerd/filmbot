package oop.project;

import feign.FeignException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.longpolling.TelegramBotsLongPollingApplication;
import static oop.project.shared.Config.botToken;

public class Main {
    private static final Logger LOG = LoggerFactory.getLogger(Main.class);

    public static void main(String[] args) {
        try {
            TelegramBotsLongPollingApplication bot = new TelegramBotsLongPollingApplication();
            bot.registerBot(botToken(), new TelegramBot(botToken()));
            LOG.info("Bot is running");
        } catch (TelegramApiException e) {
            e.printStackTrace();
            LOG.error("Bot is NOT running. Something wrong went with Telegram API");
        } catch (FeignException e) {
            if (e.status() == 401) {
                LOG.error("Invalid TMDB TOKEN");
            } else if (e.status() == 404) {
                LOG.error("Could not find by user's parameters: {}", e.getMessage());
            } else {
                LOG.error("Something wrong went with Feign: {}", e.getMessage());
            }
        } catch (Exception e) {
            LOG.error("Unexpected error {}", e.getMessage());
        }
    }
}