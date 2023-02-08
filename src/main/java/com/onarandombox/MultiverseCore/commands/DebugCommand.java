package com.onarandombox.MultiverseCore.commands;

import co.aikar.commands.BukkitCommandIssuer;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandCompletion;
import co.aikar.commands.annotation.CommandPermission;
import co.aikar.commands.annotation.Conditions;
import co.aikar.commands.annotation.Description;
import co.aikar.commands.annotation.Subcommand;
import co.aikar.commands.annotation.Syntax;
import com.dumptruckman.minecraft.util.Logging;
import com.onarandombox.MultiverseCore.MultiverseCore;
import com.onarandombox.MultiverseCore.locale.MVCorei18n;
import org.jetbrains.annotations.NotNull;

@CommandAlias("mv")
public class DebugCommand extends MultiverseCommand {
    public DebugCommand(@NotNull MultiverseCore plugin) {
        super(plugin);
    }

    @Subcommand("debug")
    @CommandPermission("multiverse.core.debug")
    @Description("{@@mv-core.debug_info_description}")
    public void onShowDebugCommand(BukkitCommandIssuer issuer) {
        this.displayDebugMode(issuer);
    }

    @Subcommand("debug")
    @CommandPermission("multiverse.core.debug")
    @Syntax("<{@@mv-core.debug_change_syntax}>")
    @CommandCompletion("@range:3")
    @Description("{@@mv-core.debug_change_description}")
    public void onChangeDebugCommand(BukkitCommandIssuer issuer,

                                     @Conditions("debuglevel")
                                     @Syntax("<{@@mv-core.debug_change_syntax}>")
                                     @Description("{@@mv-core.debug_change_level_description}")
                                     int level) {

        this.plugin.getMVConfig().setGlobalDebug(level);
        this.plugin.saveAllConfigs();
        this.displayDebugMode(issuer);
    }

    private void displayDebugMode(BukkitCommandIssuer issuer) {
        final int debugLevel = this.plugin.getMVConfig().getGlobalDebug();
        if (debugLevel == 0) {
            issuer.sendInfo(MVCorei18n.DEBUG_INFO_OFF);
            return;
        }
        issuer.sendInfo(MVCorei18n.DEBUG_INFO_ON, "{level}", String.valueOf(debugLevel));
        Logging.fine("Multiverse Debug ENABLED.");
    }
}
