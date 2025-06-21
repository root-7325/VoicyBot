package com.root7325.voicy.event;

import com.google.inject.Inject;
import com.google.inject.Injector;
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
