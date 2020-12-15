package com.onarandombox.MultiverseCore.commands_acf;

import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandCompletion;
import co.aikar.commands.annotation.CommandPermission;
import co.aikar.commands.annotation.Description;
import co.aikar.commands.annotation.Flags;
import co.aikar.commands.annotation.Subcommand;
import co.aikar.commands.annotation.Syntax;
import com.onarandombox.MultiverseCore.MultiverseCore;
import com.onarandombox.MultiverseCore.api.MultiverseWorld;
import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.text.DecimalFormat;

@CommandAlias("mv")
public class CoordCommand extends MultiverseCommand {

    public CoordCommand(MultiverseCore plugin) {
        super(plugin);
    }

    @Subcommand("coord")
    @CommandPermission("multiverse.core.coord")
    @Syntax("[player]")
    @CommandCompletion("@players")
    @Description("")
    public void onCoordCommand(@NotNull CommandSender sender,
                               @NotNull @Flags("deriveFromPlayer") Player targetPlayer) {

        MultiverseWorld world = this.plugin.getMVWorldManager().getMVWorld(targetPlayer.getWorld());
        if (world == null) {
            this.plugin.showNotMVWorldMessage(sender, targetPlayer.getWorld().getName());
            return;
        }

        DecimalFormat df = new DecimalFormat();
        df.setMinimumFractionDigits(0);
        df.setMaximumFractionDigits(2);

        sender.sendMessage(ChatColor.AQUA + "--- Location Information ---");
        sender.sendMessage(ChatColor.AQUA + "World: " + ChatColor.WHITE + world.getName());
        sender.sendMessage(ChatColor.AQUA + "Alias: " + world.getColoredWorldString());
        sender.sendMessage(ChatColor.AQUA + "World Scale: " + ChatColor.WHITE + world.getScaling());
        sender.sendMessage(ChatColor.AQUA + "Coordinates: " + ChatColor.WHITE + plugin.getLocationManipulation().strCoords(targetPlayer.getLocation()));
        sender.sendMessage(ChatColor.AQUA + "Direction: " + ChatColor.WHITE + plugin.getLocationManipulation().getDirection(targetPlayer.getLocation()));
        sender.sendMessage(ChatColor.AQUA + "Block: " + ChatColor.WHITE + world.getCBWorld().getBlockAt(targetPlayer.getLocation()).getType());
    }
}
