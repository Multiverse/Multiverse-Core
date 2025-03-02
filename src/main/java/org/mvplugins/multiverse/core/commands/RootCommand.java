package org.mvplugins.multiverse.core.commands;

import co.aikar.commands.CommandIssuer;
import co.aikar.commands.annotation.CommandAlias;
import jakarta.inject.Inject;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginDescriptionFile;
import org.jetbrains.annotations.NotNull;
import org.jvnet.hk2.annotations.Service;

import org.mvplugins.multiverse.core.MultiverseCore;
import org.mvplugins.multiverse.core.commandtools.MVCommandManager;
import org.mvplugins.multiverse.core.locale.MVCorei18n;

@Service
final class RootCommand extends CoreCommand {

    private final Plugin plugin;

    @Inject
    RootCommand(@NotNull MVCommandManager commandManager, @NotNull MultiverseCore plugin) {
        super(commandManager);
        this.plugin = plugin;
    }

    @CommandAlias("mv")
    void onRootCommand(CommandIssuer issuer) {
        PluginDescriptionFile description = this.plugin.getDescription();
        issuer.sendInfo(MVCorei18n.ROOT_TITLE,
                "{name}", description.getName(),
                "{version}", description.getVersion());
        issuer.sendInfo(MVCorei18n.ROOT_HELP);
    }
}
