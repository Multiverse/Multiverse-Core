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
 * Enables debug-information.
 */
public class SilentCommand extends MultiverseCommand {

    public SilentCommand(MultiverseCore plugin) {
        super(plugin);
        this.setName("Turn silent mode on/off?");
        this.setCommandUsage("/mv silent" + ChatColor.GOLD + " [true|false|on|off]");
        this.setArgRange(0, 1);
        this.addKey("mv silent");
        this.addKey("mvsilent");
        this.addCommandExample("/mv silent " + ChatColor.GOLD + "true");
        this.setPermission("multiverse.core.silent", "Reduces the amount of startup messages.", PermissionDefault.OP);
    }

    @Override
    public void runCommand(CommandSender sender, List<String> args) {
        if (args.size() == 1) {
            if (args.get(0).equalsIgnoreCase("on")) {
                args.set(0, "true");
            }
            plugin.getMVConfig().setSilentStart(Boolean.valueOf(args.get(0)));
            plugin.saveMVConfigs();
        }
        this.displaySilentMode(sender);
    }

    private void displaySilentMode(CommandSender sender) {
        if (plugin.getMVConfig().getSilentStart()) {
            sender.sendMessage("Multiverse Silent Start mode is " + ChatColor.GREEN + "ON");
        } else {
            sender.sendMessage("Multiverse Silent Start mode is " + ChatColor.RED + "OFF");
        }
    }
}
