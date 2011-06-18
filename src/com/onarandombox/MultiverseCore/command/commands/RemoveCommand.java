package com.onarandombox.MultiverseCore.command.commands;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

import com.onarandombox.MultiverseCore.MultiverseCore;
import com.onarandombox.MultiverseCore.command.BaseCommand;

public class RemoveCommand extends BaseCommand {
    
    public RemoveCommand(MultiverseCore plugin) {
        super(plugin);
        name = "Remove World";
        description = "Unloads a world from Multiverse and removes it from worlds.yml, this does NOT remove the world folder.";
        usage = "/mvremove" + ChatColor.GREEN + " {WORLD} ";
        minArgs = 1;
        maxArgs = 1;
        identifiers.add("mvremove");
    }
    
    @Override
    public void execute(CommandSender sender, String[] args) {
        if (plugin.removeWorld(args[0])) {
            sender.sendMessage("World removed from config!");
        } else {
            sender.sendMessage("Error trying to remove world from config!");
        }
    }
    
}
