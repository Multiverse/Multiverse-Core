package com.onarandombox.MultiverseCore.worldnew.options;

import java.util.Random;

import com.onarandombox.MultiverseCore.worldnew.LoadedMultiverseWorld;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Options for customizing the regeneration of a world.
 */
public final class RegenWorldOptions implements KeepWorldSettingsOptions {

    /**
     * Creates a new {@link RegenWorldOptions} instance with the given world.
     *
     * @param world The world to regenerate.
     * @return A new {@link RegenWorldOptions} instance.
     */
    public static @NotNull RegenWorldOptions world(@NotNull LoadedMultiverseWorld world) {
        return new RegenWorldOptions(world);
    }

    private final LoadedMultiverseWorld world;
    private boolean keepGameRule = true;
    private boolean keepWorldConfig = true;

    private boolean keepWorldBorder = true;
    private boolean randomSeed = false;
    private long seed = Long.MIN_VALUE;

    RegenWorldOptions(@NotNull LoadedMultiverseWorld world) {
        this.world = world;
    }

    /**
     * Gets the world to regenerate.
     *
     * @return The world to regenerate.
     */
    public @NotNull LoadedMultiverseWorld world() {
        return world;
    }

    /**
     * Sets whether to keep the game rule of the world during regeneration.
     *
     * @param keepGameRuleInput  Whether to keep the game rule of the world during regeneration.
     * @return This {@link RegenWorldOptions} instance.
     */
    @Override
    public @NotNull RegenWorldOptions keepGameRule(boolean keepGameRuleInput) {
        this.keepGameRule = keepGameRuleInput;
        return this;
    }

    /**
     * Gets whether to keep the game rule of the world during regeneration.
     *
     * @return Whether to keep the game rule of the world during regeneration.
     */
    @Override
    public boolean keepGameRule() {
        return keepGameRule;
    }

    /**
     * Sets whether to keep the world config of the world during regeneration.
     *
     * @param keepWorldConfigInput  Whether to keep the world config of the world during regeneration.
     * @return This {@link RegenWorldOptions} instance.
     */
    @Override
    public @NotNull RegenWorldOptions keepWorldConfig(boolean keepWorldConfigInput) {
        this.keepWorldConfig = keepWorldConfigInput;
        return this;
    }

    /**
     * Gets whether to keep the world config of the world during regeneration.
     *
     * @return Whether to keep the world config of the world during regeneration.
     */
    @Override
    public boolean keepWorldConfig() {
        return keepWorldConfig;
    }

    /**
     * Sets whether to keep the world border of the world during regeneration.
     *
     * @param keepWorldBorderInput  Whether to keep the world border of the world.
     * @return This {@link RegenWorldOptions} instance.
     */
    @Override
    public @NotNull RegenWorldOptions keepWorldBorder(boolean keepWorldBorderInput) {
        this.keepWorldBorder = keepWorldBorderInput;
        return this;
    }

    /**
     * Gets whether to keep the world border of the world during regeneration.
     *
     * @return  Whether to keep the world border of the world during regeneration.
     */
    @Override
    public boolean keepWorldBorder() {
        return keepWorldBorder;
    }

    /**
     * Sets whether to use a random seed for the world to regenerate. Cannot be set to true when seed is set.
     *
     * @param randomSeedInput   Whether to use a random seed for the world to regenerate.
     * @return This {@link RegenWorldOptions} instance.
     */
    public @NotNull RegenWorldOptions randomSeed(boolean randomSeedInput) {
        if (randomSeedInput && seed != Long.MIN_VALUE) {
            throw new IllegalStateException("Cannot set randomSeed to true when seed is set");
        }
        this.randomSeed = randomSeedInput;
        return this;
    }

    /**
     * Gets whether to use a random seed for the world to regenerate.
     *
     * @return Whether to use a random seed for the world to regenerate.
     */
    public boolean randomSeed() {
        return randomSeed;
    }

    /**
     * Sets the seed for the world to regenerate. Random seed will be disabled.
     *
     * @param seedInput The seed for the world to regenerate.
     * @return This {@link RegenWorldOptions} instance.
     */
    public @NotNull RegenWorldOptions seed(@Nullable String seedInput) {
        if (seedInput == null) {
            this.seed = Long.MIN_VALUE;
            return this;
        }
        if (randomSeed) {
            randomSeed(false);
        }
        try {
            this.seed = Long.parseLong(seedInput);
        } catch (NumberFormatException numberformatexception) {
            this.seed = seedInput.hashCode();
        }
        return this;
    }

    /**
     * Sets the seed for the world to regenerate. Random seed will be disabled.
     *
     * @param seedInput The seed for the world to regenerate.
     * @return This {@link RegenWorldOptions} instance.
     */
    public @NotNull RegenWorldOptions seed(long seedInput) {
        this.seed = seedInput;
        return this;
    }

    /**
     * Gets the seed for the world to regenerate.
     *
     * @return The seed for the world to regenerate.
     */
    public long seed() {
        if (randomSeed) {
            return new Random().nextLong();
        } else if (seed == Long.MIN_VALUE) {
            return world.getSeed();
        }
        return seed;
    }
}
