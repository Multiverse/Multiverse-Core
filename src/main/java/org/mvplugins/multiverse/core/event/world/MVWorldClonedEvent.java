package org.mvplugins.multiverse.core.event.world;

import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.mvplugins.multiverse.core.world.LoadedMultiverseWorld;
import org.mvplugins.multiverse.core.world.MultiverseWorld;

/**
 * Called when a world has been successfully cloned.
 */
public final class MVWorldClonedEvent extends MultiverseWorldEvent<LoadedMultiverseWorld> {
    private static final HandlerList HANDLERS = new HandlerList();

    private final MultiverseWorld fromWorld;

    public MVWorldClonedEvent(LoadedMultiverseWorld world, MultiverseWorld fromWorld) {
        super(world);
        this.fromWorld = fromWorld;
    }

    /**
     * @deprecated Cloning can be done from unloaded worlds as well. Use {@link #getSourceWorld()} instead.
     */
    @Deprecated(forRemoval = true, since = "5.6")
    @ApiStatus.ScheduledForRemoval(inVersion = "6.0")
    public @Nullable LoadedMultiverseWorld getFromWorld() {
        return fromWorld.asLoadedWorld().getOrNull();
    }

    @ApiStatus.AvailableSince("5.6")
    public @NotNull MultiverseWorld getSourceWorld() {
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
