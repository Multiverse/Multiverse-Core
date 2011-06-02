package com.onarandombox.MultiverseCore.command.commands;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.onarandombox.MultiverseCore.MultiverseCore;
import com.onarandombox.MultiverseCore.command.BaseCommand;

public class SetSpawnCommand extends BaseCommand {
    
    public SetSpawnCommand(MultiverseCore plugin) {
        super(plugin);
        name = "Set World Spawn";
        description = "Sets the spawn for the current world.";
        usage = "/mvsetspawn";
        minArgs = 0;
        maxArgs = 0;
        identifiers.add("mvsetspawn");
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        // TODO: Permissions
        if (sender instanceof Player) {
            Player p = (Player) sender;
            Location l = p.getLocation();
            World w = p.getWorld();
            w.setSpawnLocation(l.getBlockX(), l.getBlockY(), l.getBlockZ());
            p.sendMessage(w.getName() + " - Spawn set to X: " + l.getBlockX() + "  Y: " + l.getBlockY() + " Z: " + l.getBlockZ());
        } else {
            sender.sendMessage(IN_GAME_COMMAND_MSG);
        }
        return ;
    }
    
}
