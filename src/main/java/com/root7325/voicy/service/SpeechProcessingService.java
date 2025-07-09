package com.root7325.voicy.service;

import com.google.inject.Inject;
import com.pengrad.telegrambot.model.request.ParseMode;
import com.root7325.voicy.config.TgConfig;
import com.root7325.voicy.helper.IMessageHelper;
import lombok.AllArgsConstructor;

import java.util.concurrent.CompletableFuture;

/**
 * @author root7325 on 09.07.2025
 */
@AllArgsConstructor(onConstructor = @__({@Inject}))
public class SpeechProcessingService {
    private final TgConfig tgConfig;
    private final TranslationService translationService;
    private final LLMService llmService;
    private final IMessageHelper messageHelper;

    public void processRecognizedSpeech(long chatId, int messageId, String recognized) {
        CompletableFuture<String> responseFuture = llmService.generateResponse(recognized);
        responseFuture.thenAccept(response -> {
            String message = translationService.getMessage(tgConfig.getLanguage(), "ai.response");
            messageHelper.sendSimpleMessage(chatId, messageId, String.format(message, response), ParseMode.Markdown);
        }).exceptionally(ex -> {
            String message = translationService.getMessage(tgConfig.getLanguage(), "error.server_error") + ex.getMessage();
            messageHelper.sendSimpleMessage(chatId, messageId, message);
            return null;
        });
    }
}
