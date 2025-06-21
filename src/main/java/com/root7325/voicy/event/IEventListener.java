package com.root7325.voicy.event;

import com.pengrad.telegrambot.model.Update;

/**
 * @author root7325 on 02.03.2025
 * <p>
 * Interface for handling Telegram bot events
 */
public interface IEventListener {
    /**
     * Processes received update
     *
     * @param update filtered {@link Update}
     */
    void onUpdateReceived(Update update);

    /**
     * Checks if the update should be processed by this listener
     *
     * @param update incoming {@link Update}
     * @return true if should, false otherwise
     */
    boolean filter(Update update);

    default String getName() {
        return this.getClass().getSimpleName();
    }
}