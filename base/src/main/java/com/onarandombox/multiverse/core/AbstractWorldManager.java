package com.onarandombox.multiverse.core;

import com.dumptruckman.minecraft.pluginbase.locale.BundledMessage;
import com.onarandombox.multiverse.core.api.Core;
import com.onarandombox.multiverse.core.api.MultiverseWorld;
import com.onarandombox.multiverse.core.api.WorldManager;
import com.onarandombox.multiverse.core.minecraft.WorldEnvironment;
import com.onarandombox.multiverse.core.minecraft.WorldType;
import com.onarandombox.multiverse.core.util.Language;

import java.util.HashMap;
import java.util.Map;

abstract class AbstractWorldManager<W extends MultiverseWorld> implements WorldManager<W> {

    protected final Core core;

    private final Map<String, W> worldMap;

    AbstractWorldManager(final Core core) {
        this.core = core;
        worldMap = new HashMap<String, W>();
    }

    @Override
    public boolean addWorld(String name, WorldEnvironment env, String seedString, WorldType type, Boolean generateStructures, String generator) throws WorldCreationException {
        return this.addWorld(name, env, seedString, type, generateStructures, generator, true);
    }

    @Override
    public boolean addWorld(String name,
                            WorldEnvironment env,
                            String seedString,
                            WorldType type,
                            Boolean generateStructures,
                            String generator,
                            boolean useSpawnAdjust) throws WorldCreationException {
        if (this.worldMap.containsKey(name)) {
            throw new WorldCreationException(new BundledMessage(Language.WORLD_ALREADY_EXISTS, name));
        }
        Long seed = null;
        if (seedString != null && seedString.length() > 0) {
            try {
                seed = Long.parseLong(seedString);
            } catch (NumberFormatException numberformatexception) {
                seed = (long) seedString.hashCode();
            }
        }

        // TODO: Use the fancy kind with the commandSender | dumptruckman has no idea what this means..
        if (generator != null && generator.length() == 0) {
            generator = null;
        }

        W mvWorld = createWorld(name, env, seed, type, generateStructures, generator);
        mvWorld.setAdjustSpawn(useSpawnAdjust);

        this.worldMap.put(name, mvWorld);

        return true;
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
    protected abstract W createWorld(String name,
                                     WorldEnvironment env,
                                     Long seed,
                                     WorldType type,
                                     Boolean generateStructures,
                                     String generator) throws WorldCreationException;
}
