package com.root7325.voicy.events.messages;

import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.model.Voice;
import com.root7325.voicy.events.BaseEventListener;
import com.root7325.voicy.helpers.MessageHelper;
import com.root7325.voicy.services.AIService;
import com.root7325.voicy.services.VoiceService;
import com.root7325.voicy.services.VoskService;
import com.root7325.voicy.utils.AudioConverter;
import lombok.extern.slf4j.Slf4j;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.concurrent.*;

/**
 * @author root7325 on 13.06.2025
 */
@Slf4j
public class VoiceHandler extends BaseEventListener {
    private final ExecutorService executorService = Executors.newFixedThreadPool(4);
    private final VoiceService voiceService = new VoiceService(translationService, executorService);
    private final AIService aiService = new AIService();
    private final VoskService voskService = new VoskService();

    @Override
    public void onUpdateReceived(Update update) {
        long chatId = update.message().chat().id();

        log.debug("Starting processing voice in {}", chatId);
        CompletableFuture<byte[]> voiceCompletableFuture = voiceService.processVoiceMessage(chatId, "ru", update.message().voice());
        voiceCompletableFuture.thenAccept(voiceData -> {
            String recognized = voskService.recognizeSpeech(voiceData);
            MessageHelper.sendSimpleMessage(chatId, "Recognized: " + recognized);

            CompletableFuture<String> responseFuture = aiService.generateResponse("Что ты думаешь о моих мыслях? Мысль: " + recognized);
            responseFuture.thenAccept(response -> {
                MessageHelper.sendSimpleMessage(chatId, response);
            });
        }).exceptionally(ex -> {
            log.error("dead ass", ex);
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
