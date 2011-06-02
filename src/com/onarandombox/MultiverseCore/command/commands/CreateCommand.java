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
        // Syntax is s:SEED so we can see this variable seperately when spaces are allowed in worldnames
        usage = "/mvcreate" + ChatColor.GREEN + " {NAME} {TYPE}" + ChatColor.GOLD + " [s:SEED]";
        minArgs = 2;
        maxArgs = -1;
        identifiers.add("mvcreate");
    }
    
    @Override
    public void execute(CommandSender sender, String[] args) {
        // TODO: Permissions, check
        // TODO: Allow spaces in world names, currently this will catch it --FF
        
        int numOfParams = args.length;
        // We'll search for this in a minute, if it's still -1 after the search, a user did this:
        // /mvcreate "My Awesome world NETHER
        // ie. they forgot to close the "
        int envPosition = -1;
        boolean hasSeed = false;
        
        // Did the user input "s?
        if (args[0].substring(0, 1).equals("\"")) {
            // Find the magical keyword,
            for (int i = 0; i < numOfParams; i++) {
                if (args[i].substring(args[i].length() - 1, args[i].length()).equals("\"") && envPosition == -1) {
                    // We found a word that ended with a quote, the next one MUST be the environment keyword
                    envPosition = i + 1;
                }
            }
        } else {
            // There can only be one word, envPosition must be 1
            envPosition = 1;
        }
        
        if (envPosition == -1) {
            sender.sendMessage("Why are you such a failure...");
            return;
        }
        
        // If the environment position is not the last in the list, we have a seed after it:
        if (envPosition != numOfParams - 1) {
            hasSeed = true;
        }
        String worldName = parseQuotedString(args, 0, envPosition);
        String env = args[envPosition];
        // If we have a seed, parse the quotes out of it, if not, it's == ""
        String seed = hasSeed ? parseQuotedString(args, envPosition + 1, numOfParams) : "";
        
        if (args[numOfParams - 1].substring(0, 2).equalsIgnoreCase("s:")) {
            envPosition--;
            hasSeed = true;
            // Pull out the seed name, drop the "s:"
            seed = args[numOfParams - 1].split(":")[1];
        }
        sender.sendMessage("Stuff I found:");
        sender.sendMessage(ChatColor.GREEN + "worldName" + ChatColor.WHITE + worldName);
        sender.sendMessage(ChatColor.GREEN + "env      " + ChatColor.WHITE + env);
        sender.sendMessage(ChatColor.GREEN + "seed     " + ChatColor.WHITE + seed);
        
        if (args.length < 2) {
            sender.sendMessage("Not enough parameters to create a new world");
            sender.sendMessage(ChatColor.RED + "/mvcreate {WORLDNAME} {ENVIRONMENT} - Create a new World.");
            sender.sendMessage(ChatColor.RED + "Example - /mvcreate world NORMAL");
            sender.sendMessage(ChatColor.RED + "Example - /mvcreate airworld SKYLANDS");
            sender.sendMessage(ChatColor.RED + "Example - /mvcreate hellworld NETHER");
            return;
        }
        if (new File(worldName).exists() || this.plugin.worlds.containsKey(worldName)) {
            sender.sendMessage(ChatColor.RED + "A Folder/World already exists with this name!");
            sender.sendMessage(ChatColor.RED + "If you are confident it is a world you can import with /mvimport");
            return;
        }
        
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
        
        if (hasSeed) {
            // plugin.getServer().createWorld(worldName, environment, Long.parseLong(seed));
            try {
                plugin.addWorld(worldName, environment, Long.parseLong(seed));
            } catch (NumberFormatException e) {
                plugin.addWorld(worldName, environment, (long) seed.hashCode());
            }
        } else {
            // plugin.getServer().createWorld(worldName, environment);
            plugin.addWorld(worldName, environment);
        }
        return;
    }
    /**
     * Takes a string array and returns a combined string, excluding the stop position, including the start
     * 
     * @param args
     * @param start
     * @param stop
     * @return
     */
    private String parseQuotedString(String[] args, int start, int stop) {
        String returnVal = args[start];
        for (int i = start + 1; i < stop; i++) {
            returnVal += " " + args[i];
        }
        return returnVal.replace("\"", "");
    }
}
