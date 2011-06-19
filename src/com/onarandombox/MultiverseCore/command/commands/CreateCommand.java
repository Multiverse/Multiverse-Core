package com.onarandombox.MultiverseCore.command.commands;

import java.io.File;

import org.bukkit.ChatColor;
import org.bukkit.World.Environment;
import org.bukkit.command.CommandSender;

import com.onarandombox.MultiverseCore.MultiverseCore;
import com.onarandombox.MultiverseCore.command.BaseCommand;

public class CreateCommand extends BaseCommand {
    
    public CreateCommand(MultiverseCore plugin) {
        super(plugin);
        name = "Create World";
        description = "Creates a new world of the specified type";
        usage = "/mvcreate" + ChatColor.GREEN + " {NAME} {TYPE}" + ChatColor.GOLD + " [SEED]";
        minArgs = 2;
        maxArgs = 3;
        identifiers.add("mvcreate");
    }
    
    @Override
    public void execute(CommandSender sender, String[] args) {
        // TODO: Permissions, check
        
        int numOfParams = args.length;

        boolean hasSeed = numOfParams == 3;
        String worldName = args[0];
        String env = args[1];
        String seed = "";
        if(hasSeed) {
            seed = args[2];
        }
        
        if (new File(worldName).exists() || this.plugin.worlds.containsKey(worldName)) {
            sender.sendMessage(ChatColor.RED + "A Folder/World already exists with this name!");
            sender.sendMessage(ChatColor.RED + "If you are confident it is a world you can import with /mvimport");
            return;
        }
        Environment environment = plugin.getEnvFromString(env, sender);
        if(environment == null) {
            return;
        }
        sender.sendMessage(ChatColor.AQUA + "Starting world creation...");
        if (hasSeed) {
            try {
                plugin.addWorld(worldName, environment, Long.parseLong(seed));
            } catch (NumberFormatException e) {
                plugin.addWorld(worldName, environment, (long) seed.hashCode());
            }
        } else {
            plugin.addWorld(worldName, environment);
        }
        sender.sendMessage(ChatColor.GREEN + "Complete!");
        return;
    }

}
