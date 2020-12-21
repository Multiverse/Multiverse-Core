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
import com.onarandombox.MultiverseCore.api.MultiverseWorld;
import com.onarandombox.MultiverseCore.commandTools.CommandPlayer;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

@CommandAlias("mv")
public class CoordCommand extends MultiverseCommand {

    public CoordCommand(MultiverseCore plugin) {
        super(plugin);
    }

    @Subcommand("coord|coordinate")
    @CommandPermission("multiverse.core.coord.self")
    @Description("Detailed information on the your where abouts.")
    public void onOtherCoordCommand(@NotNull Player player) {
        showCoordInfo(player, player);
    }

    @Subcommand("coord|coordinate")
    @CommandPermission("multiverse.core.coord.other")
    @Syntax("[player]")
    @CommandCompletion("@players")
    @Description("Detailed information on the another player's where abouts.")
    public void showCoordInfo(@NotNull CommandSender sender,
                              @NotNull CommandPlayer targetPlayer) {

        showCoordInfo(sender, targetPlayer.getPlayer());
    }

    private void showCoordInfo(@NotNull CommandSender sender,
                               @NotNull Player player) {

        //TODO: Possible to move to CommandContext or something?
        MultiverseWorld world = this.plugin.getMVWorldManager().getMVWorld(player.getWorld());
        if (world == null) {
            this.plugin.showNotMVWorldMessage(sender, player.getWorld().getName());
            return;
        }

        sender.sendMessage(ChatColor.AQUA + "--- Location Information ---");
        sender.sendMessage(ChatColor.AQUA + "World: " + ChatColor.WHITE + world.getName());
        sender.sendMessage(ChatColor.AQUA + "Alias: " + world.getColoredWorldString());
        sender.sendMessage(ChatColor.AQUA + "World Scale: " + ChatColor.WHITE + world.getScaling());
        sender.sendMessage(ChatColor.AQUA + "Coordinates: " + ChatColor.WHITE + this.plugin.getLocationManipulation().strCoords(player.getLocation()));
        sender.sendMessage(ChatColor.AQUA + "Direction: " + ChatColor.WHITE + this.plugin.getLocationManipulation().getDirection(player.getLocation()));
        sender.sendMessage(ChatColor.AQUA + "Block: " + ChatColor.WHITE + world.getCBWorld().getBlockAt(player.getLocation()).getType());
    }
}
