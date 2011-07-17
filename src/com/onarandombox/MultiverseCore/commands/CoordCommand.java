package com.onarandombox.MultiverseCore.commands;

import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.permissions.PermissionDefault;

import com.onarandombox.MultiverseCore.MultiverseCore;
import com.onarandombox.utils.LocationManipulation;

public class CoordCommand extends MultiverseCommand {

    private LocationManipulation locMan = new LocationManipulation();

    public CoordCommand(MultiverseCore plugin) {
        super(plugin);
        this.setName("Coordinates");
        this.setCommandUsage("/mvcoord");
        this.setArgRange(0, 0);
        this.addKey("mv coord");
        this.addKey("mvcoord");
        this.setPermission("multiverse.core.coord", "Returns detailed information on the Players where abouts.", PermissionDefault.OP);
    }

    @Override
    public void runCommand(CommandSender sender, List<String> args) {
        // Check if the command was sent from a Player.
        if (sender instanceof Player) {
            Player p = (Player) sender;
            p.sendMessage(ChatColor.RED + "World: " + ChatColor.WHITE + p.getWorld().getName());
            p.sendMessage(ChatColor.RED + "World Scale: " + ChatColor.WHITE + this.plugin.getMVWorld(p.getWorld().getName()).getScaling());
            p.sendMessage(ChatColor.RED + "Coordinates: " + ChatColor.WHITE + this.locMan.strCoords(p.getLocation()));
            p.sendMessage(ChatColor.RED + "Direction: " + ChatColor.WHITE + this.locMan.getDirection(p.getLocation()));
            p.sendMessage(ChatColor.RED + "Block: " + ChatColor.WHITE + Material.getMaterial(p.getWorld().getBlockTypeIdAt(p.getLocation())));
        } else {
            sender.sendMessage("This command needs to be used from a Player.");
        }
    }
}
