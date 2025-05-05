package org.mvplugins.multiverse.core.dynamiclistener;

import com.dumptruckman.minecraft.util.Logging;
import io.vavr.control.Option;
import jakarta.inject.Inject;
import org.bukkit.event.EventPriority;
import org.jetbrains.annotations.NotNull;
import org.jvnet.hk2.annotations.Service;
import org.mvplugins.multiverse.core.dynamiclistener.annotations.EventPriorityKey;

import java.util.HashMap;
import java.util.Map;

/**
 * Allow for event priority to be set dynamically with key mapping instead of just fixed in EventHandler annotation.
 */
@Service
public final class EventPriorityMapper {

    private final Map<String, EventPriority> eventPriorityMap;

    @Inject
    EventPriorityMapper() {
        this.eventPriorityMap = new HashMap<>();
    }

    /**
     * Sets the event priority for the given key.
     *
     * @param key       The key referenced in {@link EventPriorityKey}
     * @param priority  The priority to set
     */
    public void setPriority(@NotNull String key, @NotNull EventPriority priority) {
        Logging.finest("Setting event priority for %s to %s", key, priority);
        eventPriorityMap.put(key, priority);
    }

    /**
     * Gets the event priority for the given key
     *
     * @param key The key referenced in {@link EventPriorityKey}
     * @return The event priority
     */
    public Option<EventPriority> getPriority(String key) {
        return Option.of(eventPriorityMap.get(key));
    }
}
