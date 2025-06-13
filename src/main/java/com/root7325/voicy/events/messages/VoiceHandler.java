package com.root7325.voicy.events.messages;

import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.model.request.ParseMode;
import com.root7325.voicy.events.BaseEventListener;
import com.root7325.voicy.helpers.MessageHelper;
import com.root7325.voicy.services.LLMService;
import com.root7325.voicy.services.VoiceService;
import com.root7325.voicy.services.VoskService;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.*;

/**
 * @author root7325 on 13.06.2025
 */
@Slf4j
public class VoiceHandler extends BaseEventListener {
    private final ExecutorService executorService = Executors.newFixedThreadPool(4);
    private final VoiceService voiceService = new VoiceService(translationService, executorService);
    private final LLMService llmService = new LLMService(executorService);
    private final VoskService voskService = new VoskService();

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

            processRecognizedSpeech(chatId, messageId, "Что ты думаешь о моих мыслях? Мысль: " + recognized);
        }).exceptionally(ex -> {
            log.error("Error completing tasks.", ex);
            return null;
        });
    }

    private void processRecognizedSpeech(long chatId, int messageId, String recognized) {
        CompletableFuture<String> responseFuture = llmService.generateResponse(recognized);
        responseFuture.thenAccept(response -> {
            MessageHelper.sendSimpleMessage(chatId, messageId, response, ParseMode.Markdown);
        }).exceptionally(ex -> {
            log.error("Error generating llm response.", ex);
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
