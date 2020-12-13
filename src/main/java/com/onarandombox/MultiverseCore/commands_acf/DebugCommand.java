package com.onarandombox.MultiverseCore.commands_acf;

import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandPermission;
import co.aikar.commands.annotation.Description;
import co.aikar.commands.annotation.Subcommand;
import co.aikar.commands.annotation.Syntax;
import com.dumptruckman.minecraft.util.Logging;
import com.onarandombox.MultiverseCore.MultiverseCore;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

@CommandAlias("mv")
public class DebugCommand extends MultiverseCommand {

    public DebugCommand(MultiverseCore plugin) {
        super(plugin);
    }

    @Subcommand("debug")
    @CommandPermission("multiverse.core.debug")
    @Description("Show the current debug level.")
    public void showDebugCommand(CommandSender sender) {
        displayDebugMode(sender);
    }

    @Subcommand("debug")
    @CommandPermission("multiverse.core.debug")
    @Syntax("<level>")
    @Description("Change debug level.")
    public void changeDebugCommand(CommandSender sender,  String debugLevel) {
        int parsedLevel;
        if (debugLevel.equalsIgnoreCase("off")) {
            parsedLevel = 0;
        }
        else {
            try {
                parsedLevel = Integer.parseInt(debugLevel);
                if (parsedLevel > 3 || parsedLevel < 0) {
                    throw new NumberFormatException();
                }
            } catch (NumberFormatException e) {
                sender.sendMessage(ChatColor.RED + "Error" + ChatColor.WHITE
                        + " setting debug level. Please use a number 0-3 " + ChatColor.AQUA + "(3 being many many messages!)");
                return;
            }
        }

        plugin.getMVConfig().setGlobalDebug(parsedLevel);
        if (!plugin.saveMVConfigs()) {
            sender.sendMessage(ChatColor.RED + "Error saving changes to config! See console for more info.");
        }

        this.displayDebugMode(sender);
    }

    private void displayDebugMode(CommandSender sender) {
        final int debugLevel = plugin.getMVConfig().getGlobalDebug();
        if (debugLevel == 0) {
            sender.sendMessage("Multiverse Debug mode is " + ChatColor.RED + "OFF");
        }
        else {
            sender.sendMessage("Multiverse Debug mode is " + ChatColor.GREEN + debugLevel);
            Logging.fine("Multiverse Debug ENABLED");
        }
    }
}
