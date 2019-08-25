/******************************************************************************
 * Multiverse 2 Copyright (c) the Multiverse Team 2011.                       *
 * Multiverse 2 is licensed under the BSD License.                            *
 * For more information please check the README.md file included              *
 * with this project.                                                         *
 ******************************************************************************/

package com.onarandombox.MultiverseCore.api;

import buscript.Buscript;
import com.onarandombox.MultiverseCore.destination.DestinationFactory;
import com.onarandombox.MultiverseCore.utils.AnchorManager;
import com.onarandombox.MultiverseCore.utils.MVEconomist;
import com.onarandombox.MultiverseCore.utils.MVPermissions;
import com.onarandombox.MultiverseCore.utils.MVPlayerSession;
import com.onarandombox.MultiverseCore.utils.SimpleBlockSafety;
import com.onarandombox.MultiverseCore.utils.SimpleLocationManipulation;
import com.onarandombox.MultiverseCore.utils.SimpleSafeTTeleporter;
import com.onarandombox.MultiverseCore.utils.VaultHandler;
import com.pneumaticraft.commandhandler.CommandHandler;
import org.bukkit.entity.Player;

/**
 * Multiverse 2 Core API
 * <p>
 * This API contains a bunch of useful things you can get out of Multiverse in general!
 */
public interface Core {

    /**
     * Returns the Vault handler used by Multiverse.  The returned object will have all methods necessary for
     * interfacing with Vault.
     *
     * @return the Vault handler for Multiverse.
     * @deprecated we are now using {@link #getEconomist()} for all economy needs.
     */
    @Deprecated
    VaultHandler getVaultHandler();

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
     * Gets the Multiverse message system. This allows you to send messages
     * to users at specified intervals.
     *
     * @return The loaded {@link MultiverseMessaging}.
     */
    MultiverseMessaging getMessaging();

    /**
     * Gets the {@link MVPlayerSession} for the given player.
     * This will also create a player session if one does not exist
     * for a player.
     *
     * @param player The player's session to grab.
     *
     * @return The corresponding {@link MVPlayerSession}.
     */
    MVPlayerSession getPlayerSession(Player player);

    /**
     * Multiverse uses an advanced permissions setup, this object
     * simplifies getting/setting permissions.
     *
     * @return A non-null {@link MVPermissions}.
     */
    MVPermissions getMVPerms();

    /**
     * Multiverse uses {@link CommandHandler} to make adding and using commands
     * a piece of cake.
     *
     * @return A non-null {@link CommandHandler}.
     */
    CommandHandler getCommandHandler();

    /**
     * Gets the factory class responsible for loading many different destinations
     * on demand.
     *
     * @return A valid {@link DestinationFactory}.
     */
    DestinationFactory getDestFactory();

    /**
     * Gets the primary class responsible for managing Multiverse Worlds.
     *
     * @return {@link MVWorldManager}.
     */
    MVWorldManager getMVWorldManager();

    /**
     * Saves all configs.
     *
     * @return Whether the config was successfully saved
     */
    boolean saveMVConfigs();

    /**
     * Gets the {@link AnchorManager}.
     *
     * @return The {@link AnchorManager}
     */
    AnchorManager getAnchorManager();

    /**
     * Used by queued commands to regenerate a world on a delay.
     *
     * @param name Name of the world to regenerate
     * @param useNewSeed If a new seed should be used
     * @param randomSeed IF the new seed should be random
     * @param seed The seed of the world.
     *
     * @return True if success, false if fail.
     *
     * @deprecated Use {@link MVWorldManager#regenWorld(String, boolean, boolean, String)} instead.
     */
    @Deprecated
    Boolean regenWorld(String name, Boolean useNewSeed, Boolean randomSeed, String seed);

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
     * Parse the Authors Array into a readable String with ',' and 'and'.
     *
     * @return The readable authors-{@link String}
     */
    String getAuthors();

    /**
     * Gets the {@link BlockSafety} this {@link Core} is using.
     * @return The {@link BlockSafety} this {@link Core} is using.
     * @see BlockSafety
     * @see SimpleBlockSafety
     */
    BlockSafety getBlockSafety();

    /**
     * Sets the {@link BlockSafety} this {@link Core} is using.
     * @param blockSafety The new {@link BlockSafety}.
     * @see BlockSafety
     * @see SimpleBlockSafety
     */
    void setBlockSafety(BlockSafety blockSafety);

    /**
     * Gets the {@link LocationManipulation} this {@link Core} is using.
     * @return The {@link LocationManipulation} this {@link Core} is using.
     * @see LocationManipulation
     * @see SimpleLocationManipulation
     */
    LocationManipulation getLocationManipulation();

    /**
     * Sets the {@link LocationManipulation} this {@link Core} is using.
     * @param locationManipulation The new {@link LocationManipulation}.
     * @see LocationManipulation
     * @see SimpleLocationManipulation
     */
    void setLocationManipulation(LocationManipulation locationManipulation);

    /**
     * Gets the {@link SafeTTeleporter} this {@link Core} is using.
     * @return The {@link SafeTTeleporter} this {@link Core} is using.
     * @see SafeTTeleporter
     * @see SimpleSafeTTeleporter
     */
    SafeTTeleporter getSafeTTeleporter();

    /**
     * Sets the {@link SafeTTeleporter} this {@link Core} is using.
     * @param safeTTeleporter The new {@link SafeTTeleporter}.
     * @see SafeTTeleporter
     * @see SimpleSafeTTeleporter
     */
    void setSafeTTeleporter(SafeTTeleporter safeTTeleporter);

    /**
     * Gets the {@link MultiverseCoreConfig}.
     * @return The configuration.
     */
    MultiverseCoreConfig getMVConfig();

    /**
     * Gets the buscript object for Multiverse.  This is what handles Javascript processing.
     *
     * @return The Multiverse buscript object.
     */
    Buscript getScriptAPI();
}
