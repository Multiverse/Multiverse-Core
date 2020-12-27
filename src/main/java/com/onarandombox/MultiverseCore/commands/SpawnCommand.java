/******************************************************************************
 * Multiverse 2 Copyright (c) the Multiverse Team 2020.                       *
 * Multiverse 2 is licensed under the BSD License.                            *
 * For more information please check the README.md file included              *
 * with this project.                                                         *
 ******************************************************************************/

package com.onarandombox.MultiverseCore.commands;

import co.aikar.commands.BaseCommand;
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
import com.onarandombox.MultiverseCore.commandTools.PlayerWorld;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;


public class SpawnCommand extends MultiverseCommand {

    public SpawnCommand(MultiverseCore plugin) {
        super(plugin);
    }

    @CommandAlias("mv")
    public class Spawn extends BaseCommand {
        @Subcommand("spawn")
        @CommandPermission("multiverse.core.spawn.self,multiverse.core.spawn.other")
        @Syntax("[player]")
        @CommandCompletion("@players")
        @Description("Teleport another player to the spawn of the world they are in.")
        public void onSpawnCommand(@NotNull CommandSender sender,

                                   @Syntax("[player]")
                                   @Description("Target player to teleport to spawn.")
                                   @NotNull
                                   @Flags("other|defaultself")
                                   @Conditions("selfOtherPerm:multiverse.core.spawn") PlayerWorld targetPlayer) {

            doSpawn(sender, targetPlayer.getPlayer(), targetPlayer.getWorld());
        }
    }

    public class AliasSpawn extends BaseCommand {
        @CommandAlias("mvspawn")
        @CommandPermission("multiverse.core.spawn.self,multiverse.core.spawn.other")
        @Syntax("[player]")
        @CommandCompletion("@players")
        @Description("Teleport another player to the spawn of the world they are in.")
        public void onSpawnCommand(@NotNull CommandSender sender,

                                   @Syntax("[player]")
                                   @Description("Target player to teleport to spawn.")
                                   @NotNull
                                   @Flags("other|defaultself")
                                   @Conditions("selfOtherPerm:multiverse.core.spawn") PlayerWorld targetPlayer) {

            doSpawn(sender, targetPlayer.getPlayer(), targetPlayer.getWorld());
        }
    }

    private void doSpawn(@NotNull CommandSender sender,
                         @NotNull Player player,
                         @NotNull MultiverseWorld world) {

        this.plugin.getSafeTTeleporter().safelyTeleport(player, player, world.getSpawnLocation(), false);

        if (sender.equals(player)) {
            player.sendMessage("Teleported to this world's spawn.");
            return;
        }

        String senderName = (sender instanceof ConsoleCommandSender)
                ? ChatColor.LIGHT_PURPLE + "console"
                : ChatColor.YELLOW + sender.getName();

        player.sendMessage("You were teleported by " + senderName);
    }
}
