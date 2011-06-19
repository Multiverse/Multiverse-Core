package com.onarandombox.MultiverseCore.command.commands;

import org.bukkit.command.CommandSender;

import com.onarandombox.MultiverseCore.MultiverseCore;
import com.onarandombox.MultiverseCore.command.BaseCommand;

public class ConfirmCommand extends BaseCommand {
    
    public ConfirmCommand(MultiverseCore plugin) {
        super(plugin);
        name = "Confirms a command that could destroy life, the universe and everything.";
        description = "If you have not been prompted to use this, it will not do anything.";
        usage = "/mvconfirm";
        minArgs = 0;
        maxArgs = 0;
        identifiers.add("mvconfirm");
        this.permission = "multiverse.world.confirm";
        // Any command that is dangerous should require op
        this.requiresOp = true;
    }
    
    @Override
    public void execute(CommandSender sender, String[] args) {
        plugin.confirmQueuedCommand(sender);
    }
    
}
