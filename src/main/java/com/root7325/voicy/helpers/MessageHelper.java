package com.root7325.voicy.helpers;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.model.request.InlineKeyboardMarkup;
import com.pengrad.telegrambot.model.request.ParseMode;
import com.pengrad.telegrambot.model.request.ReplyParameters;
import com.pengrad.telegrambot.request.GetFile;
import com.pengrad.telegrambot.request.SendMessage;
import com.pengrad.telegrambot.response.GetFileResponse;
import com.pengrad.telegrambot.response.SendResponse;
import lombok.extern.slf4j.Slf4j;

/**
 * @author root7325 on 10.02.2025
 * <p>
 * Helper for messages responsing.
 */
@Slf4j
public class MessageHelper {
    private static TelegramBot telegramBot;

    public static void init(TelegramBot bot) {
        telegramBot = bot;
    }

    public static SendResponse sendMessage(SendMessage message) {
        try {
            return telegramBot.execute(message);
        } catch (Exception e) {
            log.error("Failed to send message!", e);
            return null;
        }
    }

    private static SendMessage createMessage(long chatId, String text, ParseMode parseMode) {
        return new SendMessage(chatId, text)
                .parseMode(parseMode);
    }

    public static void sendSimpleMessage(long chatId, String text) {
        sendSimpleMessage(chatId, text, ParseMode.HTML);
    }

    public static void sendSimpleMessage(long chatId, String text, ParseMode parseMode) {
        sendSimpleMessage(chatId, -1, text, parseMode);
    }

    public static void sendSimpleMessage(long chatId, int replyToMessageId, String text) {
        sendSimpleMessage(chatId, replyToMessageId, text, ParseMode.HTML);
    }

    public static void sendSimpleMessage(long chatId, int replyToMessageId, String text, ParseMode parseMode) {
        SendMessage message = new SendMessage(chatId, text)
                .parseMode(parseMode);

        if (replyToMessageId != -1) {
            message.replyParameters(new ReplyParameters(replyToMessageId));
        }

        sendMessage(message);
    }

    public static byte[] downloadFile(String fileId) {
        try {
            GetFile getFileRequest = new GetFile(fileId);
            GetFileResponse getFileResponse = telegramBot.execute(getFileRequest);

            if (!getFileResponse.isOk()) {
                log.error("Failed to get file path: {}", getFileResponse.description());
                return null;
            }

            String fileUrl = telegramBot.getFullFilePath(getFileResponse.file());
            log.info("Downloading file from: {}", fileUrl);

            try (java.io.InputStream in = new java.net.URL(fileUrl).openStream()) {
                return in.readAllBytes();
            }
        } catch (Exception e) {
            log.error("Failed to download file!", e);
            return null;
        }
    }
}
