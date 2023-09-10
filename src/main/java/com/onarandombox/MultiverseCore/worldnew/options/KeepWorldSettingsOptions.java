package com.onarandombox.MultiverseCore.worldnew.options;

import org.jetbrains.annotations.NotNull;

public sealed interface KeepWorldSettingsOptions permits CloneWorldOptions, RegenWorldOptions {

    /**
     * Sets whether to keep the game rule of the world.
     *
     * @param keepGameRule Whether to keep the game rule of the world.
     * @return This {@link KeepWorldSettingsOptions} instance.
     */
    @NotNull KeepWorldSettingsOptions keepGameRule(boolean keepGameRule);

    /**
     * Gets whether to keep the game rule of the world.
     *
     * @return Whether to keep the game rule of the world.
     */
    boolean keepGameRule();

    /**
     * Sets whether to keep the world config of the world.
     *
     * @param keepWorldConfig   Whether to keep the world config of the world.
     * @return This {@link KeepWorldSettingsOptions} instance.
     */
    @NotNull KeepWorldSettingsOptions keepWorldConfig(boolean keepWorldConfig);

    /**
     * Gets whether to keep the world config of the world.
     *
     * @return Whether to keep the world config of the world.
     */
    boolean keepWorldConfig();

    /**
     * Sets whether to keep the world border of the world.
     *
     * @param keepWorldBorder  Whether to keep the world border of the world.
     * @return This {@link KeepWorldSettingsOptions} instance.
     */
    @NotNull KeepWorldSettingsOptions keepWorldBorder(boolean keepWorldBorder);

    /**
     * Gets whether to keep the world border of the world.
     *
     * @return  Whether to keep the world border of the world.
     */
    boolean keepWorldBorder();
}
