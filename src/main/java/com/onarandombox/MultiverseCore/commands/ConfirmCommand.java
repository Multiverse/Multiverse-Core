package com.onarandombox.MultiverseCore.commands;

import co.aikar.commands.BukkitCommandIssuer;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandPermission;
import co.aikar.commands.annotation.Description;
import co.aikar.commands.annotation.Subcommand;
import com.onarandombox.MultiverseCore.MultiverseCore;
import jakarta.inject.Inject;
import org.jetbrains.annotations.NotNull;
import org.jvnet.hk2.annotations.Service;

@Service
@CommandAlias("mv")
public class ConfirmCommand extends MultiverseCoreCommand {

    @Inject
    public ConfirmCommand(@NotNull MultiverseCore plugin) {
        super(plugin);
    }

    @Subcommand("confirm")
    @CommandPermission("multiverse.core.confirm")
    @Description("Confirms dangerous commands before executing them.")
    public void onConfirmCommand(@NotNull BukkitCommandIssuer issuer) {
        this.plugin.getMVCommandManager().getCommandQueueManager().runQueuedCommand(issuer.getIssuer());
    }
}
