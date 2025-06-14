package com.root7325.voicy.events.commands;

import com.pengrad.telegrambot.model.Update;
import com.root7325.voicy.events.BaseEventListener;
import com.root7325.voicy.events.messages.VoiceHandler;
import com.root7325.voicy.helpers.MessageHelper;
import lombok.extern.slf4j.Slf4j;

/**
 * @author root7325 on 12.06.2025
 */
@Slf4j
public class AskHandler extends BaseEventListener {
    @Override
    public void onUpdateReceived(Update update) {
        long chatId = update.message().chat().id();
        int messageId = update.message().messageId();
        String languageCode = "ru";

        String waitMessage = translationService.getMessage(languageCode, "ask.message");
        String prompt = update.message().text().split("/ask ")[1];

        MessageHelper.sendSimpleMessage(chatId, messageId, waitMessage);
        VoiceHandler.processRecognizedSpeech(translationService, llmService,
                chatId, messageId, languageCode, prompt);
    }

    @Override
    public boolean filter(Update update) {
        return super.filter(update)
                && update.message() != null
                && update.message().text().contains("/ask");
    }
}