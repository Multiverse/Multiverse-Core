package com.onarandombox.MultiverseCore.worldnew.options;

import com.onarandombox.MultiverseCore.worldnew.MVWorld;
import org.jetbrains.annotations.NotNull;

public class CloneWorldOptions {
    public static CloneWorldOptions fromTo(MVWorld world, String newWorldName) {
        return new CloneWorldOptions(world, newWorldName);
    }

    private final MVWorld world;
    private final String newWorldName;
    private boolean keepGameRule = true;
    private boolean keepWorldConfig = true;

    private boolean keepWorldBorder = true;

    public CloneWorldOptions(MVWorld world, String newWorldName) {
        this.world = world;
        this.newWorldName = newWorldName;
    }

    public MVWorld world() {
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
