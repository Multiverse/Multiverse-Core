package com.onarandombox.MultiverseCore.command.commands;

import java.util.ArrayList;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.onarandombox.MultiverseCore.MVWorld;
import com.onarandombox.MultiverseCore.MultiverseCore;
import com.onarandombox.MultiverseCore.command.BaseCommand;

public class InfoCommand extends BaseCommand {
    
    public InfoCommand(MultiverseCore plugin) {
        super(plugin);
        this.name = "World Information";
        this.description = "Returns detailed information on the world.";
        this.usage = "/mvinfo" + ChatColor.GOLD + "[WORLD]" + ChatColor.DARK_PURPLE + "<Page #>";
        this.minArgs = 0;
        this.maxArgs = 2;
        this.identifiers.add("mvinfo");
        this.permission = "multiverse.world.info";
        this.requiresOp = false;
    }
    
    @Override
    public void execute(CommandSender sender, String[] args) {
        // Check if the command was sent from a Player.
        String worldName = "";
        if (sender instanceof Player && args.length == 0) {
            worldName = ((Player) sender).getWorld().getName();
        } else if (args.length == 0) {
            sender.sendMessage("You must enter a" + ChatColor.GOLD + " world" + ChatColor.WHITE + " from the console!");
            return;
        } else {
            worldName = args[0];
        }
        if (plugin.worlds.containsKey(worldName)) {
            for (String s : buildEntireCommand(plugin.worlds.get(worldName))) {
                sender.sendMessage(s);
            }
        }
    }
    
    private String[] buildEntireCommand(MVWorld world) {
        StringBuilder sb = new StringBuilder();
        ArrayList<String[]> pagedInfo = new ArrayList<String[]>();
        String[] aPage = new String[3];
        // World Name: 1
        aPage[0] = "World: " + world.name;
        
        // World Scale: 1
        aPage[1] = "World Scale: " + world.scaling;
        
        // PVP: 1
        aPage[2] = "PVP: " + world.pvp;
        
        // This feature is not mission critical and I am spending too much time on it...
        // Stopping work on it for now --FF 20110623
        // // Animal Spawning: X
        // sb.append("Animals can spawn: ");
        // sb.append(world.animals?"Yes":"No");
        // sb.append("\n");
        // if (!world.animalList.isEmpty()) {
        // sb.append("Except: \n");
        // for (String s : world.animalList) {
        // sb.append(" - " + s + "\n");
        // }
        // }
        //
        // // Monster Spawning
        // sb.append("Monsters can spawn: ");
        // sb.append(world.monsters?"Yes":"No");
        // sb.append("\n");
        // if (!world.monsterList.isEmpty()) {
        // sb.append("Except: \n");
        // for (String s : world.monsterList) {
        // sb.append(" - " + s + "\n");
        // }
        // }
        //
        // // Whitelist
        // if (!world.playerWhitelist.isEmpty()) {
        // sb.append("Whitelisted Players: \n");
        // for (String s : world.playerWhitelist) {
        // if (!s.matches("[gG]:.+")) {
        // sb.append(" - " + s + "\n");
        // }
        // }
        // sb.append("Whitelisted Groups: \n");
        // for (String s : world.playerWhitelist) {
        // if (s.matches("[gG]:")) {
        // sb.append(" - " + s.split("[g]:")[1] + "\n");
        // }
        // }
        // }
        return aPage;
    }
    
    private ChatColor getChatColor(boolean positive) {
        return positive ? ChatColor.GREEN : ChatColor.RED;
    }
}
