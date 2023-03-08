package com.onarandombox.MultiverseCore.commands;

import java.util.ArrayList;
import java.util.List;

import co.aikar.commands.BukkitCommandIssuer;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandPermission;
import co.aikar.commands.annotation.Description;
import co.aikar.commands.annotation.Subcommand;
import com.onarandombox.MultiverseCore.MultiverseCore;
import com.onarandombox.MultiverseCore.anchor.AnchorManager;
import com.onarandombox.MultiverseCore.api.MVWorldManager;
import com.onarandombox.MultiverseCore.commandtools.MVCommandManager;
import com.onarandombox.MultiverseCore.commandtools.MultiverseCommand;
import com.onarandombox.MultiverseCore.event.MVConfigReloadEvent;
import com.onarandombox.MultiverseCore.utils.MVCorei18n;
import jakarta.inject.Inject;
import org.bukkit.ChatColor;
import org.bukkit.Server;
import org.jetbrains.annotations.NotNull;
import org.jvnet.hk2.annotations.Service;

@Service
@CommandAlias("mv")
public class ReloadCommand extends MultiverseCommand {

    private final MultiverseCore plugin;
    private final AnchorManager anchorManager;
    private final MVWorldManager worldManager;
    private final Server server;

    @Inject
    public ReloadCommand(
            @NotNull MVCommandManager commandManager,
            @NotNull MultiverseCore plugin,
            @NotNull AnchorManager anchorManager,
            @NotNull MVWorldManager worldManager,
            @NotNull Server server
    ) {
        super(commandManager);
        this.plugin = plugin;
        this.anchorManager = anchorManager;
        this.worldManager = worldManager;
        this.server = server;
    }

    @Subcommand("reload")
    @CommandPermission("multiverse.core.reload")
    @Description("{@@mv-core.reload.description}")
    public void onReloadCommand(@NotNull BukkitCommandIssuer issuer) {
        issuer.sendInfo(MVCorei18n.RELOAD_RELOADING);
        this.plugin.loadConfigs();
        this.anchorManager.loadAnchors();
        this.worldManager.loadWorlds(true);

        List<String> configsLoaded = new ArrayList<>();
        configsLoaded.add("Multiverse-Core - config.yml");
        configsLoaded.add("Multiverse-Core - worlds.yml");
        configsLoaded.add("Multiverse-Core - anchors.yml");

        MVConfigReloadEvent configReload = new MVConfigReloadEvent(configsLoaded);
        this.server.getPluginManager().callEvent(configReload);

        // @TODO: replace this sendMessage and format the configsLoaded above, maybe?
        configReload.getAllConfigsLoaded().forEach(issuer::sendMessage);
        issuer.sendInfo(MVCorei18n.RELOAD_SUCCESS);
    }
}
