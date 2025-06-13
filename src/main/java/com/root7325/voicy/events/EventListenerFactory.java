package com.root7325.voicy.events;

import com.root7325.voicy.services.TranslationService;
import lombok.AllArgsConstructor;

/**
 * @author root7325 on 02.03.2025
 * <p>
 * Factory class for creating {@link BaseEventListener} instances.
 */
@AllArgsConstructor
public class EventListenerFactory {
    private final TranslationService translationService;

    public <T extends BaseEventListener> T createEventListener(Class<T> listenerClass) {
        try {
            T listener = listenerClass.getDeclaredConstructor().newInstance();

            listener.setTranslationService(translationService);

            return listener;
        } catch (Exception ex) {
            throw new UnsupportedOperationException("Failed to create instance of event listener.", ex);
        }
    }
}
