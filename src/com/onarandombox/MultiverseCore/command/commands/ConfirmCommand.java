package com.onarandombox.MultiverseCore.command.commands;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

import com.onarandombox.MultiverseCore.MultiverseCore;
import com.onarandombox.MultiverseCore.command.BaseCommand;

public class ConfirmCommand extends BaseCommand {
    
    public ConfirmCommand(MultiverseCore plugin) {
        super(plugin);
        name = "Confirms a command that could destroy life, the universe and everything.";
        description = "If you have not been prompted to use this, it will not do anything.";
        usage = "/mvconfirm" + ChatColor.GREEN + " Yes";
        minArgs = 1;
        maxArgs = 1;
        identifiers.add("mvconfirm");
    }
    
    @Override
    public void execute(CommandSender sender, String[] args) {
        if (args[0].equalsIgnoreCase("yes")) {
            plugin.confirmQueuedCommand(sender);
        } else {
            plugin.cancelQueuedCommand(sender);
        }
    }
    
}
