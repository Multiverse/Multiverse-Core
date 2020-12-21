package com.onarandombox.MultiverseCore.commands;

import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandPermission;
import co.aikar.commands.annotation.Description;
import co.aikar.commands.annotation.Subcommand;
import com.onarandombox.MultiverseCore.MultiverseCore;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

@CommandAlias("mv")
public class ConfirmCommand extends MultiverseCommand {

    public ConfirmCommand(MultiverseCore plugin) {
        super(plugin);
    }

    @Subcommand("confirm")
    @CommandPermission("multiverse.core.confirm")
    @Description("Confirms dangerous commands before executing them.")
    public void onConfirmCommand(@NotNull CommandSender sender) {
        this.plugin.getMVCommandManager().getQueueManager().runQueuedCommand(sender);
    }
}
