package com.root7325.voicy;

import com.pengrad.telegrambot.TelegramBot;
import com.root7325.voicy.events.EventListenerFactory;
import com.root7325.voicy.events.commands.StartHandler;
import com.root7325.voicy.events.messages.VoiceHandler;
import com.root7325.voicy.helpers.MessageHelper;
import com.root7325.voicy.services.BotService;
import com.root7325.voicy.services.TranslationService;
import com.root7325.voicy.utils.Config;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * @author root7325 on 13.06.2025
 */
@Slf4j
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class Main {
    public static void main(String[] args) {
        log.info("VoicyBot is starting.");

        Config config = Config.getInstance();
        String token = config.getTgConfig().getToken();

        TelegramBot telegramBot = new TelegramBot(token);
        TranslationService translationService = new TranslationService();
        EventListenerFactory eventListenerFactory = new EventListenerFactory(translationService);

        BotService botService = new BotService(telegramBot, translationService, eventListenerFactory);
        botService.registerListener(StartHandler.class);
        botService.registerListener(VoiceHandler.class);
        MessageHelper.init(telegramBot);

        botService.startListening();
    }
}