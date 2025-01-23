package org.mvplugins.multiverse.core.event;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

/**
 * Called when Core's debug level is changed.
 */
public final class MVDebugModeEvent extends Event {

    private static final HandlerList HANDLERS = new HandlerList();

    private final int level;

    public MVDebugModeEvent(int level) {
        this.level = level;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public HandlerList getHandlers() {
        return HANDLERS;
    }

    /**
     * Gets the handler list. This is required by the event system.
     *
     * @return A list of HANDLERS.
     */
    public static HandlerList getHandlerList() {
        return HANDLERS;
    }

    /**
     * Returns the current debug level of Core.
     *
     * @return the current debug level of Core.
     */
    public int getLevel() {
        return level;
    }
}
