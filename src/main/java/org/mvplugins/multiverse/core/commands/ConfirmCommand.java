package org.mvplugins.multiverse.core.commands;

import co.aikar.commands.BukkitCommandIssuer;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandPermission;
import co.aikar.commands.annotation.Description;
import co.aikar.commands.annotation.Subcommand;
import jakarta.inject.Inject;
import org.jetbrains.annotations.NotNull;
import org.jvnet.hk2.annotations.Service;

import org.mvplugins.multiverse.core.commandtools.MVCommandManager;
import org.mvplugins.multiverse.core.commandtools.MultiverseCommand;

@Service
@CommandAlias("mv")
class ConfirmCommand extends CoreCommand {

    @Inject
    ConfirmCommand(@NotNull MVCommandManager commandManager) {
        super(commandManager);
    }

    @CommandAlias("mvconfirm")
    @Subcommand("confirm")
    @CommandPermission("multiverse.core.confirm")
    @Description("{@@mv-core.confirm.description}")
    void onConfirmCommand(@NotNull BukkitCommandIssuer issuer) {
        this.commandManager.getCommandQueueManager().runQueuedCommand(issuer.getIssuer());
    }
}
