package org.mvplugins.multiverse.core.world.options;

import org.bukkit.World;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Options for customizing the import of a new world.
 */
public final class ImportWorldOptions {

    /**
     * Creates a new {@link ImportWorldOptions} instance with the given world name.
     *
     * @param worldName The name of the world to create.
     * @return A new {@link ImportWorldOptions} instance.
     */
    public static @NotNull ImportWorldOptions worldName(@NotNull String worldName) {
        return new ImportWorldOptions(worldName);
    }

    private final String worldName;
    private String biome = "";
    private World.Environment environment = World.Environment.NORMAL;
    private String generator = null;
    private boolean useSpawnAdjust = true;
    private boolean doFolderCheck = true;

    ImportWorldOptions(String worldName) {
        this.worldName = worldName;
    }

    /**
     * Gets the name of the world to create.
     *
     * @return The name of the world to create.
     */
    public @NotNull String worldName() {
        return worldName;
    }

    /**
     * Sets the single biome used for this world. This may be null, in which case the biome from the generator will be used.
     * If no generator is specified, the "natural" biome behaviour for this environment will be used.
     *
     * @param biome The biome used for this world
     * @return This {@link ImportWorldOptions} instance.
     */
    public @NotNull ImportWorldOptions biome(@NotNull String biome) {
        this.biome = biome;
        return this;
    }

    /**
     * Gets the single biome used for this world. This may be null, in which case the biome from the generator will be used.
     * If no generator is specified, the "natural" biome behaviour for this environment will be used.
     *
     * @return The biome used for this world
     */
    public @NotNull String biome() {
        return biome;
    }

    /**
     * Sets the environment of the world to create.
     *
     * @param environmentInput  The environment of the world to create.
     * @return This {@link ImportWorldOptions} instance.
     */
    public @NotNull ImportWorldOptions environment(@NotNull World.Environment environmentInput) {
        this.environment = environmentInput;
        return this;
    }

    /**
     * Gets the environment of the world to create.
     *
     * @return The environment of the world to create.
     */
    public @NotNull World.Environment environment() {
        return environment;
    }

    /**
     * Sets the custom generator plugin and its parameters.
     *
     * @param generatorInput    The custom generator plugin and its parameters.
     * @return This {@link ImportWorldOptions} instance.
     */
    public @NotNull ImportWorldOptions generator(@Nullable String generatorInput) {
        this.generator = generatorInput;
        return this;
    }

    /**
     * Gets the custom generator plugin and its parameters.
     *
     * @return The custom generator plugin and its parameters.
     */
    public @Nullable String generator() {
        return generator;
    }

    /**
     * Sets whether multiverse will search for a safe spawn location.
     *
     * @param useSpawnAdjustInput   Whether multiverse will search for a safe spawn location.
     * @return This {@link ImportWorldOptions} instance.
     */
    public @NotNull ImportWorldOptions useSpawnAdjust(boolean useSpawnAdjustInput) {
        this.useSpawnAdjust = useSpawnAdjustInput;
        return this;
    }

    /**
     * Gets whether multiverse will search for a safe spawn location.
     *
     * @return Whether multiverse will search for a safe spawn location.
     */
    public boolean useSpawnAdjust() {
        return useSpawnAdjust;
    }

    /**
     * Sets whether to ensure the world folder is a valid world before importing it.
     *
     * @param doFolderCheckInput Whether to do the folder check
     * @return This {@link ImportWorldOptions} instance
     *
     * @since 5.2
     */
    @ApiStatus.AvailableSince("5.2")
    public @NotNull ImportWorldOptions doFolderCheck(boolean doFolderCheckInput) {
        this.doFolderCheck = doFolderCheckInput;
        return this;
    }

    /**
     * Gets whether to ensure the world folder is a valid world before importing it.
     *
     * @return Whether to do the folder check
     *
     * @since 5.2
     */
    @ApiStatus.AvailableSince("5.2")
    public boolean doFolderCheck() {
        return doFolderCheck;
    }
}
