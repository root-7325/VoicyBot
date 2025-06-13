package com.root7325.voicy.services;

import lombok.extern.slf4j.Slf4j;

import java.util.*;

/**
 * @author root7325 on 10.02.2025
 * <p>
 * Service for handling i18n functionality.
 */
@Slf4j
public class TranslationService {
    /** Base path for message resource bundles */
    private static final String I18N_PREFIX = "locales/messages";

    /** List of locales supported by this application */
    public static final Locale[] SUPPORTED_LOCALES = {new Locale("en"), new Locale("ru")};

    /** Map storing ResourceBundles for each supported locale */
    private final Map<Locale, ResourceBundle> translationsMap;

    public TranslationService() {
        this.translationsMap = new HashMap<>();
        this.registerResourceBundle(SUPPORTED_LOCALES);
    }

    /**
     * Returns translated message based on input
     *
     * @param locale A required {@link Locale}. If {@link ResourceBundle} is not associated
     *               with it, then fallback one will be returned.
     * @param key    Translation key.
     * @return Translated message.
     */
    public String getMessage(Locale locale, String key) {
        // if locale is unsupported, then return fallback one.
        if (!translationsMap.containsKey(locale)) {
            log.trace("{} locale is unsupported, switched to fallback.", locale);
            return getMessage(key);
        }

        return translationsMap.get(locale).getString(key);
    }

    /**
     * Returns translated message based on input.
     *
     * @param languageCode IETF language code
     * @param key          Translation key
     * @return Translated message.
     */
    public String getMessage(String languageCode, String key) {
        return getMessage(Locale.forLanguageTag(languageCode), key);
    }

    /**
     * Returns translated message based on input.
     * Fallback locale is {@link Locale#US}.
     *
     * @param key Translation key.
     * @return Translated message.
     */
    public String getMessage(String key) {
        if (!translationsMap.containsKey(Locale.US)) {
            throw new IllegalStateException("Fallback locale is not registered!");
        }

        return getMessage(Locale.US, key);
    }

    /**
     * Registers {@link ResourceBundle} for given {@link Locale}.
     *
     * @param locale Required locale.
     * @throws IllegalStateException    If given locale is already registered.
     * @throws MissingResourceException If given locale doesn't have associated ResourceBundle.
     */
    public void registerResourceBundle(Locale locale) {
        if (isLocaleRegistered(locale))
            throw new IllegalStateException("ResourceBundle for " + locale + " is already registered!");

        try {
            ResourceBundle bundle = ResourceBundle.getBundle(I18N_PREFIX, locale);
            translationsMap.put(locale, bundle);
            log.trace("Registered {} locale.", locale);
        } catch (MissingResourceException exception) {
            log.error("ResourceBundle for {} locale is not present!", locale);
        }
    }

    /**
     * Registers multiple locales at once.
     *
     * @param locales Array of locales to register
     */
    public void registerResourceBundle(Locale... locales) {
        for (Locale locale : locales) registerResourceBundle(locale);
    }

    /**
     * Returns whether Locale registered or not.
     *
     * @param locale {@link Locale} object.
     * @return @code true if locale is registered, @code false otherwise.
     */
    private boolean isLocaleRegistered(Locale locale) {
        return translationsMap.containsKey(locale);
    }
}
