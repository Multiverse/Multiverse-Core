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
import com.onarandombox.MultiverseCore.commandtools.context.RequiredPlayer;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

@CommandAlias("mv")
public class BedCommand extends MultiverseCoreCommand {

    public BedCommand(MultiverseCore plugin) {
        super(plugin);
    }

    @Subcommand("bed")
    @CommandPermission("multiverse.core.bed.self")
    @Description("Takes your current respawn point.")
    public void onBedCommand(@NotNull Player player) {
        doBedRespawn(player, player);
    }

    @Subcommand("bed")
    @CommandPermission("multiverse.core.bed.other")
    @Syntax("[player]")
    @CommandCompletion("@players")
    @Description("Takes another player to their respawn location.")
    public void onOtherBedCommand(@NotNull CommandSender sender,

                                  @Syntax("[player]")
                                  @Description("Target player to teleport to respawn location.")
                                  @NotNull RequiredPlayer player) {

        doBedRespawn(sender, player.get());
    }

    private void doBedRespawn(@NotNull CommandSender sender,
                              @NotNull Player player) {

        Location bedLocation = player.getBedSpawnLocation();
        if (bedLocation == null) {
            sender.sendMessage(String.format("%sYou do not have a respawn point set!", ChatColor.RED));
            return;
        }
        sender.sendMessage((player.teleport(bedLocation))
                ? "You have been teleported to your respawn point!"
                : String.format("%sThere was an error teleporting you to your respawn point.", ChatColor.RED));
    }
}
