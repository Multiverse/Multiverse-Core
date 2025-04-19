package org.mvplugins.multiverse.core.commands;

import co.aikar.commands.BukkitCommandIssuer;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandCompletion;
import co.aikar.commands.annotation.CommandPermission;
import co.aikar.commands.annotation.Description;
import co.aikar.commands.annotation.Subcommand;
import co.aikar.commands.annotation.Syntax;
import com.dumptruckman.minecraft.util.Logging;
import jakarta.inject.Inject;
import org.jetbrains.annotations.NotNull;
import org.jvnet.hk2.annotations.Service;

import org.mvplugins.multiverse.core.command.LegacyAliasCommand;
import org.mvplugins.multiverse.core.command.MVCommandManager;
import org.mvplugins.multiverse.core.config.CoreConfig;
import org.mvplugins.multiverse.core.locale.MVCorei18n;

@Service
class DebugCommand extends CoreCommand {

    private final CoreConfig config;

    @Inject
    DebugCommand(@NotNull CoreConfig config) {
        this.config = config;
    }

    @Subcommand("debug")
    @CommandPermission("multiverse.core.debug")
    @Description("{@@mv-core.debug.info.description}")
    void onShowDebugCommand(BukkitCommandIssuer issuer) {
        this.displayDebugMode(issuer);
    }

    @Subcommand("debug")
    @CommandPermission("multiverse.core.debug")
    @CommandCompletion("@range:3")
    @Syntax("<{@@mv-core.debug.change.syntax}>")
    @Description("{@@mv-core.debug.change.description}")
    void onChangeDebugCommand(
            BukkitCommandIssuer issuer,

            @Syntax("<{@@mv-core.debug.change.syntax}>")
            @Description("{@@mv-core.debug.change.level.description}")
            int level) {

        config.setGlobalDebug(level);
        config.save();
        this.displayDebugMode(issuer);
    }

    private void displayDebugMode(BukkitCommandIssuer issuer) {
        final int debugLevel = config.getGlobalDebug();
        if (debugLevel == 0) {
            issuer.sendInfo(MVCorei18n.DEBUG_INFO_OFF);
            return;
        }
        issuer.sendInfo(MVCorei18n.DEBUG_INFO_ON, "{level}", String.valueOf(debugLevel));
        Logging.fine("Multiverse Debug ENABLED.");
    }

    @Service
    private static final class LegacyAlias extends DebugCommand implements LegacyAliasCommand {
        @Inject
        public LegacyAlias(@NotNull CoreConfig config) {
            super(config);
        }

        @Override
        @CommandAlias("mvdebug")
        void onChangeDebugCommand(BukkitCommandIssuer issuer, int level) {
            super.onChangeDebugCommand(issuer, level);
        }
    }
}
