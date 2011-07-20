package com.onarandombox.MultiverseCore.commands;

import java.util.List;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.permissions.PermissionDefault;

import com.onarandombox.MultiverseCore.MultiverseCore;

public class SetSpawnCommand extends MultiverseCommand {

    public SetSpawnCommand(MultiverseCore plugin) {
        super(plugin);
        this.setName("Set World Spawn");
        this.setCommandUsage("/mvsetspawn");
        this.setArgRange(0, 0);
        this.addKey("mvsetspawn");
        this.addKey("mvss");
        this.addKey("mv set spawn");
        this.setPermission("multiverse.core.spawn.set", "Sets the spawn for the current world.", PermissionDefault.OP);
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
