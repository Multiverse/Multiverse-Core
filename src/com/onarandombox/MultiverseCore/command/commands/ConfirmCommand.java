package com.onarandombox.MultiverseCore.command.commands;

import org.bukkit.command.CommandSender;

import com.onarandombox.MultiverseCore.MultiverseCore;
import com.onarandombox.MultiverseCore.command.BaseCommand;

public class ConfirmCommand extends BaseCommand {

    public ConfirmCommand(MultiverseCore plugin) {
        super(plugin);
        this.name = "Confirms a command that could destroy life, the universe and everything.";
        this.description = "If you have not been prompted to use this, it will not do anything.";
        this.usage = "/mvconfirm";
        this.minArgs = 0;
        this.maxArgs = 0;
        this.identifiers.add("mvconfirm");
        this.permission = "multiverse.world.confirm";
        // Any command that is dangerous should require op
        this.requiresOp = true;
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        this.plugin.getCommandManager().confirmQueuedCommand(sender);
    }

}
