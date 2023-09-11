package com.onarandombox.MultiverseCore.worldnew.options;

import com.onarandombox.MultiverseCore.worldnew.LoadedMultiverseWorld;

/**
 * Options for customizing the unloading of a world.
 */
public class UnloadWorldOptions {

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
    private boolean removePlayers = false;
    private boolean saveBukkitWorld = true;

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
     * Sets whether to teleport the players out from the world before unloading.
     *
     * @param removePlayersInput    Whether to remove players from the world before unloading.
     * @return This {@link UnloadWorldOptions} instance.
     */
    public UnloadWorldOptions removePlayers(boolean removePlayersInput) {
        this.removePlayers = removePlayersInput;
        return this;
    }

    /**
     * Gets whether to teleport the players out from the world before unloading.
     *
     * @return Whether to remove players from the world before unloading.
     */
    public boolean removePlayers() {
        return removePlayers;
    }

    /**
     * Sets whether to save the bukkit world before unloading.
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
