package com.onarandombox.MultiverseCore.worldnew.options;

import com.onarandombox.MultiverseCore.worldnew.LoadedMultiverseWorld;
import org.jetbrains.annotations.NotNull;

public class CloneWorldOptions {
    public static CloneWorldOptions fromTo(LoadedMultiverseWorld world, String newWorldName) {
        return new CloneWorldOptions(world, newWorldName);
    }

    private final LoadedMultiverseWorld world;
    private final String newWorldName;
    private boolean keepGameRule = true;
    private boolean keepWorldConfig = true;

    private boolean keepWorldBorder = true;

    public CloneWorldOptions(LoadedMultiverseWorld world, String newWorldName) {
        this.world = world;
        this.newWorldName = newWorldName;
    }

    public LoadedMultiverseWorld world() {
        return world;
    }

    public String newWorldName() {
        return newWorldName;
    }

    public @NotNull CloneWorldOptions keepGameRule(boolean keepGameRule) {
        this.keepGameRule = keepGameRule;
        return this;
    }

    public boolean keepGameRule() {
        return keepGameRule;
    }

    public @NotNull CloneWorldOptions keepWorldConfig(boolean keepWorldConfig) {
        this.keepWorldConfig = keepWorldConfig;
        return this;
    }

    public boolean keepWorldConfig() {
        return keepWorldConfig;
    }

    public @NotNull CloneWorldOptions keepWorldBorder(boolean keepWorldBorder) {
        this.keepWorldBorder = keepWorldBorder;
        return this;
    }

    public boolean keepWorldBorder() {
        return keepWorldBorder;
    }
}
