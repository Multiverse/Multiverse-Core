package com.onarandombox.MultiverseCore.commands;

import co.aikar.commands.BukkitCommandIssuer;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandCompletion;
import co.aikar.commands.annotation.CommandPermission;
import co.aikar.commands.annotation.Description;
import co.aikar.commands.annotation.Subcommand;
import co.aikar.commands.annotation.Syntax;
import com.dumptruckman.minecraft.util.Logging;
import com.onarandombox.MultiverseCore.MultiverseCore;
import com.onarandombox.MultiverseCore.commandtools.MVCommandManager;
import com.onarandombox.MultiverseCore.commandtools.MultiverseCommand;
import com.onarandombox.MultiverseCore.config.MVCoreConfigProvider;
import com.onarandombox.MultiverseCore.utils.MVCorei18n;
import jakarta.inject.Inject;
import org.jetbrains.annotations.NotNull;
import org.jvnet.hk2.annotations.Service;

@Service
@CommandAlias("mv")
public class DebugCommand extends MultiverseCommand {

    private final MVCoreConfigProvider configProvider;
    private final MultiverseCore plugin;

    @Inject
    public DebugCommand(
            @NotNull MVCommandManager commandManager,
            @NotNull MVCoreConfigProvider configProvider,
            @NotNull MultiverseCore plugin
    ) {
        super(commandManager);
        this.configProvider = configProvider;
        this.plugin = plugin;
    }

    @Subcommand("debug")
    @CommandPermission("multiverse.core.debug")
    @Description("{@@mv-core.debug.info.description}")
    public void onShowDebugCommand(BukkitCommandIssuer issuer) {
        this.displayDebugMode(issuer);
    }

    @Subcommand("debug")
    @CommandPermission("multiverse.core.debug")
    @CommandCompletion("@range:3")
    @Syntax("<{@@mv-core.debug.change.syntax}>")
    @Description("{@@mv-core.debug.change.description}")
    public void onChangeDebugCommand(BukkitCommandIssuer issuer,

                                     @Syntax("<{@@mv-core.debug.change.syntax}>")
                                     @Description("{@@mv-core.debug.change.level.description}")
                                     int level) {

        this.configProvider.getConfig().setGlobalDebug(level);
        this.plugin.saveAllConfigs();
        this.displayDebugMode(issuer);
    }

    private void displayDebugMode(BukkitCommandIssuer issuer) {
        final int debugLevel = this.configProvider.getConfig().getGlobalDebug();
        if (debugLevel == 0) {
            issuer.sendInfo(MVCorei18n.DEBUG_INFO_OFF);
            return;
        }
        issuer.sendInfo(MVCorei18n.DEBUG_INFO_ON, "{level}", String.valueOf(debugLevel));
        Logging.fine("Multiverse Debug ENABLED.");
    }
}
