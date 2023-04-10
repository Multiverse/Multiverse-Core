package com.onarandombox.MultiverseCore.world.entrycheck;

import com.onarandombox.MultiverseCore.api.MVWorld;
import com.onarandombox.MultiverseCore.config.MVCoreConfig;
import com.onarandombox.MultiverseCore.economy.MVEconomist;
import com.onarandombox.MultiverseCore.utils.permissions.PermissionsChecker;
import jakarta.inject.Inject;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jvnet.hk2.annotations.Service;

@Service
public class WorldEntryCheckerProvider {

    private final @NotNull MVCoreConfig config;
    private final @NotNull MVEconomist economist;
    private final @NotNull PermissionsChecker permissionsChecker;

    @Inject
    WorldEntryCheckerProvider(
            @NotNull MVCoreConfig config,
            @NotNull MVEconomist economist,
            @NotNull PermissionsChecker permissionsChecker
    ) {
        this.config = config;
        this.economist = economist;
        this.permissionsChecker = permissionsChecker;
    }

    public @NotNull WorldEntryChecker forWorld(@NotNull Player player, @Nullable MVWorld fromWorld, @NotNull MVWorld toWorld) {
        return new WorldEntryChecker(player, fromWorld, toWorld, config, permissionsChecker, economist);
    }
}
