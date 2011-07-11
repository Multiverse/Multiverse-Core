package com.onarandombox.MultiverseCore.command.commands;

import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

import com.onarandombox.MultiverseCore.MultiverseCore;
import com.pneumaticraft.commandhandler.Command;

public class RemoveCommand extends Command {

    public RemoveCommand(MultiverseCore plugin) {
        super(plugin);
        this.commandName = "Remove World";
        this.commandDesc = "Unloads a world from Multiverse and removes it from worlds.yml, this does NOT remove the world folder.";
        this.commandUsage = "/mvremove" + ChatColor.GREEN + " {WORLD} ";
        this.minimumArgLength = 1;
        this.maximumArgLength = 1;
        this.commandKeys.add("mvremove");
        this.commandKeys.add("mv remove");
        this.permission = "multiverse.world.remove";
        this.opRequired = true;
    }

    @Override
    public void runCommand(CommandSender sender, List<String> args) {
        if (((MultiverseCore) this.plugin).removeWorld(args.get(0))) {
            sender.sendMessage("World removed from config!");
        } else {
            sender.sendMessage("Error trying to remove world from config!");
        }
    }
}
