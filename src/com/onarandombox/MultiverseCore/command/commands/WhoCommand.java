package com.onarandombox.MultiverseCore.command.commands;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.World.Environment;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.onarandombox.MultiverseCore.MultiverseCore;
import com.onarandombox.MultiverseCore.command.BaseCommand;

public class WhoCommand extends BaseCommand {
    
    public WhoCommand(MultiverseCore plugin) {
        super(plugin);
        name = "Who";
        description = "States who is in what world";
        usage = "/mvwho" + ChatColor.GOLD + " [WORLD]";
        minArgs = 0;
        maxArgs = 1;
        identifiers.add("mvwho");
    }
    
    @Override
    public void execute(CommandSender sender, String[] args) {
        // If this command was sent from a Player then we need to check Permissions
        if (sender instanceof Player) {
            if (!(plugin.ph.has(((Player) sender), "multiverse.who"))) {
                sender.sendMessage("You do not have access to this command.");
                return;
            }
        }
        
        List<World> worlds = new ArrayList<World>();
        
        if (args.length > 0) {
            World world = plugin.getServer().getWorld(args[0].toString());
            if (world != null) {
                worlds.add(world);
            } else {
                sender.sendMessage(ChatColor.RED + "World does not exist");
                return;
            }
        } else {
            worlds = plugin.getServer().getWorlds();
        }
        
        for (World world : worlds) {
            ChatColor color = ChatColor.BLUE;
            if (world.getEnvironment() == Environment.NETHER) {
                color = ChatColor.RED;
            } else if (world.getEnvironment() == Environment.NORMAL) {
                color = ChatColor.GREEN;
            } else if (world.getEnvironment() == Environment.SKYLANDS) {
                color = ChatColor.AQUA;
            }
            List<Player> players = world.getPlayers();
            
            String result = "";
            if (players.size() <= 0) {
                result = "Empty";
            } else {
                for (Player player : players) {
                    result += player.getName() + " ";
                }
            }
            sender.sendMessage(color + world.getName() + ChatColor.WHITE + " - " + result);
        }
        return;
    }
    
}
