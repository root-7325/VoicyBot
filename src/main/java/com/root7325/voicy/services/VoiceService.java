package com.root7325.voicy.services;

import com.pengrad.telegrambot.model.Voice;
import com.root7325.voicy.helpers.MessageHelper;
import com.root7325.voicy.utils.AudioConverter;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;

/**
 * @author root7325 on 13.06.2025
 */
@Slf4j
@AllArgsConstructor
public class VoiceService {
    private static final int VOICE_MINIMAL_LENGTH = 1;

    private final TranslationService translationService;
    private final ExecutorService executorService;

    public CompletableFuture<byte[]> processVoiceMessage(long chatId, int messageId, String languageCode, Voice voice) {
        if (voice.duration() < VOICE_MINIMAL_LENGTH) {
            String message = translationService.getMessage(languageCode, "error.short_voice");
            MessageHelper.sendSimpleMessage(chatId, messageId, message);
            return CompletableFuture.completedFuture(null);
        }

        return CompletableFuture.supplyAsync(
                () -> downloadVoiceAndConvert(chatId, messageId, languageCode, voice.fileId()),
                executorService
        );
    }

    private byte[] downloadVoiceAndConvert(long chatId, int messageId, String languageCode, String fileId) {
        byte[] oggData = MessageHelper.downloadFile(fileId);
        if (oggData == null || oggData.length == 0) {
            String message = translationService.getMessage(languageCode,"error.server_error") + "empty voice file";
            MessageHelper.sendSimpleMessage(chatId, messageId, message);
            return null;
        }

        try {
            return AudioConverter.convertOggToWav(  oggData);
        } catch (Exception ex) {
            log.error("Failed to convert ogg to wav!", ex);
            String message = translationService.getMessage(languageCode, "error.server_error") + ex.getMessage();
            MessageHelper.sendSimpleMessage(chatId, messageId, message);
            return null;
        }
    }
}
