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
public class SilentCommand extends MultiverseCoreCommand {

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
    @Syntax("<true|false>")
    @CommandCompletion("true|false")
    @Description("Reduces the amount of startup messages.")
    public void onChangeSilentCommand(@NotNull CommandSender sender,
                                      boolean silent) {

        plugin.getMVConfig().setSilentStart(silent);
        if (!plugin.saveMVConfigs()) {
            sender.sendMessage(String.format("%sFailed to save config! Check your console for details.", ChatColor.RED));
        }

        displaySilentMode(sender);
    }

    private void displaySilentMode(CommandSender sender) {
        sender.sendMessage((plugin.getMVConfig().getSilentStart())
                ? String.format("Multiverse Silent Start mode is %sOn", ChatColor.GREEN)
                : String.format("Multiverse Silent Start mode is %sOff", ChatColor.RED));
    }
}
