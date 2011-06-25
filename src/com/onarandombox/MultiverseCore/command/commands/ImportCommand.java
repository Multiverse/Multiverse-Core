package com.onarandombox.MultiverseCore.command.commands;

import java.io.File;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

import com.onarandombox.MultiverseCore.MultiverseCore;
import com.onarandombox.MultiverseCore.command.BaseCommand;

public class ImportCommand extends BaseCommand {

    public ImportCommand(MultiverseCore plugin) {
        super(plugin);
        this.name = "Import World";
        this.description = "Imports a new world of the specified type";
        this.usage = "/mvimport" + ChatColor.GREEN + " {NAME} {TYPE}";
        this.minArgs = 2;
        this.maxArgs = 2;
        this.identifiers.add("mvimport");
        this.permission = "multiverse.world.import";
        this.requiresOp = true;
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        String worldName = args[0];
        if (this.plugin.worlds.containsKey(worldName) && new File(worldName).exists()) {
            sender.sendMessage(ChatColor.RED + "Multiverse already knows about this world!");
            return;
        }
        
        String env = args[1];
        
        if (new File(worldName).exists() && env != null) {
            sender.sendMessage(ChatColor.AQUA + "Starting world import...");
            this.plugin.addWorld(worldName, env, "");
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
