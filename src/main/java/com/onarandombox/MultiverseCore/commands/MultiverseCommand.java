/******************************************************************************
 * Multiverse 2 Copyright (c) the Multiverse Team 2011.                       *
 * Multiverse 2 is licensed under the BSD License.                            *
 * For more information please check the README.md file included              *
 * with this project.                                                         *
 ******************************************************************************/

package com.onarandombox.MultiverseCore.commands;

import com.onarandombox.MultiverseCore.MultiverseCore;
import com.pneumaticraft.commandhandler.Command;
import org.bukkit.command.CommandSender;

import java.util.List;

/**
 * A generic Multiverse-command.
 */
public abstract class MultiverseCommand extends Command {

    /**
     * The reference to the core.
     */
    protected MultiverseCore plugin;

    public MultiverseCommand(MultiverseCore plugin) {
        super(plugin);
        this.plugin = plugin;
    }

    @Override
    public abstract void runCommand(CommandSender sender, List<String> args);

}
