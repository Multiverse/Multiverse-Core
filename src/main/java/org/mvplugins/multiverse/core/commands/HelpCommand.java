package org.mvplugins.multiverse.core.commands;

import co.aikar.commands.CommandHelp;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandCompletion;
import co.aikar.commands.annotation.CommandPermission;
import co.aikar.commands.annotation.Description;
import co.aikar.commands.annotation.Subcommand;
import co.aikar.commands.annotation.Syntax;
import jakarta.inject.Inject;
import org.jetbrains.annotations.NotNull;
import org.jvnet.hk2.annotations.Service;

import org.mvplugins.multiverse.core.command.LegacyAliasCommand;
import org.mvplugins.multiverse.core.command.MVCommandManager;
import org.mvplugins.multiverse.core.config.CoreConfig;

@Service
class HelpCommand extends CoreCommand {

    private final MVCommandManager commandManager;

    @Inject
    HelpCommand(@NotNull MVCommandManager commandManager) {
        this.commandManager = commandManager;
    }

    @co.aikar.commands.annotation.HelpCommand
    @Subcommand("help")
    @CommandPermission("multiverse.core.help")
    @CommandCompletion("@commands:mv")
    @Syntax("[filter] [page]")
    @Description("{@@mv-core.usage.description}")
    void onHelpCommand(CommandHelp help) {
        if (help.getIssuer().isPlayer()) {
            // Prevent flooding the chat
            help.setPerPage(4);
        }
        this.commandManager.showUsage(help);
    }

    @Service
    private static final class LegacyAlias extends HelpCommand implements LegacyAliasCommand {
        @Inject
        LegacyAlias(@NotNull MVCommandManager commandManager) {
            super(commandManager);
        }

        @Override
        @CommandAlias("mvhelp")
        public void onHelpCommand(CommandHelp help) {
            super.onHelpCommand(help);
        }
    }
}
