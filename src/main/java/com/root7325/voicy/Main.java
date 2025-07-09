package com.root7325.voicy;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.root7325.voicy.event.commands.AskHandler;
import com.root7325.voicy.event.commands.StartHandler;
import com.root7325.voicy.event.messages.VoiceHandler;
import com.root7325.voicy.module.AppModule;
import com.root7325.voicy.service.*;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.vosk.LibVosk;
import org.vosk.LogLevel;

/**
 * @author root7325 on 13.06.2025
 */
@Slf4j
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class Main {
    public static void main(String[] args) {
        log.info("VoicyBot is starting.");

        LibVosk.setLogLevel(LogLevel.WARNINGS);

        Injector injector = Guice.createInjector(new AppModule());

        BotService botService = injector.getInstance(BotService.class);
        botService.registerListener(StartHandler.class);
        botService.registerListener(AskHandler.class);
        botService.registerListener(VoiceHandler.class);

        botService.startListening();
    }
}