package com.mvplugin.integration.bukkit.api;

import org.bukkit.World;

import com.mvplugin.MultiverseWorld;

/**
 * Additional API for a Multiverse handled world specifically for Bukkit.
 */
public interface BukkitMultiverseWorld extends MultiverseWorld {

    /**
     * Gets the Bukkit {@link World} associated with this MultiverseWorld.
     *
     * @return The Bukkit world associated with this Multiverse world.
     */
    World getBukkitWorld();
}
