package com.onarandombox.MultiverseCore.commands;

import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.permissions.PermissionDefault;

import com.onarandombox.MultiverseCore.MVWorld;
import com.onarandombox.MultiverseCore.MultiverseCore;
import com.onarandombox.utils.LocationManipulation;

public class CoordCommand extends MultiverseCommand {

    private LocationManipulation locMan = new LocationManipulation();

    public CoordCommand(MultiverseCore plugin) {
        super(plugin);
        this.setName("Coordinates");
        this.setCommandUsage("/mv coord");
        this.setArgRange(0, 0);
        this.addKey("mv coord");
        this.addKey("mvcoord");
        this.addKey("mvco");
        this.setPermission("multiverse.core.coord", "Returns detailed information on the Players where abouts.", PermissionDefault.OP);
    }

    @Override
    public void runCommand(CommandSender sender, List<String> args) {
        // Check if the command was sent from a Player.
        if (sender instanceof Player) {
            Player p = (Player) sender;
            World world = p.getWorld();
            
            if(!this.plugin.isMVWorld(world.getName())) {
                this.plugin.showNotMVWorldMessage(sender, world.getName());
                return;
            }
            
            MVWorld mvworld = this.plugin.getMVWorld(world.getName());
            p.sendMessage(ChatColor.AQUA + "--- World Information ---");
            p.sendMessage(ChatColor.AQUA + "World: " + ChatColor.WHITE + world.getName());
            p.sendMessage(ChatColor.AQUA + "Alias: " + mvworld.getColoredWorldString());
            p.sendMessage(ChatColor.AQUA + "World Scale: " + ChatColor.WHITE + mvworld.getScaling());
            p.sendMessage(ChatColor.AQUA + "Coordinates: " + ChatColor.WHITE + this.locMan.strCoords(p.getLocation()));
            p.sendMessage(ChatColor.AQUA + "Direction: " + ChatColor.WHITE + this.locMan.getDirection(p.getLocation()));
            p.sendMessage(ChatColor.AQUA + "Block: " + ChatColor.WHITE + Material.getMaterial(world.getBlockTypeIdAt(p.getLocation())));
        } else {
            sender.sendMessage("This command needs to be used from a Player.");
        }
    }
}
