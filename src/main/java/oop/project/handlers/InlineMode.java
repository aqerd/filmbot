package oop.project.handlers;

import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.inlinequery.InlineQuery;

public class InlineMode {
    public static void handleInlineQuery(Update update) {
        InlineQuery inlineQuery = update.getInlineQuery();
    }
}
