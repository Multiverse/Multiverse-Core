package org.mvplugins.multiverse.core.dynamiclistener;

import org.bukkit.event.Event;
import org.bukkit.event.Listener;

/**
 * Runnable of an event that may not be available on the server. This is to prevent class not found errors when
 * initializing the listener class.
 *
 * @param <T> The type of event to listen to.
 */
@FunctionalInterface
public interface EventRunnable<T extends Event> {
    void onEvent(T event);
}
