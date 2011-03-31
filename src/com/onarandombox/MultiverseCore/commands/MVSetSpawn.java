package com.onarandombox.MultiverseCore.commands;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.onarandombox.MultiverseCore.MVCommandHandler;
import com.onarandombox.MultiverseCore.MultiverseCore;

public class MVSetSpawn extends MVCommandHandler {

    public MVSetSpawn(MultiverseCore plugin) {
        super(plugin);
    }

    @Override
    public boolean perform(CommandSender sender, String[] args) {
        // TODO: Permissions
        if (sender instanceof Player) {
            Player p = (Player) sender;
            Location l = p.getLocation();
            World w = p.getWorld();
            w.setSpawnLocation(l.getBlockX(), l.getBlockY(), l.getBlockZ());
            p.sendMessage(w.getName() + " - Spawn set to X: " + l.getBlockX() + "  Y: " + l.getBlockY() + " Z: " + l.getBlockZ());
        } else {
            sender.sendMessage("Must be used in game");
        }
        return false;
    }

}
