package com.onarandombox.MultiverseCore.command.commands;

import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.World.Environment;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.onarandombox.MultiverseCore.MultiverseCore;
import com.onarandombox.MultiverseCore.command.BaseCommand;

public class ListCommand extends BaseCommand {
    
    public ListCommand(MultiverseCore plugin) {
        super(plugin);
        name = "World Listing";
        description = "Returns all valid worlds";
        usage = "/mvlist";
        minArgs = 0;
        maxArgs = 0;
        identifiers.add("mvlist");
    }
    
    @Override
    public void execute(CommandSender sender, String[] args) {
        Player p = null;
        if (sender instanceof Player) {
            p = (Player) sender;
            if (!(plugin.ph.has(p, "multiverse.world.list"))) {
                sender.sendMessage("You do not have access to this command.");
                return;
            }
        }
        
        String output = ChatColor.LIGHT_PURPLE + "Worlds which you can view:\n";
        for (int i = 0; i < plugin.getServer().getWorlds().size(); i++) {
            
            World world = plugin.getServer().getWorlds().get(i);
            
            if (!(plugin.worlds.containsKey(world.getName()))) {
                continue;
            }
            if (p != null && (!plugin.ph.canEnterWorld(p, world))) {
                continue;
            }
            
            ChatColor color = ChatColor.GREEN;
            
            if (world.getEnvironment() == Environment.NETHER) {
                color = ChatColor.RED;
            } else if(world.getEnvironment() == Environment.SKYLANDS) {
                color = ChatColor.AQUA;
            } else {
                color = ChatColor.GREEN;
            }
            
            output += ChatColor.GOLD + world.getName() + ChatColor.WHITE + " - " + color + world.getEnvironment().toString() + " \n";
            
        }
        String[] response = output.split("\n");
        for (String msg : response) {
            sender.sendMessage(msg);
        }
    }
}
