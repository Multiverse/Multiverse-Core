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

import org.mvplugins.multiverse.core.command.LegacyAliasCommand;
import org.mvplugins.multiverse.core.command.MVCommandIssuer;
import org.mvplugins.multiverse.core.command.queue.CommandQueueManager;

@Service
class ConfirmCommand extends CoreCommand {

    @NotNull
    private final CommandQueueManager commandQueueManager;

    @Inject
    ConfirmCommand(@NotNull CommandQueueManager commandQueueManager) {
        this.commandQueueManager = commandQueueManager;
    }

    @Subcommand("confirm")
    @CommandPermission("multiverse.core.confirm")
    @Syntax("[otp]")
    @Description("{@@mv-core.confirm.description}")
    void onConfirmCommand(
            MVCommandIssuer issuer,

            @Default("0")
            String otp) {
        this.commandQueueManager.runQueuedCommand(issuer, otp)
                .onFailure(failure -> issuer.sendError(failure.getFailureMessage()));
    }

    @Service
    private final static class LegacyAlias extends ConfirmCommand implements LegacyAliasCommand {
        @Inject
        LegacyAlias(@NotNull CommandQueueManager commandQueueCommand) {
            super(commandQueueCommand);
        }

        @Override
        @CommandAlias("mvconfirm")
        void onConfirmCommand(MVCommandIssuer issuer, String otp) {
            super.onConfirmCommand(issuer, otp);
        }
    }
}
