package com.root7325.voicy.events;

import com.pengrad.telegrambot.model.Update;
import com.root7325.voicy.services.TranslationService;
import lombok.Setter;

/**
 * @author root7325 on 02.03.2025
 * <p>
 * Abstract base class for event listeners
 * Provides common services and basic filtering functionality
 */
@Setter
public abstract class BaseEventListener implements IEventListener {
    protected TranslationService translationService;

    /** Basic filter which checks if update is not null */
    @Override
    public boolean filter(Update update) {
        return update != null;
    }
}
