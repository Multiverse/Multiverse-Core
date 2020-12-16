package com.onarandombox.MultiverseCore.commands_acf;

import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandCompletion;
import co.aikar.commands.annotation.CommandPermission;
import co.aikar.commands.annotation.Description;
import co.aikar.commands.annotation.Flags;
import co.aikar.commands.annotation.Subcommand;
import co.aikar.commands.annotation.Syntax;
import co.aikar.commands.bukkit.contexts.OnlinePlayer;
import com.onarandombox.MultiverseCore.MultiverseCore;
import com.onarandombox.MultiverseCore.api.MultiverseWorld;
import com.onarandombox.MultiverseCore.commands_helper.CommandPlayer;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

@CommandAlias("mv")
public class SpawnCommand extends MultiverseCommand {

    public SpawnCommand(MultiverseCore plugin) {
        super(plugin);
    }

    @Subcommand("spawn")
    @CommandPermission("multiverse.core.spawn.self")
    @Description("Teleports you to the Spawn Point of the world you are in.")
    public void onSelfSpawnCommand(@NotNull Player player) {
        player.sendMessage("Teleporting to this world's spawn...");
        spawnAccurately(player);
    }

    @Subcommand("spawn")
    @CommandPermission("multiverse.core.spawn.other")
    @CommandCompletion("@players")
    @Syntax("[player]")
    @Description("Teleport another player to the spawn of the world they are in.")
    public void onOtherSpawnCommand(@NotNull CommandSender sender,
                                    @NotNull CommandPlayer targetPlayer) {

        Player player = targetPlayer.getPlayer();
        spawnAccurately(player);

        String senderName = (sender instanceof ConsoleCommandSender)
                ? ChatColor.LIGHT_PURPLE + "the console"
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
