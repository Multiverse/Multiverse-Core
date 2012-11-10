package com.mvplugin.integration;

import java.util.concurrent.Callable;

import com.mvplugin.MultiverseWorld;
import com.mvplugin.WorldCreationException;
import com.mvplugin.WorldManager.WorldCreationSettings;

public interface WorldAPI {
    /**
     * Creates a {@link MultiverseWorld} with the given properties.
     *
     * If a Minecraft world is already loaded with this name, null will be returned.  If a Minecraft world already
     * exists but it not loaded, it will be loaded instead. 
     *
     * @param settings Settings for this world creation.
     * @return An api-specific object or null if a Minecraft world by this name is already loaded.
     * @throws WorldCreationException If any problems occured while trying to create the world.
     */
    Callable<?> createWorld(WorldCreationSettings settings) throws WorldCreationException;
}
