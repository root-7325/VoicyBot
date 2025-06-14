package com.root7325.voicy.events;

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
@AllArgsConstructor
public class EventListenerFactory {
    private final TranslationService translationService;
    private final VoiceService voiceService;
    private final LLMService llmService;
    private final VoskService voskService;
    // todo: ^ is bad

    public <T extends BaseEventListener> T createEventListener(Class<T> listenerClass) {
        try {
            T listener = listenerClass.getDeclaredConstructor().newInstance();

            listener.setTranslationService(translationService);
            listener.setVoiceService(voiceService);
            listener.setLlmService(llmService);
            listener.setVoskService(voskService);

            return listener;
        } catch (Exception ex) {
            throw new UnsupportedOperationException("Failed to create instance of event listener.", ex);
        }
    }
}
