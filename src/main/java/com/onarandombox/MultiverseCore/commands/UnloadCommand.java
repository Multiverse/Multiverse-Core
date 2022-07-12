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
 * Unloads worlds from Multiverse.
 */
public class UnloadCommand extends MultiverseCommand {

    public UnloadCommand(MultiverseCore plugin) {
        super(plugin);
        this.setName("Unload World");
        this.setCommandUsage("/mv unload" + ChatColor.GREEN + " {WORLD}");
        this.setArgRange(1, 1);
        this.addKey("mvunload");
        this.addKey("mv unload");
        this.setPermission("multiverse.core.unload",
                "Unloads a world from Multiverse. This does NOT remove the world folder. This does NOT remove it from the config file.", PermissionDefault.OP);
    }

    @Override
    public void runCommand(CommandSender sender, List<String> args) {
        String worldName = args.get(0);
        this.plugin.getMVWorldManager().addOrRemoveWorldSafely(worldName, "unload", () -> {
            if (this.plugin.getMVWorldManager().unloadWorld(worldName)) {
                Command.broadcastCommandMessage(sender, "Unloaded world '" + worldName + "'!");
            } else {
                sender.sendMessage("Error trying to unload world '" + worldName + "'!");
            }
        });
    }
}
