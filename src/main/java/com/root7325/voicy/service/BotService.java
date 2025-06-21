package com.root7325.voicy.service;

import com.google.inject.Inject;
import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.UpdatesListener;
import com.pengrad.telegrambot.model.Update;
import com.root7325.voicy.event.BaseEventListener;
import com.root7325.voicy.event.EventListenerFactory;
import com.root7325.voicy.event.IEventListener;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutorService;

/**
 * @author root7325 on 02.03.2025
 * <p>
 * Core service of this bot.
 * It manages all underlying services, handles EventListeners processing.
 */
@Slf4j
@Getter
public class BotService {
    private final TelegramBot telegramBot;
    private final EventListenerFactory eventListenerFactory;
    private final ExecutorService executorService;

    /** List of registered event listeners */
    private final List<BaseEventListener> eventListeners;

    @Inject
    public BotService(TelegramBot telegramBot, EventListenerFactory eventListenerFactory,
                      ExecutorService executorService) {
        this.telegramBot = telegramBot;
        this.eventListenerFactory = eventListenerFactory;
        this.executorService = executorService;

        this.eventListeners = new CopyOnWriteArrayList<>();
    }

    /**
     * Starts listening for Telegram {@link Update}s.
     * Processes updates through registered listeners.
     */
    public void startListening() {
        telegramBot.setUpdatesListener(updates -> {
            for (Update update : updates) {
                notifyListeners(update);
            }
            return UpdatesListener.CONFIRMED_UPDATES_ALL;
        });
    }

    /**
     * Registers given {@link BaseEventListener}.
     *
     * @param listenerClass Class of listener to register.
     * @throws IllegalStateException If listener is already registered.
     */
    public void registerListener(Class<? extends BaseEventListener> listenerClass) {
        BaseEventListener eventListener = eventListenerFactory.createEventListener(listenerClass);

        if (isListenerRegistered(eventListener)) {
            throw new IllegalStateException(eventListener.getName() + " is already registered!");
        }

        eventListeners.add(eventListener);
        log.trace("Registered {} event listener.", eventListener.getName());
    }

    /**
     * Returns whether listener registered or not.
     *
     * @param eventListener {@link BaseEventListener} object.
     * @return true if listener is registered, false otherwise.
     */
    public boolean isListenerRegistered(BaseEventListener eventListener) {
        return eventListeners.contains(eventListener);
    }

    /**
     * Notifies all registered {@link IEventListener}.
     * @param update {@link Update} object.
     */
    private void notifyListeners(Update update) {
        for (IEventListener eventListener : eventListeners) {
            if (eventListener.filter(update)) {
                log.trace("Update received by {} event listener.", eventListener.getName());
                executorService.execute(() -> eventListener.onUpdateReceived(update));
            }
        }
    }
}
