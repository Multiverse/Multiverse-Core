package com.onarandombox.MultiverseCore.command.commands;

import java.util.List;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.onarandombox.MultiverseCore.MultiverseCore;
import com.pneumaticraft.commandhandler.Command;

public class SetSpawnCommand extends Command {

    public SetSpawnCommand(MultiverseCore plugin) {
        super(plugin);
        this.commandName = "Set World Spawn";
        this.commandDesc = "Sets the spawn for the current world.";
        this.commandUsage = "/mvsetspawn";
        this.minimumArgLength = 0;
        this.maximumArgLength = 0;
        this.commandKeys.add("mvsetspawn");
        this.commandKeys.add("mvss");
        this.commandKeys.add("mv set spawn");
        this.permission = "multiverse.world.spawn.set";
        this.opRequired = true;
    }

    @Override
    public void runCommand(CommandSender sender, List<String> args) {
        if (sender instanceof Player) {
            Player p = (Player) sender;
            Location l = p.getLocation();
            World w = p.getWorld();
            w.setSpawnLocation(l.getBlockX(), l.getBlockY(), l.getBlockZ());
            p.sendMessage(w.getName() + " - Spawn set to X: " + l.getBlockX() + "  Y: " + l.getBlockY() + " Z: " + l.getBlockZ());
        } else {
            sender.sendMessage("You cannot use this command from the console.");
        }
    }
}
