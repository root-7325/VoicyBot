package com.root7325.voicy.event.commands;

import com.google.inject.Inject;
import com.pengrad.telegrambot.model.Update;
import com.root7325.voicy.event.BaseEventListener;
import com.root7325.voicy.helper.IMessageHelper;
import com.root7325.voicy.service.TranslationService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * @author root7325 on 12.06.2025
 */
@Slf4j
@AllArgsConstructor(onConstructor = @__({@Inject}))
public class StartHandler extends BaseEventListener {
    private final IMessageHelper messageHelper;
    private final TranslationService translationService;

    @Override
    public void onUpdateReceived(Update update) {
        long chatId = update.message().chat().id();
        int messageId = update.message().messageId();
        String message = translationService.getMessage("ru", "start.message");

        messageHelper.sendSimpleMessage(chatId, messageId, message);
    }

    @Override
    public boolean filter(Update update) {
        return super.filter(update)
                && update.message() != null
                && "/start".equals(update.message().text());
    }
}