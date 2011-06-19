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
        this.name = "Who";
        this.description = "States who is in what world";
        this.usage = "/mvwho" + ChatColor.GOLD + " [WORLD]";
        this.minArgs = 0;
        this.maxArgs = 1;
        this.identifiers.add("mvwho");
        this.permission = "multiverse.world.list.who";
        this.requiresOp = false;
    }
    
    @Override
    public void execute(CommandSender sender, String[] args) {
        // If this command was sent from a Player then we need to check Permissions
        Player p = null;
        if (sender instanceof Player) {
            p = (Player) sender;
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
            if (!(plugin.worlds.containsKey(world.getName()))) {
                continue;
            }
            if (p != null && (!plugin.ph.canEnterWorld(p, world))) {
                continue;
            }
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
