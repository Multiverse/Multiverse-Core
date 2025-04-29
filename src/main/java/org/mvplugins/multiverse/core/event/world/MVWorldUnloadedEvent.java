package org.mvplugins.multiverse.core.event.world;

import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;
import org.mvplugins.multiverse.core.world.MultiverseWorld;

/**
 * Called when a world has been successfully unloaded.
 */
public final class MVWorldUnloadedEvent extends MultiverseWorldEvent<MultiverseWorld> {
    private static final HandlerList HANDLERS = new HandlerList();

    public MVWorldUnloadedEvent(MultiverseWorld world) {
        super(world);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public @NotNull HandlerList getHandlers() {
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
}
