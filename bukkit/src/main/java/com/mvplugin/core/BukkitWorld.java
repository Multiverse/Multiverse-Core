package com.mvplugin.core;

import com.mvplugin.core.api.BukkitMultiverseWorld;
import com.mvplugin.core.api.WorldProperties;
import com.mvplugin.core.api.WorldProperties.Spawning.Animals;
import com.mvplugin.core.minecraft.PlayerPosition;
import com.mvplugin.core.minecraft.WorldType;
import com.mvplugin.core.util.Convert;
import org.bukkit.Bukkit;
import org.bukkit.World;

import java.util.UUID;

class BukkitWorld extends AbstractMultiverseWorld implements BukkitMultiverseWorld {

    private final String name;
    private final UUID worldUID;
    private final WorldType worldType;

    BukkitWorld(final World world, final WorldProperties worldProperties) {
        super(worldProperties);
        this.name = world.getName();
        this.worldUID = world.getUID();
        this.worldType = Convert.fromBukkit(world.getWorldType());
    }

    @Override
    protected void update(Object obj) {
        if (obj == WorldProperties.DIFFICULTY) {
            getBukkitWorld().setDifficulty(Convert.toBukkit(getDifficulty()));
        } else if (obj == WorldProperties.ALLOW_WEATHER) {
            if (!isWeatherEnabled()) {
                final World world = getBukkitWorld();
                world.setWeatherDuration(0);
                world.setStorm(false);
                world.setThunderDuration(0);
                world.setThundering(false);
            }
        } else if (obj == WorldProperties.PVP) {
            getBukkitWorld().setPVP(isPVPEnabled());
        } else if (obj == WorldProperties.SPAWN_LOCATION) {
            final PlayerPosition pos = getSpawnLocation();
            getBukkitWorld().setSpawnLocation((int) pos.getX(), (int) pos.getY(), (int) pos.getZ());
        } else if (obj == Animals.SPAWN_RATE) {
            //getBukkitWorld().set
        }
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public UUID getWorldUID() {
        return this.worldUID;
    }

    @Override
    public WorldType getWorldType() {
        return worldType;
    }

    @Override
    public World getBukkitWorld() {
        final World world = Bukkit.getWorld(worldUID);
        if (world == null) {
            throw new IllegalStateException("Multiverse lost track of Bukkit world '" + this.name + "'");
        }
        return world;
    }

    @Override
    public String getTime() {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public boolean setTime(String timeAsString) {
        return false;  //To change body of implemented methods use File | Settings | File Templates.
    }
}
