package org.mvplugins.multiverse.core.world;

import io.vavr.control.Option;
import org.bukkit.NamespacedKey;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

/**
 * A reference to a world by its key that can be stored and used to retrieve the current world instance at runtime.
 * <p>
 * Stored {@link MultiverseWorld} and {@link LoadedMultiverseWorld} instances become stale when a world is reloaded
 * or regenerated. This reference looks up the world on each call to {@link #get()} or {@link #getLoaded()}, allowing
 * callers to keep the reference across those operations and retrieve the current instance when needed.
 * Store this reference rather than caching the world instances returned by its methods.
 * <p>
 * Obtain a reference using {@link MultiverseWorld#asRef()}. Resolving a reference does not load the world.
 *
 * @since 5.9
 */
@ApiStatus.AvailableSince("5.9")
public class MultiverseWorldRef {

    private final NamespacedKey worldKey;
    private final WorldStore worldStore;

    MultiverseWorldRef(@NotNull NamespacedKey worldKey, @NotNull WorldStore worldStore) {
        this.worldKey = worldKey;
        this.worldStore = worldStore;
    }

    /**
     * Gets the current world instance for this reference's key, whether loaded or unloaded.
     * The world is looked up on each call so the returned instance reflects reloads and regeneration.
     *
     * @return The current world instance wrapped in an Option, or None if the world is no longer managed by Multiverse.
     *
     * @since 5.9
     */
    @ApiStatus.AvailableSince("5.9")
    public Option<MultiverseWorld> get() {
        return worldStore.getWorld(worldKey);
    }

    /**
     * Gets the current loaded world instance for this reference's key without loading the world.
     * The world is looked up on each call so the returned instance reflects reloads and regeneration.
     *
     * @return The current loaded world instance wrapped in an Option, or None if the world is unloaded or no longer
     *         managed by Multiverse.
     *
     * @since 5.9
     */
    @ApiStatus.AvailableSince("5.9")
    public Option<LoadedMultiverseWorld> getLoaded() {
        return worldStore.getLoadedWorld(worldKey);
    }
}
