package com.onarandombox.MultiverseCore.worldnew.options;

import org.bukkit.World;
import org.bukkit.WorldType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Options for customizing the creation of a new world.
 */
public class CreateWorldOptions {

    /**
     * Creates a new {@link CreateWorldOptions} instance with the given world name.
     *
     * @param worldName The name of the world to create.
     * @return A new {@link CreateWorldOptions} instance.
     */
    public static @NotNull CreateWorldOptions worldName(@NotNull String worldName) {
        return new CreateWorldOptions(worldName);
    }

    private final String worldName;
    private World.Environment environment = World.Environment.NORMAL;
    private boolean generateStructures = true;
    private String generator = "";
    private long seed = Long.MIN_VALUE;
    private boolean useSpawnAdjust = true;
    private WorldType worldType = WorldType.NORMAL;

    /**
     * Creates a new {@link CreateWorldOptions} instance with the given world name.
     *
     * @param worldName The name of the world to create.
     */
    CreateWorldOptions(@NotNull String worldName) {
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
     * @return This {@link CreateWorldOptions} instance.
     */
    public @NotNull CreateWorldOptions environment(@NotNull World.Environment environment) {
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
     * Sets whether structures such as NPC villages should be generated.
     *
     * @param generateStructures    Whether structures such as NPC villages should be generated.
     * @return This {@link CreateWorldOptions} instance.
     */
    public @NotNull CreateWorldOptions generateStructures(boolean generateStructures) {
        this.generateStructures = generateStructures;
        return this;
    }

    /**
     * Gets whether structures such as NPC villages should be generated.
     *
     * @return Whether structures such as NPC villages should be generated.
     */
    public boolean generateStructures() {
        return generateStructures;
    }

    /**
     * Sets the custom generator plugin and its parameters.
     *
     * @param generator The custom generator plugin and its parameters.
     * @return This {@link CreateWorldOptions} instance.
     */
    public @NotNull CreateWorldOptions generator(@NotNull String generator) {
        this.generator = generator;
        return this;
    }

    /**
     * Gets the custom generator plugin and its parameters.
     *
     * @return The custom generator plugin and its parameters.
     */
    public @NotNull String generator() {
        return generator;
    }

    /**
     * Sets the seed of the world to create. If the seed is a number, it will be parsed as a long. Otherwise, it will be
     * hashed.
     *
     * @param seed  The seed of the world to create.
     * @return This {@link CreateWorldOptions} instance.
     */
    public @NotNull CreateWorldOptions seed(@Nullable String seed) {
        if (seed == null) {
            this.seed = Long.MIN_VALUE;
            return this;
        }
        try {
            this.seed = Long.parseLong(seed);
        } catch (NumberFormatException numberformatexception) {
            this.seed = seed.hashCode();
        }
        return this;
    }

    /**
     * Sets the seed of the world to create.
     *
     * @param seed  The seed of the world to create.
     * @return This {@link CreateWorldOptions} instance.
     */
    public @NotNull CreateWorldOptions seed(long seed) {
        this.seed = seed;
        return this;
    }

    /**
     * Gets the seed of the world to create.
     *
     * @return The seed of the world to create.
     */
    public long seed() {
        return seed;
    }

    /**
     * Sets whether multiverse will search for a safe spawn location.
     *
     * @param useSpawnAdjust    Whether multiverse will search for a safe spawn location.
     * @return This {@link CreateWorldOptions} instance.
     */
    public @NotNull CreateWorldOptions useSpawnAdjust(boolean useSpawnAdjust) {
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

    /**
     * Sets the world type.
     *
     * @param worldType The world type.
     * @return This {@link CreateWorldOptions} instance.
     */
    public @NotNull CreateWorldOptions worldType(@NotNull WorldType worldType) {
        this.worldType = worldType;
        return this;
    }

    /**
     * Gets the world type.
     *
     * @return The world type.
     */
    public @NotNull WorldType worldType() {
        return worldType;
    }
}
