package org.mvplugins.multiverse.core.event;

import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import org.mvplugins.multiverse.core.world.LoadedMultiverseWorld;
import org.mvplugins.multiverse.core.world.MultiverseWorld;

/**
 * Called when a world is about to be deleted by Multiverse.
 */
public class MVWorldDeleteEvent extends Event implements Cancellable {
    private boolean cancelled = false;

    private final LoadedMultiverseWorld world;
    private final boolean removeFromConfig;

    public MVWorldDeleteEvent(LoadedMultiverseWorld world, boolean removeFromConfig) {
        if (world == null) {
            throw new IllegalArgumentException("world can't be null!");
        }
        this.world = world;
        this.removeFromConfig = removeFromConfig;
    }

    private static final HandlerList HANDLERS = new HandlerList();

    /**
     * {@inheritDoc}
     */
    @Override
    public HandlerList getHandlers() {
        return HANDLERS;
    }

    /**
     * Gets the handler list. This is required by the event system.
     * @return A list of HANDLERS.
     */
    public static HandlerList getHandlerList() {
        return HANDLERS;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isCancelled() {
        return cancelled;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setCancelled(boolean cancel) {
        cancelled = cancel;
    }

    /**
     * Gets the world that's about to be deleted.
     *
     * @return That {@link MultiverseWorld}.
     */
    public LoadedMultiverseWorld getWorld() {
        return world;
    }

    /**
     * Is the world about to be removed from the config?
     *
     * @return True if yes, false if no.
     */
    public boolean removeWorldFromConfig() {
        return removeFromConfig;
    }

}
