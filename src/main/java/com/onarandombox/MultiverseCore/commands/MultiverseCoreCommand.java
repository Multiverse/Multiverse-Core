package com.onarandombox.MultiverseCore.commands;

import com.onarandombox.MultiverseCore.MultiverseCore;
import com.onarandombox.MultiverseCore.commandtools.MultiverseCommand;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

/**
 * Generic multiverse core command with handy reference to the plugin instance.
 */
public abstract class MultiverseCoreCommand extends MultiverseCommand {

    protected final MultiverseCore plugin;

    protected MultiverseCoreCommand(MultiverseCore plugin) {
        this.plugin = plugin;
    }

    protected boolean saveMVConfigs(CommandSender sender) {
        if (this.plugin.saveMVConfigs()) {
            return true;
        }
        sender.sendMessage(ChatColor.RED + "An error occurred while trying to save Multiverse-Core config.yml. " +
                "You changes will be temporary!");
        return false;
    }
}
