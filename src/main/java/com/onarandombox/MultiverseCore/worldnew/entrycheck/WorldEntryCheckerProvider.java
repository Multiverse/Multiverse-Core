package com.onarandombox.MultiverseCore.worldnew.entrycheck;

import com.onarandombox.MultiverseCore.config.MVCoreConfig;
import com.onarandombox.MultiverseCore.economy.MVEconomist;
import com.onarandombox.MultiverseCore.permissions.CorePermissionsChecker;
import jakarta.inject.Inject;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;
import org.jvnet.hk2.annotations.Service;

@Service
public class WorldEntryCheckerProvider {

    private final @NotNull MVCoreConfig config;
    private final @NotNull MVEconomist economist;
    private final @NotNull CorePermissionsChecker permissionsChecker;

    @Inject
    WorldEntryCheckerProvider(
            @NotNull MVCoreConfig config,
            @NotNull MVEconomist economist,
            @NotNull CorePermissionsChecker permissionsChecker
    ) {
        this.config = config;
        this.economist = economist;
        this.permissionsChecker = permissionsChecker;
    }

    public @NotNull WorldEntryChecker forSender(@NotNull CommandSender sender) {
        return new WorldEntryChecker(config, permissionsChecker, economist, sender);
    }
}
