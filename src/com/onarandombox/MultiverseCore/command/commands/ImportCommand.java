package com.onarandombox.MultiverseCore.command.commands;

import java.io.File;

import org.bukkit.ChatColor;
import org.bukkit.World.Environment;
import org.bukkit.command.CommandSender;

import com.onarandombox.MultiverseCore.MultiverseCore;
import com.onarandombox.MultiverseCore.command.BaseCommand;

public class ImportCommand extends BaseCommand {

    public ImportCommand(MultiverseCore plugin) {
        super(plugin);
        this.name = "Import World";
        this.description = "Imports a new world of the specified type";
        this.usage = "/mvimport" + ChatColor.GREEN + " {NAME} {ENV} " + ChatColor.GOLD + "[GENERATOR[:ID]]";
        this.minArgs = 2;
        this.maxArgs = 3;
        this.identifiers.add("mvimport");
        this.permission = "multiverse.world.import";
        this.requiresOp = true;
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        String worldName = args[0];
        if (this.plugin.isMVWorld(worldName) && new File(worldName).exists()) {
            sender.sendMessage(ChatColor.RED + "Multiverse already knows about this world!");
            return;
        }
        
        String generator = null;
        if(args.length == 3) {
            generator = args[2];
        }
        
        String env = args[1];
        Environment environment = this.plugin.getEnvFromString(env);
        if(environment == null) {
            sender.sendMessage(ChatColor.RED + "That is not a valid environment.");
            EnvironmentCommand.showEnvironments(sender);
            return;
        }
        
        if (new File(worldName).exists() && env != null) {
            sender.sendMessage(ChatColor.AQUA + "Starting world import...");
            this.plugin.addWorld(worldName, environment, null, generator);
            sender.sendMessage(ChatColor.GREEN + "Complete!");
            return;
        } else if(env == null) {
            sender.sendMessage(ChatColor.RED + "FAILED.");
            sender.sendMessage("That world type did not exist.");
            sender.sendMessage("For a list of available world types, type: /mvenv");
        } else {
            sender.sendMessage(ChatColor.RED + "FAILED.");
            sender.sendMessage("That world folder does not exist...");
        }
    }

}
