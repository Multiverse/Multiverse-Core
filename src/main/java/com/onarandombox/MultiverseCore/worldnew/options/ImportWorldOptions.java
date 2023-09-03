package com.onarandombox.MultiverseCore.worldnew.options;

import org.bukkit.World;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class ImportWorldOptions {

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
    private World.Environment environment = World.Environment.NORMAL;
    private String generator = null;
    private boolean useSpawnAdjust = true;

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
     * Sets the environment of the world to create.
     *
     * @param environment   The environment of the world to create.
     * @return This {@link ImportWorldOptions} instance.
     */
    public @NotNull ImportWorldOptions environment(@NotNull World.Environment environment) {
        this.environment = environment;
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
     * @param generator The custom generator plugin and its parameters.
     * @return This {@link ImportWorldOptions} instance.
     */
    public @NotNull ImportWorldOptions generator(@Nullable String generator) {
        this.generator = generator;
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
     * @param useSpawnAdjust    Whether multiverse will search for a safe spawn location.
     * @return This {@link ImportWorldOptions} instance.
     */
    public @NotNull ImportWorldOptions useSpawnAdjust(boolean useSpawnAdjust) {
        this.useSpawnAdjust = useSpawnAdjust;
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
}
