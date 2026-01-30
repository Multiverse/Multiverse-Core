package org.mvplugins.multiverse.core.world.options;

import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.mvplugins.multiverse.core.world.LoadedMultiverseWorld;
import org.mvplugins.multiverse.core.world.MultiverseWorld;

/**
 * Options for customizing the cloning of a world.
 */
public final class CloneWorldOptions implements KeepWorldSettingsOptions {

    /**
     * Creates a new {@link CloneWorldOptions} instance with the given loaded world.
     *
     * @param fromWorld     The loaded world to clone.
     * @param newWorldName  The name of the new world.
     * @return A new {@link CloneWorldOptions} instance.
     *
     * @deprecated Cloning can be done from unloaded worlds as well. Use {@link #fromTo(MultiverseWorld, String)} instead.
     */
    @Deprecated(forRemoval = true, since = "5.6")
    @ApiStatus.ScheduledForRemoval(inVersion = "6.0")
    public static @NotNull CloneWorldOptions fromTo(@NotNull LoadedMultiverseWorld fromWorld, @NotNull String newWorldName) {
        return new CloneWorldOptions(fromWorld, newWorldName);
    }

    /**
     * Creates a new {@link CloneWorldOptions} instance with the given world.
     *
     * @param fromWorld     The world to clone.
     * @param newWorldName  The name of the new world.
     * @return A new {@link CloneWorldOptions} instance.
     *
     * @since 5.6
     */
    @ApiStatus.AvailableSince("5.6")
    public static @NotNull CloneWorldOptions fromTo(@NotNull MultiverseWorld fromWorld, @NotNull String newWorldName) {
        return new CloneWorldOptions(fromWorld, newWorldName);
    }

    private final MultiverseWorld fromWorld;
    private final String newWorldName;
    private boolean keepGameRule = true;
    private boolean keepWorldConfig = true;
    private boolean saveBukkitWorld = true;

    private boolean keepWorldBorder = true;

    CloneWorldOptions(MultiverseWorld fromWorld, String newWorldName) {
        this.fromWorld = fromWorld;
        this.newWorldName = newWorldName;
    }

    /**
     * Gets the loaded world to clone.
     *
     * @return The world to clone.
     *
     * @deprecated Cloning can be done from unloaded worlds as well. Use {@link #fromWorld()} instead.
     */
    @Deprecated(forRemoval = true, since = "5.6")
    @ApiStatus.ScheduledForRemoval(inVersion = "6.0")
    public LoadedMultiverseWorld world() {
        return fromWorld.asLoadedWorld().getOrNull();
    }

    /**
     * Gets the loaded world to clone.
     *
     * @return The world to clone.
     *
     * @since 5.6
     */
    @ApiStatus.AvailableSince("5.6")
    public @NotNull MultiverseWorld fromWorld() {
        return fromWorld;
    }

    /**
     * Gets the name of the new world.
     *
     * @return The name of the new world.
     */
    public @NotNull String newWorldName() {
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

    /**
     * Sets whether to save the world to disk before clone copying.
     *
     * @param saveBukkitWorldInput Whether to save the world to disk before clone copying.
     * @return This {@link CloneWorldOptions} instance.
     */
    public @NotNull CloneWorldOptions saveBukkitWorld(boolean saveBukkitWorldInput) {
        this.saveBukkitWorld = saveBukkitWorldInput;
        return this;
    }

    /**
     * Gets whether to save the world to disk before clone copying.
     *
     * @return Whether to save the world to disk before clone copying.
     */
    public boolean saveBukkitWorld() {
        return saveBukkitWorld;
    }
}
