package com.mvplugin.core;

import com.mvplugin.core.api.BukkitMultiverseWorld;
import com.mvplugin.core.api.WorldProperties;
import org.bukkit.Bukkit;
import org.bukkit.World;

import java.util.UUID;

class BukkitWorld extends AbstractMultiverseWorld implements BukkitMultiverseWorld {

    private final String name;
    private final UUID worldUID;

    BukkitWorld(final World world, final WorldProperties worldProperties) {
        super(worldProperties);
        this.name = world.getName();
        this.worldUID = world.getUID();
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
    public World getBukkitWorld() {
        final World world = Bukkit.getWorld(worldUID);
        if (world == null) {
            throw new NullPointerException("Multiverse lost track of Bukkit world '" + this.name + "'");
        }
        return world;
    }
}
