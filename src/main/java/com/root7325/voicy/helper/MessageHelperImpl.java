package com.root7325.voicy.helper;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.model.request.ParseMode;
import com.pengrad.telegrambot.model.request.ReplyParameters;
import com.pengrad.telegrambot.request.DeleteMessage;
import com.pengrad.telegrambot.request.GetFile;
import com.pengrad.telegrambot.request.SendMessage;
import com.pengrad.telegrambot.response.GetFileResponse;
import com.pengrad.telegrambot.response.SendResponse;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * @author root7325 on 10.02.2025
 * <p>
 * Helper for messages responsing.
 */
@Slf4j
@Singleton
@AllArgsConstructor(onConstructor = @__({@Inject}))
public class MessageHelperImpl implements IMessageHelper {
    private final TelegramBot telegramBot;

    public SendResponse sendMessage(SendMessage message) {
        return telegramBot.execute(message);
    }

    public SendResponse sendSimpleMessage(long chatId, String text) {
        return sendSimpleMessage(chatId, text, ParseMode.HTML);
    }

    public SendResponse sendSimpleMessage(long chatId, String text, ParseMode parseMode) {
        return sendSimpleMessage(chatId, -1, text, parseMode);
    }

    public SendResponse sendSimpleMessage(long chatId, int replyToMessageId, String text) {
        return sendSimpleMessage(chatId, replyToMessageId, text, ParseMode.HTML);
    }

    public SendResponse sendSimpleMessage(long chatId, int replyToMessageId, String text, ParseMode parseMode) {
        SendMessage message = new SendMessage(chatId, text)
                .parseMode(parseMode);

        if (replyToMessageId != -1) {
            message.replyParameters(new ReplyParameters(replyToMessageId));
        }

        return sendMessage(message);
    }

    public void deleteMessage(long chatId, int messageId) {
        DeleteMessage deleteMessage = new DeleteMessage(chatId, messageId);
        telegramBot.execute(deleteMessage);
    }

    public byte[] downloadFile(String fileId) {
        try {
            GetFile getFileRequest = new GetFile(fileId);
            GetFileResponse getFileResponse = telegramBot.execute(getFileRequest);

            if (!getFileResponse.isOk()) {
                log.error("Failed to get file path: {}", getFileResponse.description());
                return null;
            }

            String fileUrl = telegramBot.getFullFilePath(getFileResponse.file());
            log.info("Downloading file: {}", fileId);

            try (java.io.InputStream in = new java.net.URL(fileUrl).openStream()) {
                return in.readAllBytes();
            }
        } catch (Exception e) {
            log.error("Failed to download file ({})!", fileId, e);
            return null;
        }
    }
}
