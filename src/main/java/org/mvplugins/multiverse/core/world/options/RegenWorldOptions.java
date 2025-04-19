package org.mvplugins.multiverse.core.world.options;

import co.aikar.commands.ACFUtil;
import org.bukkit.block.Biome;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import org.mvplugins.multiverse.core.world.LoadedMultiverseWorld;

/**
 * Options for customizing the regeneration of a world.
 */
public final class RegenWorldOptions implements KeepWorldSettingsOptions {

    private static final long UNINITIALIZED_SEED_VALUE = Long.MIN_VALUE;

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
    private Biome biome;
    private boolean keepGameRule = true;
    private boolean keepWorldConfig = true;
    private boolean keepWorldBorder = true;
    private boolean randomSeed = false;
    private long seed = UNINITIALIZED_SEED_VALUE;

    RegenWorldOptions(@NotNull LoadedMultiverseWorld world) {
        this.world = world;
    }

    private boolean isSeedInitialized() {
        return seed != UNINITIALIZED_SEED_VALUE;
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
     * Sets the single biome used for this world. This may be null, in which case the biome from the generator will be
     * used. If no generator is specified, the "natural" biome behaviour for this environment will be used.
     *
     * @param biome The biome used for this world
     * @return This {@link RegenWorldOptions} instance.
     */
    public @NotNull RegenWorldOptions biome(@Nullable Biome biome) {
        this.biome = biome;
        return this;
    }

    /**
     * Gets the single biome used for this world. This may be null, in which case the biome from the generator will be
     * used. If no generator is specified, the "natural" biome behaviour for this environment will be used.
     *
     * @return The biome used for this world
     */
    public @NotNull Biome biome() {
        return biome;
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
        if (randomSeedInput && isSeedInitialized()) {
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
    @SuppressWarnings("unused")
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
            this.seed = UNINITIALIZED_SEED_VALUE;
            return this;
        }
        if (randomSeed) {
            randomSeed(false);
        }
        this.seed = parseOrHashSeed(seedInput);
        return this;
    }

    private long parseOrHashSeed(String seedInput) {
        try {
            return Long.parseLong(seedInput);
        } catch (NumberFormatException numberformatexception) {
            return seedInput.hashCode();
        }
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
            return ACFUtil.RANDOM.nextLong();
        } else if (isSeedInitialized()) {
            return seed;
        }
        return world.getSeed();
    }
}
