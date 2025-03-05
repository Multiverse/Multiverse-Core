package org.mvplugins.multiverse.core.commands;

import java.util.ArrayList;
import java.util.List;

import co.aikar.commands.BukkitCommandIssuer;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandPermission;
import co.aikar.commands.annotation.Description;
import co.aikar.commands.annotation.Subcommand;
import jakarta.inject.Inject;
import org.bukkit.plugin.PluginManager;
import org.jetbrains.annotations.NotNull;
import org.jvnet.hk2.annotations.Service;

import org.mvplugins.multiverse.core.anchor.AnchorManager;
import org.mvplugins.multiverse.core.commandtools.MVCommandManager;
import org.mvplugins.multiverse.core.config.MVCoreConfig;
import org.mvplugins.multiverse.core.event.MVConfigReloadEvent;
import org.mvplugins.multiverse.core.locale.MVCorei18n;
import org.mvplugins.multiverse.core.world.WorldManager;

@Service
@CommandAlias("mv")
final class ReloadCommand extends CoreCommand {

    private final MVCoreConfig config;
    private final AnchorManager anchorManager;
    private final WorldManager worldManager;
    private final PluginManager pluginManager;

    @Inject
    ReloadCommand(
            @NotNull MVCommandManager commandManager,
            @NotNull MVCoreConfig config,
            @NotNull AnchorManager anchorManager,
            @NotNull WorldManager worldManager,
            @NotNull PluginManager pluginManager) {
        super(commandManager);
        this.config = config;
        this.anchorManager = anchorManager;
        this.worldManager = worldManager;
        this.pluginManager = pluginManager;
    }

    @CommandAlias("mvreload|mvr")
    @Subcommand("reload")
    @CommandPermission("multiverse.core.reload")
    @Description("{@@mv-core.reload.description}")
    void onReloadCommand(@NotNull BukkitCommandIssuer issuer) {
        issuer.sendInfo(MVCorei18n.RELOAD_RELOADING);
        try {
            // TODO: Make this all Try<Void>
            this.config.load().getOrElseThrow(e -> new RuntimeException("Failed to load config", e));
            this.worldManager.initAllWorlds().getOrElseThrow(e -> new RuntimeException("Failed to init worlds", e));
            this.anchorManager.loadAnchors();
        } catch (Exception e) {
            e.printStackTrace();
        }

        List<String> configsLoaded = new ArrayList<>();
        configsLoaded.add("Multiverse-Core - config.yml");
        configsLoaded.add("Multiverse-Core - worlds.yml");
        configsLoaded.add("Multiverse-Core - anchors.yml");

        MVConfigReloadEvent configReload = new MVConfigReloadEvent(configsLoaded);
        this.pluginManager.callEvent(configReload);

        configReload.getAllConfigsLoaded().forEach(issuer::sendMessage);
        issuer.sendInfo(MVCorei18n.RELOAD_SUCCESS);
    }
}
