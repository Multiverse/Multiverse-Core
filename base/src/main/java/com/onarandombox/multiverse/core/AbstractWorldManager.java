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

abstract class AbstractWorldManager implements WorldManager {

    protected final Core core;

    private final Map<String, MultiverseWorld> worldMap;

    AbstractWorldManager(final Core core) {
        this.core = core;
        worldMap = new HashMap<String, MultiverseWorld>();
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

        createWorld(name, env, seed, type, generateStructures, generator, true);

        return true;
    }

    protected abstract MultiverseWorld createWorld(String name,
                                                   WorldEnvironment env,
                                                   Long seed,
                                                   WorldType type,
                                                   Boolean generateStructures,
                                                   String generator,
                                                   boolean useSpawnAdjust) throws WorldCreationException;
}
