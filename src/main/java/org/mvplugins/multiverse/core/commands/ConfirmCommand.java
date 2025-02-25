package org.mvplugins.multiverse.core.commands;

import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandPermission;
import co.aikar.commands.annotation.Default;
import co.aikar.commands.annotation.Description;
import co.aikar.commands.annotation.Subcommand;
import co.aikar.commands.annotation.Syntax;
import jakarta.inject.Inject;
import org.jetbrains.annotations.NotNull;
import org.jvnet.hk2.annotations.Service;

import org.mvplugins.multiverse.core.commandtools.MVCommandIssuer;
import org.mvplugins.multiverse.core.commandtools.MVCommandManager;

@Service
@CommandAlias("mv")
final class ConfirmCommand extends CoreCommand {

    @Inject
    ConfirmCommand(@NotNull MVCommandManager commandManager) {
        super(commandManager);
    }

    @CommandAlias("mvconfirm")
    @Subcommand("confirm")
    @CommandPermission("multiverse.core.confirm")
    @Syntax("[otp]")
    @Description("{@@mv-core.confirm.description}")
    void onConfirmCommand(
            @NotNull MVCommandIssuer issuer,

            @Default("0")
            int otp) {
        try {
            this.commandManager.getCommandQueueManager().runQueuedCommand(issuer, otp);
        } catch (Throwable t) {
            t.printStackTrace();
        }
    }
}
