package com.onarandombox.MultiverseCore.commands;

import co.aikar.commands.CommandIssuer;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandCompletion;
import co.aikar.commands.annotation.CommandPermission;
import co.aikar.commands.annotation.Conditions;
import co.aikar.commands.annotation.Subcommand;
import com.dumptruckman.minecraft.util.Logging;
import com.onarandombox.MultiverseCore.MultiverseCore;
import org.jetbrains.annotations.NotNull;

@CommandAlias("mv")
public class DebugCommand extends MultiverseCommand {

    public DebugCommand(MultiverseCore plugin) {
        super(plugin);
    }

    @Subcommand("debug")
    @CommandPermission("multiverse.core.debug")
    public void onShowDebugCommand(@NotNull CommandIssuer issuer) {
        this.displayDebugMode(issuer);
    }


    @Subcommand("debug")
    @CommandPermission("multiverse.core.debug")
    @CommandCompletion("@range:3")
    public void onChangeDebugCommand(@NotNull CommandIssuer issuer,
                                     @Conditions("debuglevel") int level) {

        this.plugin.getMVConfig().setGlobalDebug(level);
        this.plugin.saveMVConfigs();
        this.displayDebugMode(issuer);
    }

    private void displayDebugMode(@NotNull CommandIssuer issuer) {
        final int debugLevel = this.plugin.getMVConfig().getGlobalDebug();
        if (debugLevel == 0) {
            issuer.sendMessage("§fMultiverse Debug mode is §cOFF§f.");
            return;
        }
        issuer.sendMessage("§fMultiverse Debug mode is at §alevel {level}§f.");
        Logging.fine("Multiverse Debug ENABLED.");
    }
}
