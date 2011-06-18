package com.onarandombox.MultiverseCore.command.commands;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

import com.onarandombox.MultiverseCore.MultiverseCore;
import com.onarandombox.MultiverseCore.command.BaseCommand;

public class UnloadCommand extends BaseCommand {
    
    public UnloadCommand(MultiverseCore plugin) {
        super(plugin);
        name = "Unload World";
        description = "Unloads a world from Multiverse. This does NOT remove the world folder. This does NOT remove it from the config file.";
        usage = "/mvunload" + ChatColor.GREEN + " {WORLD} ";
        minArgs = 1;
        maxArgs = 1;
        identifiers.add("mvunload");
    }
    
    @Override
    public void execute(CommandSender sender, String[] args) {
        if (plugin.unloadWorld(args[0])) {
            sender.sendMessage("World Unloaded!");
        } else {
            sender.sendMessage("Error trying to unload world!");
        }
    }
    
}
