package com.onarandombox.multiverse.core;

import com.dumptruckman.minecraft.pluginbase.locale.BundledMessage;
import com.dumptruckman.minecraft.pluginbase.util.Logging;
import com.onarandombox.multiverse.core.api.BukkitMultiverseWorld;
import com.onarandombox.multiverse.core.api.WorldProperties;
import com.onarandombox.multiverse.core.minecraft.WorldEnvironment;
import com.onarandombox.multiverse.core.minecraft.WorldType;
import com.onarandombox.multiverse.core.util.BukkitLanguage;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.World.Environment;
import org.bukkit.WorldCreator;

import java.io.File;
import java.io.IOException;

public class BukkitWorldManager extends AbstractWorldManager<BukkitMultiverseWorld> {

    private final MultiverseCore plugin;
    private final File worldsFolder;

    BukkitWorldManager(final MultiverseCore core) {
        super(core);
        this.plugin = core;
        worldsFolder = new File(core.getDataFolder(), "worlds");
    }

    @Override
    public WorldProperties getWorldProperties(final String worldName) throws IOException {
        return new YamlWorldProperties(plugin, new File(worldsFolder, worldName + ".yml"));
    }

    @Override
    public BukkitMultiverseWorld createWorld(String name, WorldEnvironment env, Long seed, WorldType type,
                                             Boolean generateStructures, String generator) throws WorldCreationException {
        if (Bukkit.getWorld(name) != null) {
            return null;
        }
        final WorldCreator c = new WorldCreator(name);
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
        try {
            final World world = c.createWorld();
            return new BukkitWorld(world, getWorldProperties(name));
        } catch (Exception e) {
            throw new WorldCreationException(new BundledMessage(BukkitLanguage.CREATE_WORLD_ERROR, name), e);
        }
    }
}
