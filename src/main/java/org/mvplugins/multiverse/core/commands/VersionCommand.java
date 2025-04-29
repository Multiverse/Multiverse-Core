package org.mvplugins.multiverse.core.commands;

import co.aikar.commands.BukkitCommandIssuer;
import co.aikar.commands.MessageType;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandPermission;
import co.aikar.commands.annotation.Description;
import co.aikar.commands.annotation.Subcommand;
import jakarta.inject.Inject;
import org.jetbrains.annotations.NotNull;
import org.jvnet.hk2.annotations.Service;

import org.mvplugins.multiverse.core.MultiverseCore;
import org.mvplugins.multiverse.core.command.LegacyAliasCommand;
import org.mvplugins.multiverse.core.command.MVCommandManager;
import org.mvplugins.multiverse.core.locale.MVCorei18n;

@Service
class VersionCommand extends CoreCommand {

    private final MultiverseCore plugin;

    @Inject
    VersionCommand(MultiverseCore plugin) {
        this.plugin = plugin;
    }

    @Subcommand("version")
    @CommandPermission("multiverse.core.version")
    @Description("{@@mv-core.version.description}")
    void versionCommand(BukkitCommandIssuer issuer) {
        issuer.sendMessage(MessageType.INFO, MVCorei18n.VERSION_MV, "{version}", plugin.getDescription().getVersion());
        issuer.sendMessage(MessageType.INFO, MVCorei18n.VERSION_AUTHORS,
                "{authors}", String.join(", ", plugin.getDescription().getAuthors()));
        // An in joke I don't get...
        issuer.sendMessage(MessageType.INFO, MVCorei18n.VERSION_SECRETCODE);
    }

    @Service
    private static final class LegacyAlias extends VersionCommand implements LegacyAliasCommand {
        @Inject
        LegacyAlias(MultiverseCore plugin) {
            super(plugin);
        }

        @Override
        @CommandAlias("mvversion")
        void versionCommand(BukkitCommandIssuer issuer) {
            super.versionCommand(issuer);
        }
    }
}
