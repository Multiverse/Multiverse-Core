package org.mvplugins.multiverse.core.event;

import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import org.jetbrains.annotations.NotNull;
import org.mvplugins.multiverse.core.world.LoadedMultiverseWorld;

/**
 * Called when a world is about to be deleted by Multiverse.
 */
public final class MVWorldDeleteEvent extends Event implements Cancellable {
    private boolean cancelled = false;

    private final LoadedMultiverseWorld world;

    public MVWorldDeleteEvent(@NotNull LoadedMultiverseWorld world) {
        this.world = world;
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
     *
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
     * @return That {@link LoadedMultiverseWorld}.
     */
    public LoadedMultiverseWorld getWorld() {
        return world;
    }
}
