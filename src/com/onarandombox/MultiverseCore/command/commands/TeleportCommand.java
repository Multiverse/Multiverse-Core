package com.onarandombox.MultiverseCore.command.commands;

import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.onarandombox.MultiverseCore.MVTeleport;
import com.onarandombox.MultiverseCore.MultiverseCore;
import com.onarandombox.utils.Destination;
import com.onarandombox.utils.DestinationType;
import com.pneumaticraft.commandhandler.Command;

public class TeleportCommand extends Command {
    private MVTeleport playerTeleporter;

    public TeleportCommand(MultiverseCore plugin) {
        super(plugin);
        this.commandName = "Teleport";
        this.commandDesc = "Teleports you to a different world.";
        this.commandUsage = "/mvtp" + ChatColor.GOLD + "[PLAYER]" + ChatColor.GREEN + " {WORLD}";
        this.minimumArgLength = 1;
        this.maximumArgLength = 2;
        this.commandKeys.add("mvtp");
        this.commandKeys.add("mv tp");
        this.playerTeleporter = new MVTeleport(plugin);
        this.permission = "multiverse.world.tp";
        this.opRequired = true;
    }

    @Override
    public void runCommand(CommandSender sender, List<String> args) {
        // Check if the command was sent from a Player.
        Player teleporter = null;
        Player teleportee = null;
        if (sender instanceof Player) {
            teleporter = (Player) sender;
        }

        String worldName;

        if (args.size() == 2) {
            if (teleporter != null && !((MultiverseCore) this.plugin).getPermissions().hasPermission(sender, "multiverse.world.tp.other", true)) {
                sender.sendMessage("You don't have permission to teleport another player. (multiverse.world.tp.other)");
                return;
            }
            teleportee = this.plugin.getServer().getPlayer(args.get(0));
            if (teleportee == null) {
                sender.sendMessage("Sorry, I couldn't find player: " + args.get(0));
                return;
            }
            worldName = args.get(1);

        } else {
            worldName = args.get(0);
            if (teleporter != null && !((MultiverseCore) this.plugin).getPermissions().hasPermission(sender, "multiverse.world.tp.self", true)) {
                sender.sendMessage("You don't have permission to teleport yourself between worlds. (multiverse.world.tp.self)");
                return;
            }

            if (!(sender instanceof Player)) {
                sender.sendMessage("You can only teleport other players from the command line.");
                return;
            }
            teleporter = (Player) sender;
            teleportee = (Player) sender;
        }

        Destination d = Destination.parseDestination(worldName, (MultiverseCore) this.plugin);
        if (!(d.getType() == DestinationType.World)) {
            sender.sendMessage("Multiverse does not know about this world: " + worldName);
            return;
        }

        if (teleporter != null && !((MultiverseCore) this.plugin).getPermissions().canEnterWorld(teleporter, this.plugin.getServer().getWorld(d.getName()))) {
            if (teleportee.equals(teleporter)) {
                teleporter.sendMessage("Doesn't look like you're allowed to go " + ChatColor.RED + "there...");
            } else {
                teleporter.sendMessage("Doesn't look like you're allowed to send " + ChatColor.GOLD + teleportee.getName() + ChatColor.WHITE + " to " + ChatColor.RED + "there...");
            }
            return;
        } else if (teleporter != null && !((MultiverseCore) this.plugin).getPermissions().canTravelFromWorld(teleporter, this.plugin.getServer().getWorld(d.getName()))) {
            if (teleportee.equals(teleporter)) {
                teleporter.sendMessage("DOH! Doesn't look like you can get to " + ChatColor.RED + d.getName() + " from " + ChatColor.GREEN + teleporter.getWorld().getName());
            } else {
                teleporter.sendMessage("DOH! Doesn't look like " + ChatColor.GREEN + teleporter.getWorld().getName() + " can get to " + ChatColor.RED + d.getName() + " from where they are...");
            }
            return;
        }
        Location l = this.playerTeleporter.getSafeDestination(this.plugin.getServer().getWorld(d.getName()).getSpawnLocation());
        teleportee.teleport(l);
    }
}
