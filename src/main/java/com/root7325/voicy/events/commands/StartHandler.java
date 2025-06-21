package com.root7325.voicy.events.commands;

import com.google.inject.Inject;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.model.WebAppInfo;
import com.pengrad.telegrambot.model.request.InlineKeyboardButton;
import com.pengrad.telegrambot.model.request.InlineKeyboardMarkup;
import com.root7325.voicy.events.BaseEventListener;
import com.root7325.voicy.helpers.MessageHelper;
import com.root7325.voicy.services.TranslationService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * @author root7325 on 12.06.2025
 */
@Slf4j
@AllArgsConstructor(onConstructor = @__({@Inject}))
public class StartHandler extends BaseEventListener {
    private final TranslationService translationService;

    @Override
    public void onUpdateReceived(Update update) {
        long chatId = update.message().chat().id();
        int messageId = update.message().messageId();
        String message = translationService.getMessage("ru", "start.message");

        MessageHelper.sendSimpleMessage(chatId, messageId, message);
    }

    @Override
    public boolean filter(Update update) {
        return super.filter(update)
                && update.message() != null
                && "/start".equals(update.message().text());
    }
}