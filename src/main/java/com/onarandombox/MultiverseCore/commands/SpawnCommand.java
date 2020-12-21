package com.onarandombox.MultiverseCore.commands;

import co.aikar.commands.BaseCommand;
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
import org.bukkit.Location;
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
        @CommandPermission("multiverse.core.spawn.self")
        @Description("Teleports you to the Spawn Point of the world you are in.")
        public void onSelfSpawnCommand(@NotNull Player player) {
            doSpawn(player, player);
        }

        @Subcommand("spawn")
        @CommandPermission("multiverse.core.spawn.other")
        @Syntax("[player]")
        @CommandCompletion("@players")
        @Description("Teleport another player to the spawn of the world they are in.")
        public void onOtherSpawnCommand(@NotNull CommandSender sender,
                                        @NotNull CommandPlayer targetPlayer) {

            doSpawn(sender, targetPlayer.getPlayer());
        }
    }

    public class AliasSpawn extends BaseCommand {
        @CommandAlias("mvspawn")
        @CommandPermission("multiverse.core.spawn.self")
        @Description("Teleports you to the Spawn Point of the world you are in.")
        public void onSelfSpawnCommand(@NotNull Player player) {
            doSpawn(player, player);
        }

        @CommandAlias("mvspawn")
        @CommandPermission("multiverse.core.spawn.other")
        @Syntax("[player]")
        @CommandCompletion("@players")
        @Description("Teleport another player to the spawn of the world they are in.")
        public void onOtherSpawnCommand(@NotNull CommandSender sender,
                                        @NotNull CommandPlayer targetPlayer) {

            doSpawn(sender, targetPlayer.getPlayer());
        }
    }

    private void doSpawn(@NotNull CommandSender sender, @NotNull Player player) {
        spawnAccurately(player);

        if (sender.equals(player)) {
            player.sendMessage("Teleported to this world's spawn.");
            return;
        }

        String senderName = (sender instanceof ConsoleCommandSender)
                ? ChatColor.LIGHT_PURPLE + "console"
                : ChatColor.YELLOW + sender.getName();

        player.sendMessage("You were teleported by " + senderName);
    }

    private void spawnAccurately(@NotNull Player player) {
        //TODO: API should be able to take in player object directly
        MultiverseWorld mvWorld = this.plugin.getMVWorldManager().getMVWorld(player.getWorld());
        Location spawnLocation = (mvWorld != null)
                ? mvWorld.getSpawnLocation()
                : player.getWorld().getSpawnLocation();

        this.plugin.getSafeTTeleporter().safelyTeleport(player, player, spawnLocation, false);
    }
}
