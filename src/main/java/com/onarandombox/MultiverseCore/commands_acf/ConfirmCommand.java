package com.onarandombox.MultiverseCore.commands_acf;

import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandPermission;
import co.aikar.commands.annotation.Description;
import co.aikar.commands.annotation.Subcommand;
import com.onarandombox.MultiverseCore.MultiverseCore;
import org.bukkit.command.CommandSender;

@CommandAlias("mv")
public class ConfirmCommand extends MultiverseCommand {

    public ConfirmCommand(MultiverseCore plugin) {
        super(plugin);
    }

    @Subcommand("confirm")
    @CommandPermission("multiverse.core.confirm")
    @Description("")
    public void onConfirmCommand(CommandSender sender) {
        this.plugin.getCommandQueueManager().runQueuedCommand(sender);
    }
}
