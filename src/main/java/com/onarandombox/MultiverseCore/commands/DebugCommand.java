/******************************************************************************
 * Multiverse 2 Copyright (c) the Multiverse Team 2020.                       *
 * Multiverse 2 is licensed under the BSD License.                            *
 * For more information please check the README.md file included              *
 * with this project.                                                         *
 ******************************************************************************/

package com.onarandombox.MultiverseCore.commands;

import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandCompletion;
import co.aikar.commands.annotation.CommandPermission;
import co.aikar.commands.annotation.Description;
import co.aikar.commands.annotation.Single;
import co.aikar.commands.annotation.Subcommand;
import co.aikar.commands.annotation.Syntax;
import com.dumptruckman.minecraft.util.Logging;
import com.onarandombox.MultiverseCore.MultiverseCore;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

@CommandAlias("mv")
@CommandPermission("multiverse.core.debug")
public class DebugCommand extends MultiverseCommand {

    public DebugCommand(MultiverseCore plugin) {
        super(plugin);
    }

    @Subcommand("debug")
    @Description("Show the current debug level.")
    public void onShowDebugCommand(@NotNull CommandSender sender) {
        displayDebugMode(sender);
    }

    @Subcommand("debug")
    @CommandCompletion("@toggles|@range:3")
    @Syntax("<level>")
    @Description("Change debug level.")
    public void onChangeDebugCommand(@NotNull CommandSender sender,
                                     @NotNull @Single String debugLevel) {

        int parsedLevel = parseDebugLevel(debugLevel);
        if (parsedLevel == -1) {
            sender.sendMessage(ChatColor.RED + "Error" + ChatColor.WHITE
                    + " setting debug level. Please use a number 0-3 " + ChatColor.AQUA + "(3 being many many messages!)");
            return;
        }

        this.plugin.getMVConfig().setGlobalDebug(parsedLevel);
        if (!this.plugin.saveMVConfigs()) {
            sender.sendMessage(ChatColor.RED + "Error saving changes to config! See console for more info.");
        }

        displayDebugMode(sender);
    }

    private int parseDebugLevel(@NotNull String debugLevel) {
        if (debugLevel.equalsIgnoreCase("off")) {
            return 0;
        }
        if (debugLevel.equalsIgnoreCase("on")) {
            return 1;
        }

        try {
            int parsedLevel = Integer.parseInt(debugLevel);
            return (parsedLevel > 3 || parsedLevel < 0) ? -1 : parsedLevel;
        }
        catch (NumberFormatException ignored) {
            return -1;
        }
    }

    private void displayDebugMode(@NotNull CommandSender sender) {
        final int debugLevel = this.plugin.getMVConfig().getGlobalDebug();
        if (debugLevel == 0) {
            sender.sendMessage("Multiverse Debug mode is " + ChatColor.RED + "OFF");
        }
        else {
            sender.sendMessage("Multiverse Debug mode is " + ChatColor.GREEN + debugLevel);
            Logging.fine("Multiverse Debug ENABLED");
        }
    }
}
