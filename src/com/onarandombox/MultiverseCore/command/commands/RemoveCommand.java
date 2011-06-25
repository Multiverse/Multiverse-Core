package com.onarandombox.MultiverseCore.command.commands;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

import com.onarandombox.MultiverseCore.MultiverseCore;
import com.onarandombox.MultiverseCore.command.BaseCommand;

public class RemoveCommand extends BaseCommand {
    
    public RemoveCommand(MultiverseCore plugin) {
        super(plugin);
        this.name = "Remove World";
        this.description = "Unloads a world from Multiverse and removes it from worlds.yml, this does NOT remove the world folder.";
        this.usage = "/mvremove" + ChatColor.GREEN + " {WORLD} ";
        this.minArgs = 1;
        this.maxArgs = 1;
        this.identifiers.add("mvremove");
        this.permission = "multiverse.world.remove";
        this.requiresOp = true;
    }
    
    @Override
    public void execute(CommandSender sender, String[] args) {
        if (this.plugin.removeWorld(args[0])) {
            sender.sendMessage("World removed from config!");
        } else {
            sender.sendMessage("Error trying to remove world from config!");
        }
    }
}
