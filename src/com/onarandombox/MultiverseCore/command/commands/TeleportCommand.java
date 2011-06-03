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
        name = "Teleport";
        description = "Teleports you to a different world.";
        usage = "/mvtp" + ChatColor.GREEN + " {WORLD}";
        minArgs = 1;
        maxArgs = 1;
        identifiers.add("mvtp");
        playerTeleporter = new MVTeleport(plugin);
    }
    
    @Override
    public void execute(CommandSender sender, String[] args) {
        // Check if the command was sent from a Player.
        if (sender instanceof Player) {
            Player p = (Player) sender;
            // If this command was sent from a Player then we need to check Permissions
            if (!(plugin.ph.has((p), "multiverse.tp"))) {
                p.sendMessage("You do not have access to this command.");
                return;
            }
            Destination d = Destination.parseDestination(args[0]);
            // TODO: I'd like to find a way to do these next bits inside Destination, so we're always valid --FF
            // TODO: Support portals, but I didn't see the portals list --FF
            if (this.plugin.worlds.containsKey(d.getName())) {
                if (d.getType() == DestinationType.World) {
                    Location l = playerTeleporter.getSafeDestination(plugin.getServer().getWorld(d.getName()).getSpawnLocation());
                    p.teleport(l);
                }
            } else {
                p.sendMessage("That was not a valid world (portals aren't yet supported)");
            }
            
        } else {
            sender.sendMessage(IN_GAME_COMMAND_MSG);
        }
    }
    
}
