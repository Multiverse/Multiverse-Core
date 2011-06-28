package com.onarandombox.MultiverseCore.command.commands;

import java.io.File;
import java.util.logging.Level;

import org.bukkit.ChatColor;
import org.bukkit.World.Environment;
import org.bukkit.command.CommandSender;

import com.onarandombox.MultiverseCore.MultiverseCore;
import com.onarandombox.MultiverseCore.command.BaseCommand;

public class CreateCommand extends BaseCommand {
    
    public CreateCommand(MultiverseCore plugin) {
        super(plugin);
        this.name = "Create World";
        this.description = "Creates a new world of the specified type";
        this.usage = "/mvcreate" + ChatColor.GREEN + " {NAME} {TYPE}" + ChatColor.GOLD + " -s [SEED] -g [GENERATOR[:GENID]]";
        this.minArgs = 2;
        this.maxArgs = 6;
        this.identifiers.add("mvcreate");
        this.permission = "multiverse.world.create";
        this.requiresOp = true;
        
    }
    
    @Override
    public void execute(CommandSender sender, String[] args) {
        if(args.length %2 != 0) {
            sender.sendMessage("You must preface your SEED with -s OR your GENERATOR with -g. Type /mv for help");
            return;
        }
        String worldName = args[0];
        String env = args[1];
        String seed = this.getFlag("-s", args);
        String generator = this.getFlag("-g", args);
        
        
        if (new File(worldName).exists() || this.plugin.isMVWorld(worldName)) {
            sender.sendMessage(ChatColor.RED + "A Folder/World already exists with this name!");
            sender.sendMessage(ChatColor.RED + "If you are confident it is a world you can import with /mvimport");
            return;
        }
        
        Environment environment = this.plugin.getEnvFromString(env);
        if(environment == null) {
            sender.sendMessage(ChatColor.RED + "That is not a valid environment.");
            EnvironmentCommand.showEnvironments(sender);
            return;
        }
        sender.sendMessage(ChatColor.AQUA + "Starting world creation...");
        if (this.plugin.addWorld(worldName, environment, seed, generator)) {
            sender.sendMessage(ChatColor.GREEN + "Complete!");
        } else {
            sender.sendMessage(ChatColor.RED + "FAILED.");
        }
        return;
    }
    
    
    /**
     * Returns the given flag value
     * 
     * @param flag A param flag, like -s or -g
     * @param args All arguments to search through
     * @return A string or null
     */
    private String getFlag(String flag, String[] args) {
        int i = 0;
        try {
            for (String s : args) {
                if (s.equalsIgnoreCase(flag)) {
                    this.plugin.debugLog(Level.CONFIG, args[i+1]);
                    return args[i+1];
                }
                i++;
            }
        } catch (IndexOutOfBoundsException e) {
        }
        return null;
    }
}
