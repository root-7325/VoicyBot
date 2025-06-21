package com.root7325.voicy.module;

import com.google.inject.AbstractModule;
import com.google.inject.Provider;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import com.pengrad.telegrambot.TelegramBot;
import com.root7325.voicy.config.Config;
import com.root7325.voicy.services.LLMService;
import com.root7325.voicy.services.TranslationService;
import com.root7325.voicy.services.VoiceService;
import com.root7325.voicy.services.VoskService;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @author root7325 on 21.06.2025
 */
public class BotModule extends AbstractModule {
    @Override
    protected void configure() {
        bind(TranslationService.class).in(Singleton.class);
        bind(VoskService.class).in(Singleton.class);
        bind(LLMService.class).in(Singleton.class);
        bind(VoiceService.class).in(Singleton.class);

        bind(ExecutorService.class)
                .toProvider(ExecutorServiceProvider.class)
                .in(Singleton.class);
    }

    @Provides @Singleton
    public TelegramBot provideTelegramBot(Config config) {
        return new TelegramBot(config.getTgConfig().getToken());
    }
}

class ExecutorServiceProvider implements Provider<ExecutorService> {
    @Override
    public ExecutorService get() {
        return Executors.newFixedThreadPool(4);
    }
}
