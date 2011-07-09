package com.onarandombox.MultiverseCore.command.commands;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

import com.onarandombox.MultiverseCore.MultiverseCore;
import com.onarandombox.MultiverseCore.command.BaseCommand;

public class UnloadCommand extends BaseCommand {

    public UnloadCommand(MultiverseCore plugin) {
        super(plugin);
        this.name = "Unload World";
        this.description = "Unloads a world from Multiverse. This does NOT remove the world folder. This does NOT remove it from the config file.";
        this.usage = "/mvunload" + ChatColor.GREEN + " {WORLD} ";
        this.minArgs = 1;
        this.maxArgs = 1;
        this.identifiers.add("mvunload");
        this.permission = "multiverse.world.unload";
        this.requiresOp = true;
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (this.plugin.unloadWorld(args[0])) {
            sender.sendMessage("World Unloaded!");
        } else {
            sender.sendMessage("Error trying to unload world!");
        }
    }
}
