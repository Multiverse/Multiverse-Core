package com.onarandombox.MultiverseCore.commands;

import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

import com.onarandombox.MultiverseCore.MultiverseCore;

public class UnloadCommand extends MultiverseCommand {

    public UnloadCommand(MultiverseCore plugin) {
        super(plugin);
        this.commandName = "Unload World";
        this.commandDesc = "Unloads a world from Multiverse. This does NOT remove the world folder. This does NOT remove it from the config file.";
        this.commandUsage = "/mvunload" + ChatColor.GREEN + " {WORLD} ";
        this.minimumArgLength = 1;
        this.maximumArgLength = 1;
        this.commandKeys.add("mvunload");
        this.commandKeys.add("mv unload");
        this.permission = "multiverse.world.unload";
        this.opRequired = true;
    }

    @Override
    public void runCommand(CommandSender sender, List<String> args) {
        if (this.plugin.unloadWorld(args.get(0))) {
            sender.sendMessage("World Unloaded!");
        } else {
            sender.sendMessage("Error trying to unload world!");
        }
    }
}
