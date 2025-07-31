package org.mvplugins.multiverse.core.world.options;

import org.jetbrains.annotations.ApiStatus;
import org.mvplugins.multiverse.core.world.MultiverseWorld;

/**
 * Options for customizing the loading of a world.
 *
 * @since 5.2
 */
@ApiStatus.AvailableSince("5.2")
public final class LoadWorldOptions {

    /**
     * Creates a new {@link LoadWorldOptions} instance with the given world.
     *
     * @param world The world to load.
     * @return A new {@link LoadWorldOptions} instance.
     *
     * @since 5.2
     */
    @ApiStatus.AvailableSince("5.2")
    public static LoadWorldOptions world(MultiverseWorld world) {
        return new LoadWorldOptions(world);
    }

    private final MultiverseWorld world;
    private boolean doFolderCheck = true;

    LoadWorldOptions(MultiverseWorld world) {
        this.world = world;
    }

    /**
     * Gets the world to load.
     *
     * @return The world to load.
     *
     * @since 5.2
     */
    @ApiStatus.AvailableSince("5.2")
    public MultiverseWorld world() {
        return world;
    }

    /**
     * Sets whether to check if the world folder is valid before loading the world.
     * <br />
     * This helps to prevent deleted world folders from being re-created.
     *
     * @return Whether to check if the world folder is valid before loading the world.
     *
     * @since 5.2
     */
    @ApiStatus.AvailableSince("5.2")
    public boolean doFolderCheck() {
        return doFolderCheck;
    }

    /**
     * Sets whether to check if the world folder is valid before loading the world.
     *
     * @return This {@link LoadWorldOptions} instance.
     *
     * @since 5.2
     */
    @ApiStatus.AvailableSince("5.2")
    public LoadWorldOptions doFolderCheck(boolean doFolderCheck) {
        this.doFolderCheck = doFolderCheck;
        return this;
    }
}
