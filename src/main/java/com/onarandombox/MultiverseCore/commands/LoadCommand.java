/******************************************************************************
 * Multiverse 2 Copyright (c) the Multiverse Team 2011.                       *
 * Multiverse 2 is licensed under the BSD License.                            *
 * For more information please check the README.md file included              *
 * with this project.                                                         *
 ******************************************************************************/

package com.onarandombox.MultiverseCore.commands;

import com.onarandombox.MultiverseCore.MultiverseCore;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.permissions.PermissionDefault;

import java.util.List;

/**
 * Loads a world into Multiverse.
 */
public class LoadCommand extends MultiverseCommand {

    public LoadCommand(MultiverseCore plugin) {
        super(plugin);
        this.setName("Load World");
        this.setCommandUsage("/mv load" + ChatColor.GREEN + " {WORLD}");
        this.setArgRange(1, 1);
        this.addKey("mvload");
        this.addKey("mv load");
        this.addCommandExample("/mv load " + ChatColor.GREEN + "MyUnloadedWorld");
        this.setPermission("multiverse.core.load", "Loads a world into Multiverse.", PermissionDefault.OP);
    }

    @Override
    public void runCommand(CommandSender sender, List<String> args) {
        String worldName = args.get(0);
        this.plugin.getMVWorldManager().addOrRemoveWorldSafely(worldName, "load", () -> {
            if (this.plugin.getMVWorldManager().loadWorld(worldName)) {
                Command.broadcastCommandMessage(sender, "Loaded world '" + worldName + "'!");
            } else {
                sender.sendMessage("Error trying to load world '" + worldName + "'!");
            }
        });
    }
}
