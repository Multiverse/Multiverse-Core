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
import co.aikar.commands.annotation.Description;
import co.aikar.commands.annotation.Flags;
import co.aikar.commands.annotation.Subcommand;
import co.aikar.commands.annotation.Syntax;
import com.onarandombox.MultiverseCore.MultiverseCore;
import com.onarandombox.MultiverseCore.api.MultiverseWorld;
import com.onarandombox.MultiverseCore.commandtools.contexts.PlayerWorld;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

@CommandAlias("mv")
public class CoordCommand extends MultiverseCoreCommand {

    public CoordCommand(MultiverseCore plugin) {
        super(plugin);
    }

    @Subcommand("coord|coordinate")
    @CommandPermission("multiverse.core.coord.self,multiverse.core.coord.other")
    @Syntax("[player]")
    @CommandCompletion("@players")
    @Description("Detailed information on the player's where abouts.")
    public void onCoorCommand(@NotNull CommandSender sender,

                              @Syntax("[player]")
                              @Description("Player you want coordinate info of.")
                              @NotNull
                              @Flags("other,defaultself")
                              @Conditions("selfOtherPerm:multiverse.core.coord") PlayerWorld targetPlayer) {

        Player player = targetPlayer.getPlayer();
        MultiverseWorld world = targetPlayer.getWorld();

        sender.sendMessage(String.format("%s--- Location Information %s---", ChatColor.AQUA,
                (targetPlayer.isSender(sender))
                        ? ""
                        : String.format("for %s%s%s",ChatColor.YELLOW, player.getName(), ChatColor.AQUA)));

        sender.sendMessage(String.format("%sWorld: %s%s", ChatColor.AQUA, ChatColor.WHITE,  world.getName()));
        sender.sendMessage(String.format("%sAlias: %s%s", ChatColor.AQUA, ChatColor.WHITE,  world.getColoredWorldString()));
        sender.sendMessage(String.format("%sWorld Scale: %s%s", ChatColor.AQUA, ChatColor.WHITE, world.getScaling()));
        sender.sendMessage(String.format("%sCoordinates: %s%s", ChatColor.AQUA, ChatColor.WHITE, this.plugin.getLocationManipulation().strCoords(player.getLocation())));
        sender.sendMessage(String.format("%sWorld Scale: %s%s", ChatColor.AQUA, ChatColor.WHITE, world.getScaling()));
        sender.sendMessage(String.format("%sDirection: %s%s", ChatColor.AQUA, ChatColor.WHITE, this.plugin.getLocationManipulation().getDirection(player.getLocation())));
        sender.sendMessage(String.format("%sBlock: %s%s", ChatColor.AQUA, ChatColor.WHITE, world.getCBWorld().getBlockAt(player.getLocation()).getType()));
    }
}
