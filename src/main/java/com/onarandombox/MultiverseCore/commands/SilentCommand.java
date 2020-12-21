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
import co.aikar.commands.annotation.Subcommand;
import co.aikar.commands.annotation.Syntax;
import com.onarandombox.MultiverseCore.MultiverseCore;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

@CommandAlias("mv")
public class SilentCommand extends MultiverseCommand {

    public SilentCommand(MultiverseCore plugin) {
        super(plugin);
    }

    @Subcommand("silent")
    @CommandPermission("multiverse.core.silent")
    @Description("Show current state of silent mode.")
    public void onShowSilentCommand(@NotNull CommandSender sender) {
        displaySilentMode(sender);
    }

    @Subcommand("silent")
    @CommandPermission("multiverse.core.silent")
    @Syntax("<true|false|on|off>")
    @CommandCompletion("true|false|on|off")
    @Description("Reduces the amount of startup messages.")
    public void onChangeSilentCommand(@NotNull CommandSender sender,
                                      boolean silent) {

        plugin.getMVConfig().setSilentStart(silent);
        if (!plugin.saveMVConfigs()) {
            sender.sendMessage(ChatColor.RED + "Failed to save config! Check your console for details.");
        }

        displaySilentMode(sender);
    }

    private void displaySilentMode(CommandSender sender) {
        sender.sendMessage((plugin.getMVConfig().getSilentStart())
                ? "Multiverse Silent Start mode is " + ChatColor.GREEN + "ON"
                : "Multiverse Silent Start mode is " + ChatColor.RED + "OFF");
    }
}
