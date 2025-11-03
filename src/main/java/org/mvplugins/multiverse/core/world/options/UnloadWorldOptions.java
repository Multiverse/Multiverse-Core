package org.mvplugins.multiverse.core.world.options;

import org.mvplugins.multiverse.core.world.LoadedMultiverseWorld;

/**
 * Options for customizing the unloading of a world.
 */
public final class UnloadWorldOptions {

    /**
     * Creates a new {@link UnloadWorldOptions} instance with the given world.
     *
     * @param world The world to unload.
     * @return A new {@link UnloadWorldOptions} instance.
     */
    public static UnloadWorldOptions world(LoadedMultiverseWorld world) {
        return new UnloadWorldOptions(world);
    }

    private final LoadedMultiverseWorld world;
    private boolean saveBukkitWorld = true;
    private boolean unloadBukkitWorld = true;

    UnloadWorldOptions(LoadedMultiverseWorld world) {
        this.world = world;
    }

    /**
     * Gets the world to unload.
     *
     * @return The world to unload.
     */
    public LoadedMultiverseWorld world() {
        return world;
    }

    /**
     * Sets whether to unload the bukkit world.
     * <br />
     * By setting this to false, multiverse will essentially untrack the world, but the world itself is still loaded.
     * This should be only used in edge cases where the world is used by other plugins but you don't want multiverse
     * to handle it.
     *
     * @param unloadBukkitWorldInput  Whether to unload the bukkit world.
     * @return This {@link UnloadWorldOptions} instance.
     */
    public UnloadWorldOptions unloadBukkitWorld(boolean unloadBukkitWorldInput) {
        this.unloadBukkitWorld = unloadBukkitWorldInput;
        return this;
    }

    /**
     * Gets whether to unload the bukkit world before unloading.
     *
     * @return Whether to unload the bukkit world before unloading.
     */
    public boolean unloadBukkitWorld() {
        return unloadBukkitWorld;
    }

    /**
     * Sets whether to save the bukkit world before unloading.
     * <br />
     * This option only applies when {@link #unloadBukkitWorld()} is true.
     *
     * @param saveBukkitWorldInput  Whether to save the bukkit world before unloading.
     * @return This {@link UnloadWorldOptions} instance.
     */
    public UnloadWorldOptions saveBukkitWorld(boolean saveBukkitWorldInput) {
        this.saveBukkitWorld = saveBukkitWorldInput;
        return this;
    }

    /**
     * Gets whether to save the bukkit world before unloading.
     *
     * @return Whether to save the bukkit world before unloading.
     */
    public boolean saveBukkitWorld() {
        return saveBukkitWorld;
    }
}
