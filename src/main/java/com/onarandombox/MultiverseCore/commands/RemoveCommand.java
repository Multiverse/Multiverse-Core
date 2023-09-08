/******************************************************************************
 * Multiverse 2 Copyright (c) the Multiverse Team 2011.                       *
 * Multiverse 2 is licensed under the BSD License.                            *
 * For more information please check the README.md file included              *
 * with this project.                                                         *
 ******************************************************************************/

package com.onarandombox.MultiverseCore.commands;

import com.onarandombox.MultiverseCore.MultiverseCore;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.permissions.PermissionDefault;

import java.util.List;

/**
 * Unloads a world and removes it from the config.
 */
public class RemoveCommand extends MultiverseCommand {

    public RemoveCommand(MultiverseCore plugin) {
        super(plugin);
        this.setName("Remove World");
        this.setCommandUsage("/mv remove" + ChatColor.GREEN + " {WORLD}");
        this.setArgRange(1, 1);
        this.addKey("mvremove");
        this.addKey("mv remove");
        this.addCommandExample("/mv remove " + ChatColor.GREEN + "MyWorld");
        this.setPermission("multiverse.core.remove",
                "Unloads a world from Multiverse and removes it from worlds.yml, this does NOT DELETE the world folder.", PermissionDefault.OP);
    }

    @Override
    public void runCommand(CommandSender sender, List<String> args) {
        String worldName = args.get(0);
        this.plugin.getMVWorldManager().addOrRemoveWorldSafely(worldName, "remove", () -> {
            if (this.plugin.getMVWorldManager().removeWorldFromConfig(worldName)) {
                sender.sendMessage("World removed from config!");
            } else {
                sender.sendMessage("Error trying to remove world from config!");
            }
        });
    }
}
