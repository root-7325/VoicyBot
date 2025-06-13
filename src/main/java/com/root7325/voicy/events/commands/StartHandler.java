package com.root7325.voicy.events.commands;

import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.model.WebAppInfo;
import com.pengrad.telegrambot.model.request.InlineKeyboardButton;
import com.pengrad.telegrambot.model.request.InlineKeyboardMarkup;
import com.root7325.voicy.events.BaseEventListener;
import com.root7325.voicy.helpers.MessageHelper;
import com.root7325.voicy.utils.Config;
import lombok.extern.slf4j.Slf4j;

/**
 * @author root7325 on 12.06.2025
 */
@Slf4j
public class StartHandler extends BaseEventListener {
    @Override
    public void onUpdateReceived(Update update) {
        long id = update.message().chat().id();
        String message = translationService.getMessage("ru", "welcome.info");

        MessageHelper.sendSimpleMessage(id, message);
    }

    @Override
    public boolean filter(Update update) {
        return super.filter(update)
                && update.message() != null
                && "/start".equals(update.message().text());
    }
}