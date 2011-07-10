package com.onarandombox.MultiverseCore.command.commands;

import java.util.List;

import org.bukkit.command.CommandSender;

import com.onarandombox.MultiverseCore.MultiverseCore;
import com.onarandombox.MultiverseCore.command.BaseCommand;
import com.pneumaticraft.commandhandler.Command;

public class ConfirmCommand extends Command {

    public ConfirmCommand(MultiverseCore plugin) {
        super(plugin);
        this.commandName = "Confirms a command that could destroy life, the universe and everything.";
        this.commandDesc = "If you have not been prompted to use this, it will not do anything.";
        this.commandUsage = "/mvconfirm";
        this.minimumArgLength = 0;
        this.maximumArgLength = 0;
        this.commandKeys.add("mvconfirm");
        this.permission = "multiverse.world.confirm";
        // Any command that is dangerous should require op
        this.opRequired = true;
    }

    @Override
    public void runCommand(CommandSender sender, List<String> args) {
        this.plugin.getCommandManager().confirmQueuedCommand(sender);
        
    }

}
