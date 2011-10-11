/******************************************************************************
 * Multiverse 2 Copyright (c) the Multiverse Team 2011.                       *
 * Multiverse 2 is licensed under the BSD License.                            *
 * For more information please check the README.md file included              *
 * with this project.                                                         *
 ******************************************************************************/

package com.onarandombox.MultiverseCore.api;

import com.fernferret.allpay.GenericBank;
import com.onarandombox.MultiverseCore.destination.DestinationFactory;
import com.onarandombox.MultiverseCore.utils.*;
import com.pneumaticraft.commandhandler.CommandHandler;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.util.config.Configuration;

/**
 * Multiverse 2 Core API
 * <p/>
 * This API contains a bunch of useful things you can get out of Multiverse in general!
 */
public interface Core {

    /**
     * Gets the Multiverse config file.
     *
     * @return The Multiverse config file.
     */
    public FileConfiguration getMVConfiguration();

    /**
     * Gets the Banking system that Multiverse-Core has hooked into.
     *
     * @return A {@link GenericBank} that can be used for payments.
     */
    public GenericBank getBank();

    /**
     * Reloads the Multiverse Configuration files:
     * worlds.yml and config.yml.
     */
    public void loadConfigs();

    /**
     * Gets the Multiverse message system. This allows you to send messages
     * to users at specified intervals.
     *
     * @return The loaded {@link MVMessaging}.
     */
    public MVMessaging getMessaging();

    /**
     * Gets the {@link MVPlayerSession} for the given player.
     * This will also create a player session if one does not exist
     * for a player.
     *
     * @param player The player's session to grab.
     *
     * @return The corresponding {@link MVPlayerSession}.
     */
    public MVPlayerSession getPlayerSession(Player player);

    /**
     * Gets the instantiated Safe-T-Teleporter for performing
     * safe teleports.
     *
     * @return A non-null {@link SafeTTeleporter}.
     */
    public SafeTTeleporter getTeleporter();

    /**
     * Multiverse uses an advanced permissions setup, this object
     * simplifies getting/setting permissions.
     *
     * @return A non-null {@link MVPermissions}.
     */
    public MVPermissions getMVPerms();

    /**
     * Multiverse uses {@link CommandHandler} to make adding and using commands
     * a piece of cake.
     *
     * @return A non-null {@link CommandHandler}.
     */
    public CommandHandler getCommandHandler();

    /**
     * Gets the factory class responsible for loading many different destinations
     * on demand.
     *
     * @return A valid {@link DestinationFactory}.
     */
    public DestinationFactory getDestFactory();

    /**
     * Gets the primary class responsible for managing Multiverse Worlds.
     *
     * @return {@link WorldManager}.
     */
    public WorldManager getMVWorldManager();
}
