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
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

@CommandAlias("mv")
public class BedCommand extends MultiverseCoreCommand {

    public BedCommand(MultiverseCore plugin) {
        super(plugin);
    }

    @Subcommand("bed")
    @CommandPermission("multiverse.core.bed")
    @Description("Takes your current respawn point.")
    public void onBedCommand(@NotNull Player player) {
        Location bedLocation = player.getBedSpawnLocation();
        if (bedLocation == null) {
            player.sendMessage(String.format("%sYou do not have a respawn point set!", ChatColor.RED));
            return;
        }
        player.sendMessage((player.teleport(bedLocation))
                ? "You have been teleported to your respawn point!"
                : String.format("%sThere was an error teleporting you to your respawn point.", ChatColor.RED));
    }
}
