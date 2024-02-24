package org.mvplugins.multiverse.core.commands;

import co.aikar.commands.BukkitCommandIssuer;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandPermission;
import co.aikar.commands.annotation.Description;
import co.aikar.commands.annotation.Subcommand;
import jakarta.inject.Inject;
import org.bukkit.ChatColor;
import org.jetbrains.annotations.NotNull;
import org.jvnet.hk2.annotations.Service;
import org.mvplugins.multiverse.core.MultiverseCore;
import org.mvplugins.multiverse.core.commandtools.MVCommandManager;
import org.mvplugins.multiverse.core.commandtools.MultiverseCommand;

@Service
@CommandAlias("mv")
class VersionCommand extends MultiverseCommand {

    private final MultiverseCore plugin;

    @Inject
    VersionCommand(@NotNull MVCommandManager commandManager, MultiverseCore plugin) {
        super(commandManager);
        this.plugin = plugin;
    }

    @Subcommand("version")
    @CommandPermission("multiverse.core.version")
    @Description("{@@mv-core.version.description}")
    void versionCommand(BukkitCommandIssuer issuer) {
        issuer.sendMessage("Multiverse Core version: " + ChatColor.WHITE + plugin.getDescription().getVersion());
        issuer.sendMessage("Multiverse Core authors: " + ChatColor.WHITE + String.join(", ", plugin.getDescription().getAuthors()));
        issuer.sendMessage("Special Code: " + ChatColor.WHITE + "FRN002"); // An in joke I don't get...
    }
}
