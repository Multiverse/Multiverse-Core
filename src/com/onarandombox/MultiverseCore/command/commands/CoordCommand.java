package com.onarandombox.MultiverseCore.command.commands;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.onarandombox.MultiverseCore.MultiverseCore;
import com.onarandombox.MultiverseCore.command.BaseCommand;
import com.onarandombox.utils.LocationManipulation;

public class CoordCommand extends BaseCommand {

    private LocationManipulation locMan = new LocationManipulation();

    public CoordCommand(MultiverseCore plugin) {
        super(plugin);
        this.name = "Coordinates";
        this.description = "Returns detailed information on the Players where abouts.";
        this.usage = "/mvcoord";
        this.minArgs = 0;
        this.maxArgs = 0;
        this.identifiers.add("mvcoord");
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        // Check if the command was sent from a Player.
        if (sender instanceof Player) {
        	Player p = (Player) sender;
            p.sendMessage(ChatColor.RED + "World: " + ChatColor.WHITE + p.getWorld().getName());
            p.sendMessage(ChatColor.RED + "World Scale: " + ChatColor.WHITE + this.plugin.getMVWorld(p.getWorld().getName()).scaling);
            p.sendMessage(ChatColor.RED + "Coordinates: " + ChatColor.WHITE + this.locMan.strCoords(p.getLocation()));
            p.sendMessage(ChatColor.RED + "Direction: " + ChatColor.WHITE + this.locMan.getDirection(p.getLocation()));
            p.sendMessage(ChatColor.RED + "Block: " + ChatColor.WHITE + Material.getMaterial(p.getWorld().getBlockTypeIdAt(p.getLocation())));
        } else {
            sender.sendMessage("This command needs to be used from a Player.");
        }
    }
}
