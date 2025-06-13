package com.root7325.voicy.helpers;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.model.request.InlineKeyboardMarkup;
import com.pengrad.telegrambot.model.request.ParseMode;
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

    private static SendMessage createMessage(long chatId, String text) {
        return new SendMessage(chatId, text)
                .parseMode(ParseMode.HTML);
    }

    public static void sendSimpleMessage(long chatId, String text) {
        SendMessage message = createMessage(chatId, text);
        sendMessage(message);
    }

    public static void sendMessage(long chatId, String text, InlineKeyboardMarkup inlineKeyboardMarkup) {
        SendMessage message = createMessage(chatId, text);
        message.replyMarkup(inlineKeyboardMarkup);

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
