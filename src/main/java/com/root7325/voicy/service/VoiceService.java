package com.root7325.voicy.service;

import com.google.inject.Inject;
import com.pengrad.telegrambot.model.Voice;
import com.root7325.voicy.config.TgConfig;
import com.root7325.voicy.helper.IMessageHelper;
import com.root7325.voicy.util.AudioConverter;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;

/**
 * @author root7325 on 13.06.2025
 */
@Slf4j
@AllArgsConstructor(onConstructor = @__({@Inject}))
public class VoiceService {
    private static final int VOICE_MINIMAL_LENGTH = 3;

    private final TgConfig tgConfig;
    private final IMessageHelper messageHelper;
    private final TranslationService translationService;
    private final ExecutorService executorService;

    public CompletableFuture<byte[]> processVoiceMessage(long chatId, int messageId, Voice voice) {
        if (voice.duration() < VOICE_MINIMAL_LENGTH) {
            String message = translationService.getMessage(tgConfig.getLanguage(), "error.short_voice");
            messageHelper.sendSimpleMessage(chatId, messageId, message);
            return CompletableFuture.completedFuture(null);
        }

        return CompletableFuture.supplyAsync(
                () -> downloadVoiceAndConvert(chatId, messageId, voice.fileId()),
                executorService
        );
    }

    private byte[] downloadVoiceAndConvert(long chatId, int messageId, String fileId) {
        byte[] oggData = messageHelper.downloadFile(fileId);
        if (oggData == null || oggData.length == 0) {
            String message = translationService.getMessage(tgConfig.getLanguage(),"error.server_error") + "empty voice file";
            messageHelper.sendSimpleMessage(chatId, messageId, message);
            return null;
        }

        try {
            return AudioConverter.convertOggToWav(  oggData);
        } catch (Exception ex) {
            log.error("Failed to convert ogg to wav!", ex);
            String message = translationService.getMessage(tgConfig.getLanguage(), "error.server_error") + ex.getMessage();
            messageHelper.sendSimpleMessage(chatId, messageId, message);
            return null;
        }
    }
}
