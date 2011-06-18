package com.onarandombox.MultiverseCore.command.commands;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

import com.onarandombox.MultiverseCore.MultiverseCore;
import com.onarandombox.MultiverseCore.command.BaseCommand;

public class DeleteCommand extends BaseCommand {
    
    public DeleteCommand(MultiverseCore plugin) {
        super(plugin);
        name = "Delete World";
        description = "Deletes a world on your server. " + ChatColor.RED + "PERMANENTLY.";
        usage = "/mvdelete" + ChatColor.GREEN + " {WORLD} ";
        minArgs = 1;
        maxArgs = 1;
        identifiers.add("mvdelete");
    }
    
    @Override
    public void execute(CommandSender sender, String[] args) {
        if (plugin.deleteWorld(args[0])) {
            sender.sendMessage("World Deleted!");
        } else {
            sender.sendMessage("Error trying to delete World!");
        }
    }
    
}
