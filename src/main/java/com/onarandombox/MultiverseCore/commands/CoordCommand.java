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
import co.aikar.commands.annotation.Conditions;
import co.aikar.commands.annotation.Default;
import co.aikar.commands.annotation.Description;
import co.aikar.commands.annotation.Flags;
import co.aikar.commands.annotation.Optional;
import co.aikar.commands.annotation.Subcommand;
import co.aikar.commands.annotation.Syntax;
import com.onarandombox.MultiverseCore.MultiverseCore;
import com.onarandombox.MultiverseCore.api.MultiverseWorld;
import com.onarandombox.MultiverseCore.commandTools.CommandPlayer;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@CommandAlias("mv")
public class CoordCommand extends MultiverseCommand {

    public CoordCommand(MultiverseCore plugin) {
        super(plugin);
    }

    @Subcommand("coord|coordinate")
    @CommandPermission("multiverse.core.coord.self,multiverse.core.coord.other")
    @Syntax("[player]")
    @CommandCompletion("@players")
    @Description("Detailed information on the player's where abouts.")
    public void onCoorCommand(@NotNull CommandSender sender,
                              @NotNull
                              @Flags("other,defaultself")
                              @Conditions("selfOtherPerm:multiverse.core.coord") CommandPlayer targetPlayer) {

        Player player = targetPlayer.getPlayer();
        MultiverseWorld world = targetPlayer.getWorld();

        sender.sendMessage(ChatColor.AQUA + "--- Location Information "
                + ((targetPlayer.isSender(sender))
                ? "---"
                : "for " + ChatColor.YELLOW + player.getName() + ChatColor.AQUA + " ---"));

        sender.sendMessage(ChatColor.AQUA + "World: " + ChatColor.WHITE + world.getName());
        sender.sendMessage(ChatColor.AQUA + "Alias: " + world.getColoredWorldString());
        sender.sendMessage(ChatColor.AQUA + "World Scale: " + ChatColor.WHITE + world.getScaling());
        sender.sendMessage(ChatColor.AQUA + "Coordinates: " + ChatColor.WHITE + this.plugin.getLocationManipulation().strCoords(player.getLocation()));
        sender.sendMessage(ChatColor.AQUA + "Direction: " + ChatColor.WHITE + this.plugin.getLocationManipulation().getDirection(player.getLocation()));
        sender.sendMessage(ChatColor.AQUA + "Block: " + ChatColor.WHITE + world.getCBWorld().getBlockAt(player.getLocation()).getType());
    }
}
