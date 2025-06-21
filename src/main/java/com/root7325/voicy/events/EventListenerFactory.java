package com.root7325.voicy.events;

import com.google.inject.Inject;
import com.google.inject.Injector;
import com.root7325.voicy.services.LLMService;
import com.root7325.voicy.services.TranslationService;
import com.root7325.voicy.services.VoiceService;
import com.root7325.voicy.services.VoskService;
import lombok.AllArgsConstructor;

/**
 * @author root7325 on 02.03.2025
 * <p>
 * Factory class for creating {@link BaseEventListener} instances.
 */
@AllArgsConstructor(onConstructor = @__({@Inject}))
public class EventListenerFactory {
    private final Injector injector;

    public <T extends IEventListener> T createEventListener(Class<T> listenerClass) {
        return injector.getInstance(listenerClass);
    }
}
