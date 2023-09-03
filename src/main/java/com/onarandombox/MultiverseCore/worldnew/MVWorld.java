package com.onarandombox.MultiverseCore.worldnew;

import com.onarandombox.MultiverseCore.worldnew.config.WorldConfig;
import io.vavr.control.Option;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public class MVWorld extends OfflineWorld {

    private final UUID worldUid;

    MVWorld(
            @NotNull World world,
            @NotNull WorldConfig worldConfig
    ) {
        super(world.getName(), worldConfig);
        this.worldUid = world.getUID();

        setupWorldConfig(world);
    }

    private void setupWorldConfig(World world) {
        worldConfig.setMVWorld(this);
        worldConfig.load();
        worldConfig.setEnvironment(world.getEnvironment());
        worldConfig.setSeed(world.getSeed());
    }

    public Option<World> getBukkitWorld() {
        return Option.of(Bukkit.getWorld(worldUid));
    }

    @Override
    void setWorldConfig(WorldConfig worldConfig) {
        super.setWorldConfig(worldConfig);
        setupWorldConfig(getBukkitWorld().get());
    }
}
