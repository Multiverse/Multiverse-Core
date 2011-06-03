package com.onarandombox.MultiverseCore.command.commands;

import java.io.File;
import java.util.Arrays;
import java.util.HashSet;

import org.bukkit.ChatColor;
import org.bukkit.World.Environment;
import org.bukkit.command.CommandSender;

import com.onarandombox.MultiverseCore.MultiverseCore;
import com.onarandombox.MultiverseCore.command.BaseCommand;

public class CreateCommand extends BaseCommand {
    
    private final String envNameArray[] = { "NETHER", "NORMAL", "SKYLANDS", "SKYLAND", "HELL" };
    private final HashSet<String> envNames = new HashSet<String>(Arrays.asList(envNameArray));
    
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
        
        Environment environment = null;
        // Don't reference the enum directly as there aren't that many, and we can be more forgiving to users this way
        if (env.equalsIgnoreCase("HELL"))
            env = "NETHER";
        
        if (env.equalsIgnoreCase("SKYLAND") || env.equalsIgnoreCase("STARWARS"))
            env = "SKYLANDS";
        try {
            environment = Environment.valueOf(env);
        } catch (IllegalArgumentException e) {
            sender.sendMessage(ChatColor.RED + "Environment type " + env + " does not exist!");
            // TODO: Show the player the mvenvironments command.
            return;
        }
        
        if (hasSeed) {
            try {
                plugin.addWorld(worldName, environment, Long.parseLong(seed));
            } catch (NumberFormatException e) {
                plugin.addWorld(worldName, environment, (long) seed.hashCode());
            }
        } else {
            plugin.addWorld(worldName, environment);
        }
        return;
    }

}
