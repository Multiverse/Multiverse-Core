package org.mvplugins.multiverse.core.world.options;

import io.vavr.control.Either;
import org.bukkit.NamespacedKey;
import org.bukkit.World;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.mvplugins.multiverse.core.world.key.WorldKeyOrName;

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
        return new ImportWorldOptions(Either.left(worldName));
    }

    /**
     * Creates a new {@link ImportWorldOptions} instance with the given namespaced key. Note that importing world with
     * namespace requires PaperMC. This will not work on Spigot.
     *
     * @param key The namespaced key for the world to import.
     * @return A new {@link ImportWorldOptions} instance.
     *
     * @since 5.7
     */
    @ApiStatus.AvailableSince("5.7")
    public static @NotNull ImportWorldOptions worldKey(@NotNull NamespacedKey key) {
        return new ImportWorldOptions(Either.right(WorldKeyOrName.parseKey(key)));
    }

    /**
     * Creates a new {@link ImportWorldOptions} instance with the given world key or name. Note that importing world with
     * namespace requires PaperMC. WorldKeyOrName parsed as namespaced key (i.e. {@link WorldKeyOrName#isKey()} is true)
     * will not work on Spigot.
     *
     * @param keyOrName The key or name for the world to import.
     * @return A new {@link ImportWorldOptions} instance.
     *
     * @since 5.7
     */
    @ApiStatus.AvailableSince("5.7")
    public static @NotNull ImportWorldOptions worldKeyOrName(@NotNull WorldKeyOrName keyOrName) {
        return new ImportWorldOptions(Either.right(keyOrName));
    }

    private final Either<String, WorldKeyOrName> keyOrName;
    private String biome = "";
    private World.Environment environment = World.Environment.NORMAL;
    private String generator = null;
    private boolean useSpawnAdjust = true;
    private boolean doFolderCheck = true;

    /**
     * Creates a new {@link ImportWorldOptions} instance with either a world name or world key or name.
     *
     * @param keyOrName Either the world name or the world key/name instance.
     */
    ImportWorldOptions(Either<String, WorldKeyOrName> keyOrName) {
        this.keyOrName = keyOrName;
    }

    /**
     * Gets the new world key or name, either unparsed as string or the {@link WorldKeyOrName} instance.
     *
     * @return The new world key or name.
     *
     * @since 5.7
     */
    @ApiStatus.AvailableSince("5.7")
    public @NotNull Either<String, WorldKeyOrName> keyOrName() {
        return keyOrName;
    }

    /**
     * Gets the name of the world to create.
     *
     * @return The name of the world to create.
     */
    @Deprecated(forRemoval = true, since = "5.7")
    @ApiStatus.ScheduledForRemoval(inVersion = "6.0")
    public @NotNull String worldName() {
        return keyOrName.fold(name -> name ,WorldKeyOrName::usableName);
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
