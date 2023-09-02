package com.onarandombox.MultiverseCore.worldnew;

import com.onarandombox.MultiverseCore.worldnew.config.WorldConfig;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public class MVWorld extends OfflineWorld {

    private final UUID worldUid;

    public MVWorld(
            @NotNull String worldName,
            @NotNull WorldConfig worldConfig,
            @NotNull UUID worldUid
    ) {
        super(worldName, worldConfig);
        this.worldUid = worldUid;

        worldConfig.setMVWorld(this);
        worldConfig.load();
    }

    public World getBukkitWorld() {
        return Bukkit.getWorld(worldUid);
    }
}
