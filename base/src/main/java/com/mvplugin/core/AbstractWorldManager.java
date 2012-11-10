package com.mvplugin.core;

import com.dumptruckman.minecraft.pluginbase.messaging.BundledMessage;
import com.mvplugin.core.api.MultiverseCore;
import com.mvplugin.core.api.MultiverseWorld;
import com.mvplugin.core.api.WorldManager;
import com.mvplugin.core.minecraft.WorldEnvironment;
import com.mvplugin.core.minecraft.WorldType;
import com.mvplugin.core.util.Language;

import java.util.HashMap;
import java.util.Map;

abstract class AbstractWorldManager<W extends MultiverseWorld> implements WorldManager<W> {

    protected final MultiverseCore core;
    private final Map<String, MultiverseWorld> worldMap;

    protected AbstractWorldManager(final MultiverseCore core) {
        this.core = core;
        this.worldMap = new HashMap<String, MultiverseWorld>();
    }

    @Override
    public W addWorld(String name, WorldEnvironment env, String seedString, WorldType type, Boolean generateStructures, String generator) throws WorldCreationException {
        return this.addWorld(name, env, seedString, type, generateStructures, generator, true);
    }

    @Override
    public W addWorld(String name,
                      WorldEnvironment env,
                      String seedString,
                      WorldType type,
                      Boolean generateStructures,
                      String generator,
                      boolean useSpawnAdjust) throws WorldCreationException {
        final WorldCreationSettings settings = new WorldCreationSettings(name);
        if (seedString != null && !seedString.isEmpty()) {
            try {
                settings.seed(Long.parseLong(seedString));
            } catch (NumberFormatException numberformatexception) {
                settings.seed((long) seedString.hashCode());
            }
        }

        settings.type(type);
        settings.generateStructures(generateStructures);

        // TODO: Use the fancy kind with the commandSender | dumptruckman has no idea what this means..
        if (!generator.isEmpty()) {
            settings.generator(generator);
        }

        settings.adjustSpawn(useSpawnAdjust);

        return addWorld(settings);
    }

    @Override
    public W addWorld(WorldCreationSettings settings) throws WorldCreationException {
        if (this.worldMap.containsKey(settings.name())) {
            throw new WorldCreationException(new BundledMessage(Language.WORLD_ALREADY_EXISTS, settings.name()));
        }
        W mvWorld = createWorld(settings);
        mvWorld.setAdjustSpawn(settings.adjustSpawn());
        this.worldMap.put(settings.name(), mvWorld);
        return mvWorld;
    }

    /**
     * Creates a {@link W} with the given properties.
     *
     * If a Minecraft world is already loaded with this name, null will be returned.  If a Minecraft world already
     * exists but it not loaded, it will be loaded instead.
     *
     * @param name The name for the world.  May not be null.
     * @param env The environment for the world.  May not be null.
     * @param seed The seed for the world.  Null means a random seed will be used if creating a new Minecraft world.
     * @param type The world type for the world.  Null means the Minecraft default will be used if creating a new
     *             Minecraft world or, if loading a world, the loaded world's type will be used.
     * @param generateStructures Whether or not to generate structures for the world.  Null means the Minecraft default
     *                           will be used if creating a new world or, if loading a world, the loaded world's
     *                           setting will be used.
     * @param generator The name of the generator for the world.  Null may be used to specify no generator.
     * @return A new {@link W} or null if a Minecraft world by this name is already loaded.
     * @throws WorldCreationException If any problems occured while trying to create the world.
     */

    /**
     * Creates a {@link W} with the given properties.
     *
     * If a Minecraft world is already loaded with this name, null will be returned.  If a Minecraft world already
     * exists but it not loaded, it will be loaded instead.
     *
     * @param settings
     * @return
     * @throws WorldCreationException
     */
    protected abstract W createWorld(WorldCreationSettings settings) throws WorldCreationException;
}
