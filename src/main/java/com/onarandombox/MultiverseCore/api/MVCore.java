/******************************************************************************
 * Multiverse 2 Copyright (c) the Multiverse Team 2011.                       *
 * Multiverse 2 is licensed under the BSD License.                            *
 * For more information please check the README.md file included              *
 * with this project.                                                         *
 ******************************************************************************/

package com.onarandombox.MultiverseCore.api;

import com.onarandombox.MultiverseCore.anchor.AnchorManager;
import com.onarandombox.MultiverseCore.commandtools.MVCommandManager;
import com.onarandombox.MultiverseCore.destination.DestinationsProvider;
import com.onarandombox.MultiverseCore.economy.MVEconomist;
import com.onarandombox.MultiverseCore.teleportation.SimpleBlockSafety;
import com.onarandombox.MultiverseCore.teleportation.SimpleLocationManipulation;
import com.onarandombox.MultiverseCore.teleportation.SimpleSafeTTeleporter;
import com.onarandombox.MultiverseCore.utils.MVPlayerSession;
import com.onarandombox.MultiverseCore.utils.PermissionsTool;
import com.onarandombox.MultiverseCore.utils.UnsafeCallWrapper;
import com.onarandombox.MultiverseCore.utils.actioncheck.PlayerActionChecker;

/**
 * Multiverse 2 Core API
 * <p>
 * This API contains a bunch of useful things you can get out of Multiverse in general!
 */
public interface MVCore extends MVPlugin {
    /**
     * Retrieves Multiverse's friendly economist. The economist can be used for dealing with economies without
     * worrying about any of the messy details.
     *
     * @return the economy manager for Multiverse.
     */
    MVEconomist getEconomist();

    /**
     * Reloads the Multiverse Configuration files:
     * worlds.yml and config.yml.
     */
    void loadConfigs();

    /**
     * Gets the {@link UnsafeCallWrapper} class.
     *
     * @return A non-null {@link UnsafeCallWrapper}.
     */
    UnsafeCallWrapper getUnsafeCallWrapper();

    /**
     * Multiverse uses {@link MVCommandManager} to make adding and using commands
     * a piece of cake.
     *
     * @return A non-null {@link MVCommandManager}.
     */
    MVCommandManager getMVCommandManager();

    /**
     * Gets the class responsible for loading many different destinations
     * on demand.
     *
     * @return A valid {@link DestinationsProvider}.
     */
    DestinationsProvider getDestinationsProvider();

    /**
     * Gets the primary class responsible for managing Multiverse Worlds.
     *
     * @return {@link MVWorldManager}.
     */
    MVWorldManager getMVWorldManager();

    /**
     * Saves the Multiverse-Config.
     *
     * @return Whether the Multiverse-Config was successfully saved
     */
    boolean saveMVConfig();

    /**
     * Saves all configs.
     *
     * @return Whether the config was successfully saved
     */
    boolean saveAllConfigs();

    /**
     * Gets the {@link AnchorManager}.
     *
     * @return The {@link AnchorManager}
     */
    AnchorManager getAnchorManager();

    /**
     * Decrements the number of plugins that have specifically hooked into core.
     */
    void decrementPluginCount();

    /**
     * Increments the number of plugins that have specifically hooked into core.
     */
    void incrementPluginCount();

    /**
     * Returns the number of plugins that have specifically hooked into core.
     *
     * @return The number if plugins that have hooked into core.
     */
    int getPluginCount();

    /**
     * Gets the {@link PermissionsTool} instance.
     *
     * @return The {@link PermissionsTool} instance.
     */
    PermissionsTool getPermissionsTool();

    /**
     * Gets the {@link PlayerActionChecker} instance.
     *
     * @return The {@link PlayerActionChecker} instance.
     */
    PlayerActionChecker getPlayerActionChecker();

    /**
     * Gets the {@link BlockSafety} this {@link MVCore} is using.
     * @return The {@link BlockSafety} this {@link MVCore} is using.
     * @see BlockSafety
     * @see SimpleBlockSafety
     */
    BlockSafety getBlockSafety();

    /**
     * Sets the {@link BlockSafety} this {@link MVCore} is using.
     * @param blockSafety The new {@link BlockSafety}.
     * @see BlockSafety
     * @see SimpleBlockSafety
     */
    void setBlockSafety(BlockSafety blockSafety);

    /**
     * Gets the {@link LocationManipulation} this {@link MVCore} is using.
     * @return The {@link LocationManipulation} this {@link MVCore} is using.
     * @see LocationManipulation
     * @see SimpleLocationManipulation
     */
    LocationManipulation getLocationManipulation();

    /**
     * Sets the {@link LocationManipulation} this {@link MVCore} is using.
     * @param locationManipulation The new {@link LocationManipulation}.
     * @see LocationManipulation
     * @see SimpleLocationManipulation
     */
    void setLocationManipulation(LocationManipulation locationManipulation);

    /**
     * Gets the {@link SafeTTeleporter} this {@link MVCore} is using.
     * @return The {@link SafeTTeleporter} this {@link MVCore} is using.
     * @see SafeTTeleporter
     * @see SimpleSafeTTeleporter
     */
    SafeTTeleporter getSafeTTeleporter();

    /**
     * Sets the {@link SafeTTeleporter} this {@link MVCore} is using.
     * @param safeTTeleporter The new {@link SafeTTeleporter}.
     * @see SafeTTeleporter
     * @see SimpleSafeTTeleporter
     */
    void setSafeTTeleporter(SafeTTeleporter safeTTeleporter);

    /**
     * Gets the {@link MVConfig}.
     * @return The configuration.
     */
    MVConfig getMVConfig();
}
