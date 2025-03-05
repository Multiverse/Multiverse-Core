package org.mvplugins.multiverse.core.world.entrycheck;

import jakarta.inject.Inject;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;
import org.jvnet.hk2.annotations.Service;

import org.mvplugins.multiverse.core.config.MVCoreConfig;
import org.mvplugins.multiverse.core.economy.MVEconomist;
import org.mvplugins.multiverse.core.permissions.CorePermissionsChecker;

@Service
public final class WorldEntryCheckerProvider {

    private final @NotNull MVCoreConfig config;
    private final @NotNull MVEconomist economist;
    private final @NotNull CorePermissionsChecker permissionsChecker;

    @Inject
    WorldEntryCheckerProvider(
            @NotNull MVCoreConfig config,
            @NotNull MVEconomist economist,
            @NotNull CorePermissionsChecker permissionsChecker) {
        this.config = config;
        this.economist = economist;
        this.permissionsChecker = permissionsChecker;
    }

    public @NotNull WorldEntryChecker forSender(@NotNull CommandSender sender) {
        return new WorldEntryChecker(config, permissionsChecker, economist, sender);
    }
}
