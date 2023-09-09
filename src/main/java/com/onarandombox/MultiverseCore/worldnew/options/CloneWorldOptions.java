package com.onarandombox.MultiverseCore.worldnew.options;

import com.onarandombox.MultiverseCore.worldnew.LoadedMultiverseWorld;
import org.jetbrains.annotations.NotNull;

/**
 * Options for customizing the cloning of a world.
 */
public class CloneWorldOptions {

    /**
     * Creates a new {@link CloneWorldOptions} instance with the given world.
     *
     * @param world         The world to clone.
     * @param newWorldName  The name of the new world.
     * @return A new {@link CloneWorldOptions} instance.
     */
    public static CloneWorldOptions fromTo(LoadedMultiverseWorld world, String newWorldName) {
        return new CloneWorldOptions(world, newWorldName);
    }

    private final LoadedMultiverseWorld world;
    private final String newWorldName;
    private boolean keepGameRule = true;
    private boolean keepWorldConfig = true;

    private boolean keepWorldBorder = true;

    CloneWorldOptions(LoadedMultiverseWorld world, String newWorldName) {
        this.world = world;
        this.newWorldName = newWorldName;
    }

    /**
     * Gets the world to clone.
     *
     * @return The world to clone.
     */
    public LoadedMultiverseWorld world() {
        return world;
    }

    /**
     * Gets the name of the new world.
     *
     * @return The name of the new world.
     */
    public String newWorldName() {
        return newWorldName;
    }

    /**
     * Sets whether to keep the game rule of the world during cloning.
     *
     * @param keepGameRule  Whether to keep the game rule of the world during cloning.
     * @return This {@link CloneWorldOptions} instance.
     */
    public @NotNull CloneWorldOptions keepGameRule(boolean keepGameRule) {
        this.keepGameRule = keepGameRule;
        return this;
    }

    /**
     * Gets whether to keep the game rule of the world during cloning.
     *
     * @return Whether to keep the game rule of the world during cloning.
     */
    public boolean keepGameRule() {
        return keepGameRule;
    }

    public @NotNull CloneWorldOptions keepWorldConfig(boolean keepWorldConfig) {
        this.keepWorldConfig = keepWorldConfig;
        return this;
    }

    /**
     * Gets whether to keep the world config of the world during cloning.
     *
     * @return Whether to keep the world config of the world during cloning.
     */
    public boolean keepWorldConfig() {
        return keepWorldConfig;
    }

    public @NotNull CloneWorldOptions keepWorldBorder(boolean keepWorldBorder) {
        this.keepWorldBorder = keepWorldBorder;
        return this;
    }

    /**
     * Gets whether to keep the world border of the world during cloning.
     *
     * @return Whether to keep the world border of the world during cloning.
     */
    public boolean keepWorldBorder() {
        return keepWorldBorder;
    }
}
