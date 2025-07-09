package com.root7325.voicy.event.commands;

import com.google.inject.Inject;
import com.pengrad.telegrambot.model.Update;
import com.root7325.voicy.config.TgConfig;
import com.root7325.voicy.event.BaseEventListener;
import com.root7325.voicy.helper.IMessageHelper;
import com.root7325.voicy.service.SpeechProcessingService;
import com.root7325.voicy.service.TranslationService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * @author root7325 on 12.06.2025
 */
@Slf4j
@AllArgsConstructor(onConstructor = @__({@Inject}))
public class AskHandler extends BaseEventListener {
    private final TgConfig tgConfig;
    private final IMessageHelper messageHelper;
    private final TranslationService translationService;
    private final SpeechProcessingService speechProcessingService;

    @Override
    public void onUpdateReceived(Update update) {
        long chatId = update.message().chat().id();
        int messageId = update.message().messageId();

        String waitMessage = translationService.getMessage(tgConfig.getLanguage(), "ask.message");
        String prompt = update.message().text().split("/ask ")[1];

        messageHelper.sendSimpleMessage(chatId, messageId, waitMessage);
        speechProcessingService.processRecognizedSpeech(chatId, messageId, prompt);
    }

    @Override
    public boolean filter(Update update) {
        return super.filter(update)
                && update.message() != null
                && update.message().text() != null
                && update.message().text().contains("/ask");
    }
}