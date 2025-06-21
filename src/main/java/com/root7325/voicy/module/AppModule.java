package com.root7325.voicy.module;

import com.google.inject.AbstractModule;

/**
 * @author root7325 on 21.06.2025
 */
public class AppModule extends AbstractModule {
    @Override
    protected void configure() {
        install(new ConfigModule());
        install(new BotModule());
    }
}
