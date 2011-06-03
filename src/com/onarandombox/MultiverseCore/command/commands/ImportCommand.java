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
        name = "Import World";
        description = "Imports a new world of the specified type";
        usage = "/mvimport" + ChatColor.GREEN + " {NAME} {TYPE}";
        minArgs = 2;
        maxArgs = 2;
        identifiers.add("mvimport");
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        String worldName = args[0];
        if (this.plugin.worlds.containsKey(worldName) && new File(worldName).exists()) {
            sender.sendMessage(ChatColor.RED + "Multiverse already knows about this world!");
            return;
        }
        
        Environment env = plugin.getEnvFromString(args[1], sender);
        
        // TODO: What if the folder does not exist but MV knows about the world?
        
        if (new File(worldName).exists() && env != null) {
            sender.sendMessage(ChatColor.AQUA + "Starting world import...");
            plugin.addWorld(worldName, env);
            sender.sendMessage(ChatColor.GREEN + "Complete!");            
            return;
        }
    }

}
