package com.onarandombox.multiverse.core;

import com.dumptruckman.minecraft.pluginbase.util.Logging;
import com.onarandombox.multiverse.core.api.Core;
import com.onarandombox.multiverse.core.api.MultiverseWorld;
import com.onarandombox.multiverse.core.minecraft.WorldEnvironment;
import com.onarandombox.multiverse.core.minecraft.WorldType;
import org.bukkit.Bukkit;
import org.bukkit.World.Environment;
import org.bukkit.WorldCreator;

class BukkitWorldManager extends AbstractWorldManager {

    BukkitWorldManager(final Core core) {
        super(core);
    }

    @Override
    public MultiverseWorld createWorld(String name, WorldEnvironment env, Long seed, WorldType type, Boolean generateStructures, String generator, boolean useSpawnAdjust) throws WorldCreationException {
        if (Bukkit.getWorld(name) != null) {
            
        }
        WorldCreator c = new WorldCreator(name);
        if (seed != null) {
            c.seed(seed);
        }
        if (generator != null) {
            c.generator(generator);
        }
        c.environment(Environment.valueOf(env.toString()));
        if (type != null) {
            c.type(org.bukkit.WorldType.valueOf(type.toString()));
        }
        if (generateStructures != null) {
            c.generateStructures(generateStructures);
        }

        StringBuilder builder = new StringBuilder();
        builder.append("Loading World & Settings - '").append(name).append("'");
        builder.append(" - Env: ").append(env);
        builder.append(" - Type: ").append(type);
        if (seed != null) {
            builder.append(" & seed: ").append(seed);
        }
        if (generator != null) {
            builder.append(" & generator: ").append(generator);
        }
        Logging.info(builder.toString());
    }
}
