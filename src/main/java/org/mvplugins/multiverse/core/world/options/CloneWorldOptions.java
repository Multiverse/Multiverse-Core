package org.mvplugins.multiverse.core.world.options;

import org.jetbrains.annotations.NotNull;
import org.mvplugins.multiverse.core.world.LoadedMultiverseWorld;

/**
 * Options for customizing the cloning of a world.
 */
public final class CloneWorldOptions implements KeepWorldSettingsOptions {

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
     * @param keepGameRuleInput Whether to keep the game rule of the world during cloning.
     * @return This {@link CloneWorldOptions} instance.
     */
    @Override
    public @NotNull CloneWorldOptions keepGameRule(boolean keepGameRuleInput) {
        this.keepGameRule = keepGameRuleInput;
        return this;
    }

    /**
     * Gets whether to keep the game rule of the world during cloning.
     *
     * @return Whether to keep the game rule of the world during cloning.
     */
    @Override
    public boolean keepGameRule() {
        return keepGameRule;
    }

    /**
     * Sets whether to keep the world config of the world during cloning.
     *
     * @param keepWorldConfigInput  Whether to keep the world config of the world.
     * @return This {@link CloneWorldOptions} instance.
     */
    @Override
    public @NotNull CloneWorldOptions keepWorldConfig(boolean keepWorldConfigInput) {
        this.keepWorldConfig = keepWorldConfigInput;
        return this;
    }

    /**
     * Gets whether to keep the world config of the world during cloning.
     *
     * @return Whether to keep the world config of the world during cloning.
     */
    @Override
    public boolean keepWorldConfig() {
        return keepWorldConfig;
    }


    /**
     * Sets whether to keep the world border of the world during cloning.
     *
     * @param keepWorldBorderInput  Whether to keep the world border of the world.
     * @return This {@link CloneWorldOptions} instance.
     */
    @Override
    public @NotNull CloneWorldOptions keepWorldBorder(boolean keepWorldBorderInput) {
        this.keepWorldBorder = keepWorldBorderInput;
        return this;
    }

    /**
     * Gets whether to keep the world border of the world during cloning.
     *
     * @return Whether to keep the world border of the world during cloning.
     */
    @Override
    public boolean keepWorldBorder() {
        return keepWorldBorder;
    }
}
