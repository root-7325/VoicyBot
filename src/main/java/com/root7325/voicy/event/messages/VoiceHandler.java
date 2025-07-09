package com.root7325.voicy.event.messages;

import com.google.inject.Inject;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.model.request.ParseMode;
import com.root7325.voicy.event.BaseEventListener;
import com.root7325.voicy.helper.MessageHelper;
import com.root7325.voicy.service.LLMService;
import com.root7325.voicy.service.TranslationService;
import com.root7325.voicy.service.VoiceService;
import com.root7325.voicy.service.VoskService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.*;

/**
 * @author root7325 on 13.06.2025
 */
@Slf4j
@AllArgsConstructor(onConstructor = @__({@Inject}))
public class VoiceHandler extends BaseEventListener {
    private final TranslationService translationService;
    private final LLMService llmService;
    private final VoiceService voiceService;
    private final VoskService voskService;

    @Override
    public void onUpdateReceived(Update update) {
        long chatId = update.message().chat().id();
        int messageId = update.message().messageId();
        String languageCode = "ru"; // todo: determine (may be null in chat)

        log.debug("Starting processing voice in {}", chatId);
        CompletableFuture<byte[]> voiceCompletableFuture = voiceService.processVoiceMessage(chatId, messageId, languageCode, update.message().voice());
        voiceCompletableFuture.thenAccept(voiceData -> {
            String message = translationService.getMessage(languageCode, "voice.recognized");
            String recognized = voskService.recognizeSpeech(voiceData);
            MessageHelper.sendSimpleMessage(chatId, messageId, String.format(message, recognized));

            processRecognizedSpeech(translationService, llmService, chatId, messageId, languageCode, recognized);
        }).exceptionally(ex -> {
            log.error("Error completing tasks.", ex);
            return null;
        });
    }

    public static void processRecognizedSpeech(TranslationService translationService, LLMService llmService, long chatId, int messageId, String languageCode, String recognized) {
        CompletableFuture<String> responseFuture = llmService.generateResponse(recognized);
        responseFuture.thenAccept(response -> {
            String message = translationService.getMessage(languageCode, "ai.response");
            MessageHelper.sendSimpleMessage(chatId, messageId, String.format(message, response), ParseMode.Markdown);
        }).exceptionally(ex -> {
            log.error("Error generating llm response.", ex);

            String message = translationService.getMessage(languageCode, "error.server_error") + ex.getMessage();
            MessageHelper.sendSimpleMessage(chatId, messageId, message);
            return null;
        });
    }

    @Override
    public boolean filter(Update update) {
        return super.filter(update)
                && update.message() != null
                && update.message().voice() != null;
    }
}
