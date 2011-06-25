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
        this.name = "World Listing";
        this.description = "Displays a listing of all worlds that you can enter";
        this.usage = "/mvlist";
        this.minArgs = 0;
        this.maxArgs = 0;
        this.identifiers.add("mvlist");
        this.permission = "multiverse.world.list";
        this.requiresOp = false;
    }
    
    @Override
    public void execute(CommandSender sender, String[] args) {
        //TODO: Show custom worldtypes
        Player p = null;
        if (sender instanceof Player) {
            p = (Player) sender;
        }
        
        String output = ChatColor.LIGHT_PURPLE + "Worlds which you can view:\n";
        for (World world : plugin.getServer().getWorlds()) {
            
            
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
