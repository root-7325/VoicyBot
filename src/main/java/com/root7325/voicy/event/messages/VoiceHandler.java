package com.root7325.voicy.event.messages;

import com.google.inject.Inject;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.model.request.ParseMode;
import com.root7325.voicy.config.TgConfig;
import com.root7325.voicy.event.BaseEventListener;
import com.root7325.voicy.helper.IMessageHelper;
import com.root7325.voicy.service.*;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.*;

/**
 * @author root7325 on 13.06.2025
 */
@Slf4j
@AllArgsConstructor(onConstructor = @__({@Inject}))
public class VoiceHandler extends BaseEventListener {
    private final IMessageHelper messageHelper;
    private final TgConfig tgConfig;
    private final TranslationService translationService;
    private final VoiceService voiceService;
    private final VoskService voskService;
    private final SpeechProcessingService speechProcessingService;

    @Override
    public void onUpdateReceived(Update update) {
        long chatId = update.message().chat().id();
        int messageId = update.message().messageId();

        log.debug("Starting processing voice in {}", chatId);
        CompletableFuture<byte[]> voiceCompletableFuture = voiceService.processVoiceMessage(chatId, messageId, update.message().voice());
        voiceCompletableFuture.thenAccept(voiceData -> handleVoiceData(voiceData, chatId, messageId))
                .exceptionally(ex -> {
                    log.error("Error completing tasks.", ex);
                    String message = translationService.getMessage(tgConfig.getLanguage(), "error.server_error") + ex.getMessage();
                    messageHelper.sendSimpleMessage(chatId, messageId, message);

                    return null;
                });
    }

    private void handleVoiceData(byte[] voiceData, long chatId, int messageId) {
        if (voiceData == null) {
            return;
        }

        String message = translationService.getMessage(tgConfig.getLanguage(), "voice.recognized");
        String recognized = voskService.recognizeSpeech(voiceData);
        messageHelper.sendSimpleMessage(chatId, messageId, String.format(message, recognized));

        speechProcessingService.processRecognizedSpeech(chatId, messageId, recognized);
    }

    @Override
    public boolean filter(Update update) {
        return super.filter(update)
                && update.message() != null
                && update.message().voice() != null;
    }
}
