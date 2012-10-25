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
import java.util.logging.Level;

/**
 * Enables debug-information.
 */
public class DebugCommand extends MultiverseCommand {

    public DebugCommand(MultiverseCore plugin) {
        super(plugin);
        this.setName("Turn Debug on/off?");
        this.setCommandUsage("/mv debug" + ChatColor.GOLD + " [1|2|3|off|silent]");
        this.setArgRange(0, 1);
        this.addKey("mv debug");
        this.addKey("mv d");
        this.addKey("mvdebug");
        this.addCommandExample("/mv debug " + ChatColor.GOLD + "2");
        this.setPermission("multiverse.core.debug", "Spams the console a bunch.", PermissionDefault.OP);
    }

    @Override
    public void runCommand(CommandSender sender, List<String> args) {
        if (args.size() == 1) {
            if (args.get(0).equalsIgnoreCase("off")) {
                plugin.getMVConfig().setGlobalDebug(0);
            } else {
                try {
                    int debugLevel = Integer.parseInt(args.get(0));
                    if (debugLevel > 3 || debugLevel < 0) {
                        throw new NumberFormatException();
                    }
                    plugin.getMVConfig().setGlobalDebug(debugLevel);
                } catch (NumberFormatException e) {
                    sender.sendMessage(ChatColor.RED + "Error" + ChatColor.WHITE
                            + " setting debug level. Please use a number 0-3 " + ChatColor.AQUA + "(3 being many many messages!)");
                }
            }
            plugin.saveMVConfigs();
        }
        this.displayDebugMode(sender);
    }

    private void displayDebugMode(CommandSender sender) {
        final int debugLevel = plugin.getMVConfig().getGlobalDebug();
        if (debugLevel == 0) {
            sender.sendMessage("Multiverse Debug mode is " + ChatColor.RED + "OFF");
        } else {
            sender.sendMessage("Multiverse Debug mode is " + ChatColor.GREEN + debugLevel);
            this.plugin.log(Level.FINE, "Multiverse Debug ENABLED");
        }
    }
}
