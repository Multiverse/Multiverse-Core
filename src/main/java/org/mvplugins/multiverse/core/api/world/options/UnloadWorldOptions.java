package org.mvplugins.multiverse.core.api.world.options;

import org.mvplugins.multiverse.core.api.world.LoadedMultiverseWorld;

/**
 * Options for customizing the unloading of a world.
 *
 * @since 5.0
 */
public class UnloadWorldOptions {

    /**
     * Creates a new {@link UnloadWorldOptions} instance with the given world.
     *
     * @param world The world to unload.
     * @return A new {@link UnloadWorldOptions} instance.
     * @since 5.0
     */
    public static UnloadWorldOptions world(LoadedMultiverseWorld world) {
        return new UnloadWorldOptions(world);
    }

    private final LoadedMultiverseWorld world;
    private boolean saveBukkitWorld = true;

    UnloadWorldOptions(LoadedMultiverseWorld world) {
        this.world = world;
    }

    /**
     * Gets the world to unload.
     *
     * @return The world to unload.
     * @since 5.0
     */
    public LoadedMultiverseWorld world() {
        return world;
    }

    /**
     * Sets whether to save the bukkit world before unloading.
     *
     * @param saveBukkitWorldInput  Whether to save the bukkit world before unloading.
     * @return This {@link UnloadWorldOptions} instance.
     * @since 5.0
     */
    public UnloadWorldOptions saveBukkitWorld(boolean saveBukkitWorldInput) {
        this.saveBukkitWorld = saveBukkitWorldInput;
        return this;
    }

    /**
     * Gets whether to save the bukkit world before unloading.
     *
     * @return Whether to save the bukkit world before unloading.
     * @since 5.0
     */
    public boolean saveBukkitWorld() {
        return saveBukkitWorld;
    }
}
