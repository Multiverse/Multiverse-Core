/******************************************************************************
 * Multiverse 2 Copyright (c) the Multiverse Team 2020.                       *
 * Multiverse 2 is licensed under the BSD License.                            *
 * For more information please check the README.md file included              *
 * with this project.                                                         *
 ******************************************************************************/

package com.onarandombox.MultiverseCore.commands;

import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandPermission;
import co.aikar.commands.annotation.Description;
import co.aikar.commands.annotation.Subcommand;
import com.onarandombox.MultiverseCore.MultiverseCore;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

@CommandAlias("mv")
public class EnvironmentCommand extends MultiverseCoreCommand {

    public EnvironmentCommand(MultiverseCore plugin) {
        super(plugin);
    }

    @Subcommand("env|environments")
    @CommandPermission("multiverse.core.list.environments")
    @Description("Lists valid known environments and world types.")
    public void onEnvironmentCommand(CommandSender sender) {
        showEnvironments(sender);
        sender.sendMessage("");
        showWorldTypes(sender);
    }

    /**
     * Shows all valid known environments to a {@link CommandSender}.
     *
     * @param sender The {@link CommandSender}.
     */
    public static void showEnvironments(CommandSender sender) {
        sender.sendMessage(String.format("%sValid Environments are:", ChatColor.YELLOW));
        sender.sendMessage(String.format("%sNORMAL%s, %sNETHER %sor %sEND",
                ChatColor.GREEN, ChatColor.WHITE, ChatColor.RED, ChatColor.WHITE, ChatColor.AQUA));
    }

    /**
     * Shows all valid known world types to a {@link CommandSender}.
     *
     * @param sender The {@link CommandSender}.
     */
    public static void showWorldTypes(CommandSender sender) {
        sender.sendMessage(String.format("%sValid World Types are:", ChatColor.YELLOW));
        sender.sendMessage(String.format("%sNORMAL%s, %sFLAT%s, %sLARGEBIOMES %sor %sAMPLIFIED",
                ChatColor.GREEN, ChatColor.WHITE, ChatColor.AQUA, ChatColor.WHITE, ChatColor.RED, ChatColor.WHITE, ChatColor.GOLD));
    }
}
