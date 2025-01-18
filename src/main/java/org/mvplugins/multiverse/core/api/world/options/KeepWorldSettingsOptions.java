package org.mvplugins.multiverse.core.api.world.options;

import org.jetbrains.annotations.NotNull;

/**
 * Options for customizing the keeping of world settings. Used by clone and regen.
 *
 * @since 5.0
 */
public sealed interface KeepWorldSettingsOptions permits CloneWorldOptions, RegenWorldOptions {

    /**
     * Sets whether to keep the game rule of the world.
     *
     * @param keepGameRuleInput Whether to keep the game rule of the world.
     * @return This {@link KeepWorldSettingsOptions} instance.
     * @since 5.0
     */
    @NotNull KeepWorldSettingsOptions keepGameRule(boolean keepGameRuleInput);

    /**
     * Gets whether to keep the game rule of the world.
     *
     * @return Whether to keep the game rule of the world.
     * @since 5.0
     */
    boolean keepGameRule();

    /**
     * Sets whether to keep the world config of the world.
     *
     * @param keepWorldConfigInput  Whether to keep the world config of the world.
     * @return This {@link KeepWorldSettingsOptions} instance.
     * @since 5.0
     */
    @NotNull KeepWorldSettingsOptions keepWorldConfig(boolean keepWorldConfigInput);

    /**
     * Gets whether to keep the world config of the world.
     *
     * @return Whether to keep the world config of the world.
     * @since 5.0
     */
    boolean keepWorldConfig();

    /**
     * Sets whether to keep the world border of the world.
     *
     * @param keepWorldBorderInput  Whether to keep the world border of the world.
     * @return This {@link KeepWorldSettingsOptions} instance.
     * @since 5.0
     */
    @NotNull KeepWorldSettingsOptions keepWorldBorder(boolean keepWorldBorderInput);

    /**
     * Gets whether to keep the world border of the world.
     *
     * @return  Whether to keep the world border of the world.
     * @since 5.0
     */
    boolean keepWorldBorder();
}
