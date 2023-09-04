package com.onarandombox.MultiverseCore.commands;

import co.aikar.commands.BukkitCommandIssuer;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandPermission;
import co.aikar.commands.annotation.Description;
import co.aikar.commands.annotation.Subcommand;
import com.onarandombox.MultiverseCore.anchor.AnchorManager;
import com.onarandombox.MultiverseCore.commandtools.MVCommandManager;
import com.onarandombox.MultiverseCore.commandtools.MultiverseCommand;
import com.onarandombox.MultiverseCore.config.MVCoreConfig;
import com.onarandombox.MultiverseCore.event.MVConfigReloadEvent;
import com.onarandombox.MultiverseCore.utils.MVCorei18n;
import com.onarandombox.MultiverseCore.worldnew.WorldManager;
import jakarta.inject.Inject;
import org.bukkit.plugin.PluginManager;
import org.jetbrains.annotations.NotNull;
import org.jvnet.hk2.annotations.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@CommandAlias("mv")
public class ReloadCommand extends MultiverseCommand {

    private final MVCoreConfig config;
    private final AnchorManager anchorManager;
    private final WorldManager worldManager;
    private final PluginManager pluginManager;

    @Inject
    public ReloadCommand(
            @NotNull MVCommandManager commandManager,
            @NotNull MVCoreConfig config,
            @NotNull AnchorManager anchorManager,
            @NotNull WorldManager worldManager,
            @NotNull PluginManager pluginManager
    ) {
        super(commandManager);
        this.config = config;
        this.anchorManager = anchorManager;
        this.worldManager = worldManager;
        this.pluginManager = pluginManager;
    }

    @Subcommand("reload")
    @CommandPermission("multiverse.core.reload")
    @Description("{@@mv-core.reload.description}")
    public void onReloadCommand(@NotNull BukkitCommandIssuer issuer) {
        issuer.sendInfo(MVCorei18n.RELOAD_RELOADING);
        try {
            this.config.load();
            this.worldManager.initAllWorlds();
            this.anchorManager.loadAnchors();
        } catch (Exception e) {
            e.printStackTrace();
        }

        List<String> configsLoaded = new ArrayList<>();
        configsLoaded.add("Multiverse-Core - config.yml");
        configsLoaded.add("Multiverse-Core - worlds2.yml");
        configsLoaded.add("Multiverse-Core - anchors.yml");

        MVConfigReloadEvent configReload = new MVConfigReloadEvent(configsLoaded);
        this.pluginManager.callEvent(configReload);

        // TODO: replace this sendMessage and format the configsLoaded above, maybe?
        configReload.getAllConfigsLoaded().forEach(issuer::sendMessage);
        issuer.sendInfo(MVCorei18n.RELOAD_SUCCESS);
    }
}
