package org.mvplugins.multiverse.core.event.world;

import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;
import org.mvplugins.multiverse.core.world.LoadedMultiverseWorld;

/**
 * Called when a world has been successfully cloned.
 */
public final class MVWorldClonedEvent extends MultiverseWorldEvent<LoadedMultiverseWorld> {
    private static final HandlerList HANDLERS = new HandlerList();

    private final LoadedMultiverseWorld fromWorld;

    public MVWorldClonedEvent(LoadedMultiverseWorld world, LoadedMultiverseWorld fromWorld) {
        super(world);
        this.fromWorld = fromWorld;
    }

    public LoadedMultiverseWorld getFromWorld() {
        return fromWorld;
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
