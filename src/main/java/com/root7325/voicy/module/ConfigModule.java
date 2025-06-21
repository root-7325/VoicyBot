package com.root7325.voicy.module;

import com.google.inject.AbstractModule;
import com.google.inject.Provider;
import com.google.inject.Singleton;
import com.root7325.voicy.config.Config;
import org.yaml.snakeyaml.Yaml;

import java.io.IOException;
import java.io.InputStream;

/**
 * @author root7325 on 21.06.2025
 */
public class ConfigModule extends AbstractModule {
    @Override
    protected void configure() {
        bind(Config.class).toProvider(ConfigProvider.class).in(Singleton.class);
    }
}

class ConfigProvider implements Provider<Config> {
    private static final String YAML_FILE = "config.yaml";

    @Override
    public Config get() {
        Yaml yaml = new Yaml();

        try (InputStream inputStream = Config.class.getClassLoader()
                .getResourceAsStream(YAML_FILE)) {
            return yaml.loadAs(inputStream, Config.class);
        } catch (Exception ex) {
            throw new RuntimeException("Failed to load yaml configuration", ex);
        }
    }
}