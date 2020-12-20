package com.onarandombox.MultiverseCore.commands_acf;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandCompletion;
import co.aikar.commands.annotation.CommandPermission;
import co.aikar.commands.annotation.Description;
import co.aikar.commands.annotation.Flags;
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

//TODO: Location tab complete doesnt work.
public class SetSpawnCommand extends MultiverseCommand {

    public SetSpawnCommand(MultiverseCore plugin) {
        super(plugin);
    }

    @CommandAlias("mv")
    public class SetSpawn extends BaseCommand {

        @Subcommand("setspawn|")
        @CommandPermission("multiverse.core.spawn.set")
        @Syntax("[world x y z [yaw pitch]]")
        @CommandCompletion("@MVWorlds")
        @Description("Sets the spawn for the current world.")
        public void onSetSpawnCommand(@NotNull CommandSender sender,
                                      @NotNull @Flags("other,defaultself,fallbackself") Location location) {

            doSpawnSet(sender, location);
        }

        @Subcommand("modify set spawn")
        @CommandPermission("multiverse.core.spawn.set")
        @Syntax("[world x y z [yaw pitch]]")
        @CommandCompletion("@MVWorlds")
        @Description("Sets the spawn for the current world.")
        public void onModifySetSpawnCommand(@NotNull CommandSender sender,
                                      @NotNull @Flags("other,defaultself,fallbackself") Location location) {

            doSpawnSet(sender, location);
        }
    }

    @CommandAlias("mvm")
    public class AliasModifySetSpawn extends BaseCommand {

        @Subcommand("set spawn")
        @CommandPermission("multiverse.core.spawn.set")
        @Syntax("[world x y z [yaw pitch]]")
        @CommandCompletion("@MVWorlds")
        @Description("Sets the spawn for the current world.")
        public void onModifySetSpawnCommand(@NotNull CommandSender sender,
                                            @NotNull @Flags("other,defaultself,fallbackself") Location location) {

            doSpawnSet(sender, location);
        }
    }

    public class AliasSetSpawn extends BaseCommand {

        @CommandAlias("mvsetspawn")
        @CommandPermission("multiverse.core.spawn.set")
        @Syntax("[world x y z [yaw pitch]]")
        @CommandCompletion("@MVWorlds")
        @Description("Sets the spawn for the current world.")
        public void onAliasSetSpawnCommand(@NotNull CommandSender sender,
                                           @NotNull @Flags("other,defaultself,fallbackself") Location location) {

            doSpawnSet(sender, location);
        }
    }

    private void doSpawnSet(@NotNull CommandSender sender, @NotNull Location location) {
        World bukkitWorld = location.getWorld();
        if (bukkitWorld == null) {
            sender.sendMessage("No world found for the spawn location your tried to set.");
            return;
        }

        MultiverseWorld world = this.plugin.getMVWorldManager().getMVWorld(bukkitWorld);
        if (world == null) {
            bukkitWorld.setSpawnLocation(location.getBlockX(), location.getBlockY(), location.getBlockZ());
            sender.sendMessage("Multiverse does not know about this world, only X,Y and Z set.");
            sender.sendMessage("Please import it (see /mv import) to set the spawn fully with Pitch and Yaw.");
            return;
        }

        world.setSpawnLocation(location);
        BlockSafety blockSafety = this.plugin.getBlockSafety();
        if (!blockSafety.playerCanSpawnHereSafely(location) && world.getAdjustSpawn()) {
            sender.sendMessage("It looks like that location would normally be unsafe. But I trust you.");
            sender.sendMessage("I'm turning off the Safe-T-Teleporter for spawns to this world.");
            sender.sendMessage("If you want turn this back on, just do " + ChatColor.AQUA + "/mvm set adjustspawn true " + world.getName());
            world.setAdjustSpawn(false);
        }

        sender.sendMessage((sender instanceof Player && ((Player) sender).getWorld().equals(world.getCBWorld()))
                ? "Spawn of this world is set to:"
                : "Spawn for " + world.getColoredWorldString() + " is set to:");

        sender.sendMessage(plugin.getLocationManipulation().strCoords(location));

        if (!plugin.saveWorldConfig()) {
            sender.sendMessage(ChatColor.RED + "There was an issue saving worlds.yml!  Your changes will only be temporary!");
        }
    }


}
