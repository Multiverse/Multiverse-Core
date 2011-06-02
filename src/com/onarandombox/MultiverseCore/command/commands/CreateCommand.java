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
        // Syntax is s:SEED so we can see this variable seperately when spaces are allowed in worldnames
        usage = "/mvcoord" + ChatColor.GREEN + "{NAME} {TYPE}" + ChatColor.GOLD + " [s:SEED]";
        minArgs = 2;
        maxArgs = 3;
        identifiers.add("mvcoord");
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        // TODO: Permissions, check
        // TODO: Allow spaces in world names, currently this will catch it --FF
        
        int numOfParams = args.length;
        // By default the environment will be located at the end, unless there is a seed
        int envPosition = numOfParams - 1;
        boolean hasSeed = false;
        String seed = "";
        if(args[numOfParams - 1].substring(0, 2).equalsIgnoreCase("s:")) {
            envPosition--;
            hasSeed = true;
            // Pull out the seed name, drop the "s:"
            seed = args[numOfParams - 1].split(":")[1];
        }
        String env = args[envPosition];
        String worldName = args[0];
        for(int i = 1; i < envPosition; i++) {
            worldName += " " + args[i];
        }
        
        if (args.length < 2) {
            sender.sendMessage("Not enough parameters to create a new world");
            sender.sendMessage(ChatColor.RED + "/mvcreate {WORLDNAME} {ENVIRONMENT} - Create a new World.");
            sender.sendMessage(ChatColor.RED + "Example - /mvcreate world NORMAL");
            sender.sendMessage(ChatColor.RED + "Example - /mvcreate airworld SKYLANDS");
            sender.sendMessage(ChatColor.RED + "Example - /mvcreate hellworld NETHER");
            return;
        }
        if (new File(args[0]).exists() || this.plugin.worlds.containsKey(args[0])) {
            sender.sendMessage(ChatColor.RED + "A Folder/World already exists with this name!");
            sender.sendMessage(ChatColor.RED + "If you are confident it is a world you can import with /mvimport");
            return;
        }
        // String name = args[0].toString();
        
        Environment environment = null;
        // Don't reference the enum directly as there aren't that many, and we can be more forgiving to users this way
        if (env.equalsIgnoreCase("NETHER") || env.equalsIgnoreCase("HELL"))
            environment = Environment.NETHER;

        if (env.equalsIgnoreCase("NORMAL"))
            environment = Environment.NORMAL;
        
        if (env.equalsIgnoreCase("SKYLANDS") || env.equalsIgnoreCase("SKYLAND"))
            environment = Environment.SKYLANDS;

        if (environment == null) {
            sender.sendMessage(ChatColor.RED + "Environment type " + env + " does not exist!");
            return;
        }
        
        if(hasSeed) {
            plugin.getServer().createWorld(worldName, environment, Long.parseLong(seed));
            plugin.addWorld(worldName, environment, Long.parseLong(seed));
        } else {
            plugin.getServer().createWorld(worldName, environment);
            plugin.addWorld(worldName, environment);
        }
        return;
    }

}
