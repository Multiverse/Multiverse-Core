package org.mvplugins.multiverse.core.dynamiclistener;

import org.bukkit.event.Event;
import org.bukkit.event.Listener;
import org.bukkit.plugin.EventExecutor;
import org.jetbrains.annotations.NotNull;

final class EventRunnableExecutor<T extends Event> implements EventExecutor {

    private final Class<T> eventClass;
    private final EventRunnable<T> runnable;

    EventRunnableExecutor(Class<T> eventClass, EventRunnable<T> runnable) {
        this.eventClass = eventClass;
        this.runnable = runnable;
    }

    @Override
    public void execute(@NotNull Listener listener, @NotNull Event event) {
        if (!eventClass.isInstance(event)) {
            return;
        }
        runnable.onEvent((T) event);
    }
}
