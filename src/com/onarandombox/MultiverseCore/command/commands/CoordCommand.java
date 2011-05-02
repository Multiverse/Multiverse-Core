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
        name = "Coordinates";
        description = "Returns detailed information on the Players where abouts.";
        usage = "/mvcoord";
        minArgs = 0;
        maxArgs = 0;
        identifiers.add("mvcoord");
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        // Check if the command was sent from a Player.
        if (sender instanceof Player) {
            // If this command was sent from a Player then we need to check Permissions
            if (!(plugin.ph.has(((Player) sender), "multiverse.coord"))) {
                sender.sendMessage("You do not have access to this command.");
                return;
            }
            Player p = (Player) sender;

            p.sendMessage(ChatColor.RED + "World: " + ChatColor.WHITE + p.getWorld().getName());
            p.sendMessage(ChatColor.RED + "Compression: " + ChatColor.WHITE + plugin.worlds.get(p.getWorld().getName()).compression);
            p.sendMessage(ChatColor.RED + "Coordinates: " + ChatColor.WHITE + locMan.strCoords(p.getLocation()));
            p.sendMessage(ChatColor.RED + "Direction: " + ChatColor.WHITE + locMan.getDirection(p.getLocation()));
            p.sendMessage(ChatColor.RED + "Block: " + ChatColor.WHITE + Material.getMaterial(p.getWorld().getBlockTypeIdAt(p.getLocation())));
        } else {
            sender.sendMessage("This command needs to be used from a Player.");
        }
    }
}
