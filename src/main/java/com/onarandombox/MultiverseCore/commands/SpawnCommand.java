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
import com.onarandombox.MultiverseCore.commandtools.contexts.RequiredPlayer;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

@CommandAlias("mv")
public class SpawnCommand extends MultiverseCoreCommand {

    public SpawnCommand(MultiverseCore plugin) {
        super(plugin);
    }

    @Subcommand("spawn")
    @CommandPermission("multiverse.core.spawn.self")
    @Description("Teleport yourself to the spawn of the world.")
    public void onSpawnCommand(@NotNull Player player) {
        doSpawn(player, player);
    }

    @CommandAlias("spawn")
    @CommandPermission("multiverse.core.spawn.other")
    @Syntax("[player]")
    @CommandCompletion("@players")
    @Description("Teleport another player to the spawn of the world they are in.")
    public void onSpawnCommand(@NotNull CommandSender sender,

                               @NotNull
                               @Syntax("[player]")
                               @Description("Target player to teleport to spawn.")
                               RequiredPlayer player) {

        doSpawn(sender, player.get());
    }

    private void doSpawn(@NotNull CommandSender sender,
                         @NotNull Player player) {

        MultiverseWorld world = this.plugin.getMVWorldManager().getMVWorld(player.getWorld());
        Location spawnLocation = (world == null) ? player.getWorld().getSpawnLocation() : world.getSpawnLocation();

        this.plugin.getSafeTTeleporter().safelyTeleport(player, player, spawnLocation, false);

        if (sender.equals(player)) {
            player.sendMessage("Teleported to this world's spawn.");
            return;
        }

        String senderName = (sender instanceof ConsoleCommandSender)
                ? String.format("%sconsole%s", ChatColor.LIGHT_PURPLE, ChatColor.RESET)
                : String.format("%s%s%s", ChatColor.YELLOW, sender.getName(), ChatColor.RESET);

        player.sendMessage("You were teleported by " + senderName + "to spawn.");
    }
}
