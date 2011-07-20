package com.onarandombox.MultiverseCore.commands;

import java.util.List;

import org.bukkit.command.CommandSender;
import org.bukkit.permissions.PermissionDefault;

import com.onarandombox.MultiverseCore.MultiverseCore;

public class ConfirmCommand extends MultiverseCommand {

    public ConfirmCommand(MultiverseCore plugin) {
        super(plugin);
        // Any command that is dangerous should require op
        this.setName("Confirms a command that could destroy life, the universe and everything.");
        this.setCommandUsage("/mvconfirm");
        this.setArgRange(0, 0);
        this.addKey("mvconfirm");
        this.setPermission("multiverse.core.confirm", "If you have not been prompted to use this, it will not do anything.", PermissionDefault.OP);
        
    }

    @Override
    public void runCommand(CommandSender sender, List<String> args) {
        this.plugin.getCommandHandler().confirmQueuedCommand(sender);
    }

}
