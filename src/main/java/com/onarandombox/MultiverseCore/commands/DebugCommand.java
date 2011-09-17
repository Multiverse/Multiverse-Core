package com.onarandombox.MultiverseCore.commands;

import com.onarandombox.MultiverseCore.MultiverseCore;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.permissions.PermissionDefault;

import java.util.List;
import java.util.logging.Level;

public class DebugCommand extends MultiverseCommand {

    public DebugCommand(MultiverseCore plugin) {
        super(plugin);
        this.setName("Turn Debug on/off?");
        this.setCommandUsage("/mv debug" + ChatColor.GOLD + " [1|2|3|off]");
        this.setArgRange(0, 1);
        this.addKey("mv debug");
        this.addKey("mv d");
        this.addKey("mvdebug");
        this.setPermission("multiverse.core.debug", "Spams the console a bunch.", PermissionDefault.OP);
    }

    @Override
    public void runCommand(CommandSender sender, List<String> args) {
        if (args.size() == 1) {
            if (args.get(0).equalsIgnoreCase("off")) {
                MultiverseCore.GlobalDebug = 0;
            } else {
                try {
                    int debugLevel = Integer.parseInt(args.get(0));
                    if (debugLevel > 3 || debugLevel < 0) {
                        throw new NumberFormatException();
                    }
                    MultiverseCore.GlobalDebug = debugLevel;
                } catch (NumberFormatException e) {
                    sender.sendMessage(ChatColor.RED + "Error" + ChatColor.WHITE + " setting debug level. Please use a number 0-3 " + ChatColor.AQUA + "(3 being many many messages!)");
                }

            }
        }
        this.displayDebugMode(sender);
    }

    private void displayDebugMode(CommandSender sender) {
        if (MultiverseCore.GlobalDebug == 0) {
            sender.sendMessage("Multiverse Debug mode is " + ChatColor.RED + "OFF");
        } else {
            sender.sendMessage("Multiverse Debug mode is " + ChatColor.GREEN + MultiverseCore.GlobalDebug);
            this.plugin.log(Level.FINE, "Multiverse Debug ENABLED");
        }
    }
}
