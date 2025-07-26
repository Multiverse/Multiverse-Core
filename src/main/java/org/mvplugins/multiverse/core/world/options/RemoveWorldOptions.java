package org.mvplugins.multiverse.core.world.options;

import org.jetbrains.annotations.ApiStatus;
import org.mvplugins.multiverse.core.world.MultiverseWorld;

/**
 * Options for customizing the removal of a world.
 *
 * @since 5.2
 */
@ApiStatus.AvailableSince("5.2")
public final class RemoveWorldOptions {

    /**
     * Creates a new {@link RemoveWorldOptions} instance with the given world. The world may be a loaded or unloaded.
     *
     * @param world The world to remove.
     * @return A new {@link RemoveWorldOptions} instance.
     *
     * @since 5.2
     */
    @ApiStatus.AvailableSince("5.2")
    public static RemoveWorldOptions world(MultiverseWorld world) {
        return new RemoveWorldOptions(world);
    }

    private final MultiverseWorld world;
    private boolean saveBukkitWorld = true;
    private boolean unloadBukkitWorld = true;

    private RemoveWorldOptions(MultiverseWorld world) {
        this.world = world;
    }

    /**
     * Gets the world to remove. The world may be a loaded or unloaded.
     *
     * @return The world to remove.
     *
     * @since 5.2
     */
    @ApiStatus.AvailableSince("5.2")
    public MultiverseWorld world() {
        return world;
    }

    /**
     * Sets whether to save the Bukkit world before removing it.
     * <br />
     * This option only applies when {@link #unloadBukkitWorld()} is true.
     *
     * @param saveBukkitWorldInput Whether to save the Bukkit world.
     * @return This {@link RemoveWorldOptions} instance.
     *
     * @since 5.2
     */
    @ApiStatus.AvailableSince("5.2")
    public RemoveWorldOptions saveBukkitWorld(boolean saveBukkitWorldInput) {
        this.saveBukkitWorld = saveBukkitWorldInput;
        return this;
    }

    /**
     * Gets whether to save the Bukkit world before removing it. By default, this is true.
     * <br />
     * This option only applies when {@link #unloadBukkitWorld()} is true.
     *
     * @return Whether to save the Bukkit world.
     *
     * @since 5.2
     */
    @ApiStatus.AvailableSince("5.2")
    public boolean saveBukkitWorld() {
        return saveBukkitWorld;
    }

    /**
     * Sets whether to unload the Bukkit world before removing it. This option is usually only used if the world is
     * managed by another plugin and you want to untrack it from Multiverse without unloading from the server.
     *
     * @param unloadBukkitWorldInput Whether to unload the Bukkit world.
     * @return This {@link RemoveWorldOptions} instance.
     *
     * @since 5.2
     */
    @ApiStatus.AvailableSince("5.2")
    public RemoveWorldOptions unloadBukkitWorld(boolean unloadBukkitWorldInput) {
        this.unloadBukkitWorld = unloadBukkitWorldInput;
        return this;
    }

    /**
     * Gets whether to unload the Bukkit world before removing it. By default, this is true.
     *
     * @return Whether to unload the Bukkit world.
     *
     * @since 5.2
     */
    @ApiStatus.AvailableSince("5.2")
    public boolean unloadBukkitWorld() {
        return unloadBukkitWorld;
    }
}
