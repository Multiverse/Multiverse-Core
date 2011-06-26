package com.onarandombox.MultiverseCore.command.commands;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.onarandombox.MultiverseCore.MVTeleport;
import com.onarandombox.MultiverseCore.MultiverseCore;
import com.onarandombox.MultiverseCore.command.BaseCommand;
import com.onarandombox.utils.Destination;
import com.onarandombox.utils.DestinationType;

public class TeleportCommand extends BaseCommand {
    private MVTeleport playerTeleporter;
    
    public TeleportCommand(MultiverseCore plugin) {
        super(plugin);
        this.name = "Teleport";
        this.description = "Teleports you to a different world.";
        this.usage = "/mvtp" + ChatColor.GOLD + "[PLAYER]" + ChatColor.GREEN + " {WORLD}";
        this.minArgs = 1;
        this.maxArgs = 2;
        this.identifiers.add("mvtp");
        this.playerTeleporter = new MVTeleport(plugin);
        this.permission = "multiverse.world.tp.self";
        this.requiresOp = true;
    }
    
    @Override
    public void execute(CommandSender sender, String[] args) {
        // Check if the command was sent from a Player.
        Player teleporter = null;
        Player teleportee = null;
        if (sender instanceof Player) {
            teleporter = (Player) sender;
        }
        
        String worldName;
        
        if (args.length == 2) {
            if (teleporter != null && !this.plugin.ph.hasPermission(sender, "multiverse.world.tp.other", true)) {
                sender.sendMessage("You don't have permission to teleport another player.");
                return;
            }
            teleportee = this.plugin.getServer().getPlayer(args[0]);
            if (teleportee == null) {
                sender.sendMessage("Sorry, I couldn't find player: " + args[0]);
                return;
            }
            worldName = args[1];
            
        } else {
            worldName = args[0];
            
            if (!(sender instanceof Player)) {
                sender.sendMessage("You can only teleport other players from the command line.");
                return;
            }
            teleporter = (Player) sender;
            teleportee = (Player) sender;
        }
        
        Destination d = Destination.parseDestination(worldName, this.plugin);
        if (!(d.getType() == DestinationType.World)) {
            sender.sendMessage("Multiverse does not know about this world: " + worldName);
            return;
        }
        
        if (teleporter != null && !this.plugin.ph.canEnterWorld(teleporter, this.plugin.getServer().getWorld(d.getName()))) {
            if (teleportee.equals(teleporter)) {
                teleporter.sendMessage("Doesn't look like you're allowed to go " + ChatColor.RED + "there...");
            } else {
                teleporter.sendMessage("Doesn't look like you're allowed to send " + ChatColor.GOLD + teleportee.getName() + ChatColor.WHITE + " to " + ChatColor.RED + "there...");
            }
        } else if (teleporter != null && !this.plugin.ph.canTravelFromWorld(teleporter, this.plugin.getServer().getWorld(d.getName()))) {
            if (teleportee.equals(teleporter)) {
                teleporter.sendMessage("DOH! Doesn't look like you can get to " + ChatColor.RED + d.getName() + " from " + ChatColor.GREEN + teleporter.getWorld().getName());
            } else {
                teleporter.sendMessage("DOH! Doesn't look like " + ChatColor.GREEN + teleporter.getWorld().getName() + " can get to " + ChatColor.RED + d.getName() + " from where they are...");
            }
        }
        Location l = this.playerTeleporter.getSafeDestination(this.plugin.getServer().getWorld(d.getName()).getSpawnLocation());
        teleporter.teleport(l);
    }
    
}
