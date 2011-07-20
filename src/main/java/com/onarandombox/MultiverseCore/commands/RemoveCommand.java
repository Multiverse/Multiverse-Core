package com.onarandombox.MultiverseCore.commands;

import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.permissions.PermissionDefault;

import com.onarandombox.MultiverseCore.MultiverseCore;

public class RemoveCommand extends MultiverseCommand {

    public RemoveCommand(MultiverseCore plugin) {
        super(plugin);
        this.setName("Remove World");
        this.setCommandUsage("/mvremove " + ChatColor.GREEN + " {WORLD}");
        this.setArgRange(1, 1);
        this.addKey("mvcoord");
        this.setPermission("multiverse.core.remove", "Unloads a world from Multiverse and removes it from worlds.yml, this does NOT remove the world folder.", PermissionDefault.OP);
    }

    @Override
    public void runCommand(CommandSender sender, List<String> args) {
        if (this.plugin.removeWorld(args.get(0))) {
            sender.sendMessage("World removed from config!");
        } else {
            sender.sendMessage("Error trying to remove world from config!");
        }
    }
}
