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
        this.usage = "/mvtp" + ChatColor.GREEN + " {WORLD}";
        this.minArgs = 1;
        this.maxArgs = 1;
        this.identifiers.add("mvtp");
        this.playerTeleporter = new MVTeleport(plugin);
        this.permission = "multiverse.world.tp";
        this.requiresOp = true;
    }
    
    @Override
    public void execute(CommandSender sender, String[] args) {
        // Check if the command was sent from a Player.
        if (sender instanceof Player) {
            Player p = (Player) sender;
            Destination d = Destination.parseDestination(args[0], this.plugin);
            if (d.getType() == DestinationType.World && plugin.ph.canEnterWorld(p, plugin.getServer().getWorld(d.getName()))) {
                Location l = playerTeleporter.getSafeDestination(this.plugin.getServer().getWorld(d.getName()).getSpawnLocation());
                p.teleport(l);
            } else {
                p.sendMessage("That was not a valid world.");
            }
            
        } else {
            sender.sendMessage(IN_GAME_COMMAND_MSG);
        }
    }
    
}
