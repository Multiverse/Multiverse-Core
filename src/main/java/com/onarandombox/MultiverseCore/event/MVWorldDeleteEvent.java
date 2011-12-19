package com.onarandombox.MultiverseCore.event;

import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;

import com.onarandombox.MultiverseCore.api.MultiverseWorld;

/**
 * Called when a world is about to be deleted by Multiverse.
 */
public class MVWorldDeleteEvent extends Event implements Cancellable {
    private boolean cancelled = false;

    private final MultiverseWorld world;
    private final boolean removeFromConfig;

    public MVWorldDeleteEvent(MultiverseWorld world, boolean removeFromConfig) {
        super("MVWorldDeleteEvent");

        if (world == null)
            throw new IllegalArgumentException("world can't be null!");

        this.world = world;
        this.removeFromConfig = removeFromConfig;
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
    public MultiverseWorld getWorld() {
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
