/******************************************************************************
 * Multiverse 2 Copyright (c) the Multiverse Team 2020.                       *
 * Multiverse 2 is licensed under the BSD License.                            *
 * For more information please check the README.md file included              *
 * with this project.                                                         *
 ******************************************************************************/

package com.onarandombox.MultiverseCore.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.InvalidCommandArgument;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandCompletion;
import co.aikar.commands.annotation.CommandPermission;
import co.aikar.commands.annotation.Description;
import co.aikar.commands.annotation.Flags;
import co.aikar.commands.annotation.Optional;
import co.aikar.commands.annotation.Subcommand;
import co.aikar.commands.annotation.Syntax;
import com.onarandombox.MultiverseCore.MultiverseCore;
import com.onarandombox.MultiverseCore.api.BlockSafety;
import com.onarandombox.MultiverseCore.api.MultiverseWorld;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class SetSpawnCommand extends MultiverseCommand {

    public SetSpawnCommand(MultiverseCore plugin) {
        super(plugin);
    }

    @CommandAlias("mv")
    public class SetSpawn extends BaseCommand {

        @Subcommand("setspawn")
        @CommandPermission("multiverse.core.spawn.set")
        @Syntax("[world x y z [yaw pitch]]")
        @CommandCompletion("@MVWorlds @location:x @location:y @location:z @location:yaw @location:pitch")
        @Description("Sets the spawn for the current world.")
        public void onSetSpawnCommand(@NotNull CommandSender sender,
                                      @Nullable @Optional Player player,

                                      @Syntax("[world x y z [yaw pitch]]")
                                      @Description("New location of spawn.")
                                      @NotNull @Flags("other,defaultself") MultiverseWorld world,
                                      @Nullable @Optional Double x,
                                      @Nullable @Optional Double y,
                                      @Nullable @Optional Double z,
                                      @Nullable @Optional Float yaw,
                                      @Nullable @Optional Float pitch) {

            doSpawnSet(sender, world, parseLocation(player, world, x, y, z, yaw, pitch));
        }

        @Subcommand("modify set spawn")
        @CommandPermission("multiverse.core.spawn.set")
        @Syntax("[world x y z [yaw pitch]]")
        @CommandCompletion("@MVWorlds @location:x @location:y @location:z @location:yaw @location:pitch")
        @Description("Sets the spawn for the current world.")
        public void onModifySetSpawnCommand(@NotNull CommandSender sender,
                                            @Nullable @Optional Player player,

                                            @Syntax("[world x y z [yaw pitch]]")
                                            @Description("New location of spawn.")
                                            @NotNull @Flags("other,defaultself") MultiverseWorld world,
                                            @Nullable @Optional Double x,
                                            @Nullable @Optional Double y,
                                            @Nullable @Optional Double z,
                                            @Nullable @Optional Float yaw,
                                            @Nullable @Optional Float pitch) {

            doSpawnSet(sender, world, parseLocation(player, world, x, y, z, yaw, pitch));
        }
    }

    @CommandAlias("mvsetspawn")
    public class AliasSetSpawn extends BaseCommand {

        @CommandAlias("mvsetspawn")
        @CommandPermission("multiverse.core.spawn.set")
        @Syntax("[world x y z [yaw pitch]]")
        @CommandCompletion("@MVWorlds @location:x @location:y @location:z @location:yaw @location:pitch @empty")
        @Description("Sets the spawn for the current world.")
        public void onAliasSetSpawnCommand(@NotNull CommandSender sender,
                                           @Nullable @Optional Player player,

                                           @Syntax("[world x y z [yaw pitch]]")
                                           @Description("New location of spawn.")
                                           @NotNull @Flags("other,defaultself") MultiverseWorld world,
                                           @Nullable @Optional Double x,
                                           @Nullable @Optional Double y,
                                           @Nullable @Optional Double z,
                                           @Nullable @Optional Float yaw,
                                           @Nullable @Optional Float pitch) {

            doSpawnSet(sender, world, parseLocation(player, world, x, y, z, yaw, pitch));
        }
    }

    @NotNull
    private Location parseLocation(@Nullable Player player,
                                   @NotNull MultiverseWorld world,
                                   @Nullable Double x,
                                   @Nullable Double y,
                                   @Nullable Double z,
                                   @Nullable Float yaw,
                                   @Nullable Float pitch) {

        if (x == null) {
            if (player == null) {
                throw new InvalidCommandArgument("You need to specify a location from console.");
            }
            return player.getLocation();
        }
        if (y == null) {
            throw new InvalidCommandArgument("You need to specify y and z axis as well.");
        }
        if (z == null) {
            throw new InvalidCommandArgument("You need to specify z axis as well.");
        }
        if (yaw == null) {
            return new Location(world.getCBWorld(), x, y, z);
        }
        if (pitch == null) {
            throw new InvalidCommandArgument("You need to specify pitch as well.");
        }

        return new Location(world.getCBWorld(), x, y, z, yaw, pitch);
    }

    private void doSpawnSet(@NotNull CommandSender sender,
                            @NotNull MultiverseWorld world,
                            @NotNull Location location) {

        World bukkitWorld = location.getWorld();
        if (bukkitWorld == null) {
            sender.sendMessage("No world found for the spawn location your tried to set.");
            return;
        }

        world.setSpawnLocation(location);
        BlockSafety blockSafety = this.plugin.getBlockSafety();
        if (!blockSafety.playerCanSpawnHereSafely(location) && world.getAdjustSpawn()) {
            sender.sendMessage("It looks like that location would normally be unsafe. But I trust you.");
            sender.sendMessage("I'm turning off the Safe-T-Teleporter for spawns to this world.");
            sender.sendMessage(String.format("If you want turn this back on, just do %s/mv modify set adjustspawn true %s", ChatColor.AQUA, world.getName()));
            world.setAdjustSpawn(false);
        }

        sender.sendMessage((sender instanceof Player && ((Player) sender).getWorld().equals(world.getCBWorld()))
                ? "Spawn of this world is set to:"
                : String.format("Spawn for %s is set to:",  world.getColoredWorldString()));

        sender.sendMessage(plugin.getLocationManipulation().strCoords(location));

        if (!plugin.saveWorldConfig()) {
            sender.sendMessage(String.format("%sThere was an issue saving worlds.yml!  Your changes will only be temporary!", ChatColor.RED));
        }
    }
}
