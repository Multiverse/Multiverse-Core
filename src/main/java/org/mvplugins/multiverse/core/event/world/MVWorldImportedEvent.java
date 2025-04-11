package org.mvplugins.multiverse.core.event.world;

import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;
import org.mvplugins.multiverse.core.world.LoadedMultiverseWorld;

/**
 * Called when a world has been successfully imported.
 */
public final class MVWorldImportedEvent extends MultiverseWorldEvent<LoadedMultiverseWorld> {
    private static final HandlerList HANDLERS = new HandlerList();

    public MVWorldImportedEvent(LoadedMultiverseWorld world) {
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
