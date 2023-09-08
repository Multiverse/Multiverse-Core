package com.onarandombox.MultiverseCore.worldnew.options;

import com.onarandombox.MultiverseCore.worldnew.LoadedMultiverseWorld;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Random;

public class RegenWorldOptions {
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

    public @NotNull LoadedMultiverseWorld world() {
        return world;
    }

    public @NotNull RegenWorldOptions keepGameRule(boolean keepGameRule) {
        this.keepGameRule = keepGameRule;
        return this;
    }

    public boolean keepGameRule() {
        return keepGameRule;
    }

    public @NotNull RegenWorldOptions keepWorldConfig(boolean keepWorldConfig) {
        this.keepWorldConfig = keepWorldConfig;
        return this;
    }

    public boolean keepWorldConfig() {
        return keepWorldConfig;
    }

    public @NotNull RegenWorldOptions keepWorldBorder(boolean keepWorldBorder) {
        this.keepWorldBorder = keepWorldBorder;
        return this;
    }

    public boolean keepWorldBorder() {
        return keepWorldBorder;
    }

    public @NotNull RegenWorldOptions randomSeed(boolean randomSeed) {
        if (randomSeed && seed != Long.MIN_VALUE) {
            throw new IllegalStateException("Cannot set randomSeed to true when seed is set");
        }
        this.randomSeed = randomSeed;
        return this;
    }

    public boolean randomSeed() {
        return randomSeed;
    }

    public @NotNull RegenWorldOptions seed(@Nullable String seed) {
        if (seed == null) {
            this.seed = Long.MIN_VALUE;
            return this;
        }
        if (randomSeed) {
            randomSeed(false);
        }
        try {
            this.seed = Long.parseLong(seed);
        } catch (NumberFormatException numberformatexception) {
            this.seed = seed.hashCode();
        }
        return this;
    }

    public @NotNull RegenWorldOptions seed(long seed) {
        this.seed = seed;
        return this;
    }

    public long seed() {
        if (randomSeed) {
            return new Random().nextLong();
        }
        if (seed == Long.MIN_VALUE) {
            return world.getSeed();
        }
        return seed;
    }
}
