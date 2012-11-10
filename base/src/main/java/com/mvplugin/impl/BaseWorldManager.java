package com.mvplugin.impl;

import com.dumptruckman.minecraft.pluginbase.locale.BundledMessage;
import com.mvplugin.MVCore;
import com.mvplugin.MultiverseWorld;
import com.mvplugin.WorldCreationException;
import com.mvplugin.WorldManager;
import com.mvplugin.impl.util.Language;
import com.mvplugin.integration.WorldAPI;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Callable;

public class BaseWorldManager implements WorldManager {
    protected final MVCore core;
    private WorldAPI creator;

    private final Map<String, MultiverseWorld> worldMap;

    protected BaseWorldManager(final MVCore core, final WorldAPI creator) {
        this.core = core;
        this.creator = creator;
        this.worldMap = new HashMap<String, MultiverseWorld>();
    }

    @Override
    public MultiverseWorld addWorld(WorldCreationSettings settings) throws WorldCreationException {
        if (this.worldMap.containsKey(settings.name())) {
            throw new WorldCreationException(new BundledMessage(Language.WORLD_ALREADY_EXISTS, settings.name()));
        }

        Callable<?> callable = this.creator.createWorld(settings);
        MultiverseWorld mvWorld = new BaseMultiverseWorld(null); // TODO world properties
        mvWorld.setAPISpecificObjectCallable(callable);
        this.worldMap.put(settings.name(), mvWorld);
        return mvWorld;
    }
}
