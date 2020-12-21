package com.onarandombox.MultiverseCore.commands;

import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandPermission;
import co.aikar.commands.annotation.Description;
import co.aikar.commands.annotation.Flags;
import co.aikar.commands.annotation.Subcommand;
import com.onarandombox.MultiverseCore.MultiverseCore;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

@CommandAlias("mv")
public class BedCommand extends MultiverseCommand {

    public BedCommand(MultiverseCore plugin) {
        super(plugin);
    }

    @Subcommand("bed")
    @CommandPermission("multiverse.core.bed")
    @Description("Takes your current respawn point.")
    public void onBedCommand(@NotNull @Flags("onlyself") Player player) {
        Location bedLocation = player.getBedSpawnLocation();
        if (bedLocation == null) {
            player.sendMessage("You do have a respawn point set!");
            return;
        }
        player.sendMessage((player.teleport(bedLocation))
                ? "You have been teleported to your respawn point!"
                : ChatColor.RED + "There was an error teleporting you to your respawn point.");
    }
}
