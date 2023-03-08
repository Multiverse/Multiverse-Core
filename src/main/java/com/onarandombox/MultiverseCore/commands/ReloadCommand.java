package com.onarandombox.MultiverseCore.commands;

import java.util.ArrayList;
import java.util.List;

import co.aikar.commands.BukkitCommandIssuer;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandPermission;
import co.aikar.commands.annotation.Description;
import co.aikar.commands.annotation.Subcommand;
import com.onarandombox.MultiverseCore.MultiverseCore;
import com.onarandombox.MultiverseCore.event.MVConfigReloadEvent;
import jakarta.inject.Inject;
import org.bukkit.ChatColor;
import org.jetbrains.annotations.NotNull;
import org.jvnet.hk2.annotations.Service;

@Service
@CommandAlias("mv")
public class ReloadCommand extends MultiverseCoreCommand {

    @Inject
    public ReloadCommand(@NotNull MultiverseCore plugin) {
        super(plugin);
    }

    @Subcommand("reload")
    @CommandPermission("multiverse.core.reload")
    @Description("Reloads config files for all multiverse modules.")
    public void onReloadCommand(@NotNull BukkitCommandIssuer issuer) {
        issuer.sendMessage(ChatColor.GOLD + "Reloading all Multiverse Plugin configs...");
        this.plugin.loadConfigs();
        this.plugin.getAnchorManager().loadAnchors();
        this.plugin.getMVWorldManager().loadWorlds(true);

        List<String> configsLoaded = new ArrayList<>();
        configsLoaded.add("Multiverse-Core - config.yml");
        configsLoaded.add("Multiverse-Core - worlds.yml");
        configsLoaded.add("Multiverse-Core - anchors.yml");

        MVConfigReloadEvent configReload = new MVConfigReloadEvent(configsLoaded);
        this.plugin.getServer().getPluginManager().callEvent(configReload);

        configReload.getAllConfigsLoaded().forEach(issuer::sendMessage);
        issuer.sendMessage(String.format("%sReload Complete!", ChatColor.GREEN));
    }
}
