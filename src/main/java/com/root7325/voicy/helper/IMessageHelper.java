package com.root7325.voicy.helper;

import com.pengrad.telegrambot.model.request.ParseMode;
import com.pengrad.telegrambot.request.SendMessage;
import com.pengrad.telegrambot.response.SendResponse;

/**
 * @author root7325 on 09.07.2025
 */
public interface IMessageHelper {
    SendResponse sendMessage(SendMessage message);
    SendResponse sendSimpleMessage(long chatId, String text);
    SendResponse sendSimpleMessage(long chatId, String text, ParseMode parseMode);
    SendResponse sendSimpleMessage(long chatId, int replyToMessageId, String text);
    SendResponse sendSimpleMessage(long chatId, int replyToMessageId, String text, ParseMode parseMode);
    void deleteMessage(long chatId, int messageId);
    byte[] downloadFile(String fileId);
}
