package com.root7325.voicy;

import com.pengrad.telegrambot.TelegramBot;
import com.root7325.voicy.events.EventListenerFactory;
import com.root7325.voicy.events.commands.AskHandler;
import com.root7325.voicy.events.commands.StartHandler;
import com.root7325.voicy.events.messages.VoiceHandler;
import com.root7325.voicy.helpers.MessageHelper;
import com.root7325.voicy.services.*;
import com.root7325.voicy.utils.Config;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.vosk.LibVosk;
import org.vosk.LogLevel;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @author root7325 on 13.06.2025
 */
@Slf4j
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class Main {
    public static void main(String[] args) {
        log.info("VoicyBot is starting.");

        LibVosk.setLogLevel(LogLevel.WARNINGS);

        Config config = Config.getInstance();
        String token = config.getTgConfig().getToken();

        ExecutorService executorService = Executors.newFixedThreadPool(4);

        TelegramBot telegramBot = new TelegramBot(token);
        BotService botService = getBotService(executorService, telegramBot);
        botService.registerListener(StartHandler.class);
        botService.registerListener(AskHandler.class);
        botService.registerListener(VoiceHandler.class);
        MessageHelper.init(telegramBot);

        botService.startListening();
    }

    @NotNull
    private static BotService getBotService(ExecutorService executorService, TelegramBot telegramBot) {
        TranslationService translationService = new TranslationService();
        VoiceService voiceService = new VoiceService(translationService, executorService);
        LLMService llmService = new LLMService(executorService);
        VoskService voskService = new VoskService();
        EventListenerFactory eventListenerFactory = new EventListenerFactory(translationService, voiceService, llmService, voskService);

        return new BotService(telegramBot, translationService, eventListenerFactory);
    }
}