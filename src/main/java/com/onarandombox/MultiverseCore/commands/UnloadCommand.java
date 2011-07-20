package com.onarandombox.MultiverseCore.commands;

import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.permissions.PermissionDefault;

import com.onarandombox.MultiverseCore.MultiverseCore;

public class UnloadCommand extends MultiverseCommand {

    public UnloadCommand(MultiverseCore plugin) {
        super(plugin);
        this.setName("Unload World");
        this.setCommandUsage("/mvunload" + ChatColor.GREEN + " {WORLD}");
        this.setArgRange(1, 1);
        this.addKey("mvunload");
        this.addKey("mv unload");
        this.setPermission("multiverse.core.unload", "Unloads a world from Multiverse. This does NOT remove the world folder. This does NOT remove it from the config file.", PermissionDefault.OP);
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
