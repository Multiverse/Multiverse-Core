package com.mvplugin.integration.bukkit;

import com.dumptruckman.minecraft.pluginbase.messaging.BundledMessage;
import com.mvplugin.MVCore;
import com.mvplugin.WorldCreationException;
import com.mvplugin.WorldManager.WorldCreationSettings;
import com.mvplugin.integration.WorldAPI;
import com.mvplugin.integration.bukkit.util.BukkitLanguage;
import com.mvplugin.integration.bukkit.util.Convert;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.WorldCreator;
import org.bukkit.generator.ChunkGenerator;

import java.io.File;
import java.util.concurrent.Callable;

public class BukkitWorldAPI implements WorldAPI {
    private final MVCore plugin;
    private final File worldsFolder;

    public BukkitWorldAPI(MVCore plugin, File worldsFolder) {
        this.plugin = plugin;
        this.worldsFolder = worldsFolder;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Callable<?> createWorld(WorldCreationSettings settings) throws WorldCreationException {
        if (Bukkit.getWorld(settings.name()) != null)
            return null;
        
        final WorldCreator c = WorldCreator.name(settings.name());
        if (settings.env() != null)
            c.environment(Convert.toBukkit(settings.env()));
        if (settings.type() != null)
            c.type(Convert.toBukkit(settings.type()));
        if (settings.seed() != null)
            c.seed(settings.seed());

        c.generateStructures(settings.generateStructures());
        if (settings.generator() instanceof ChunkGenerator)
            c.generator((ChunkGenerator)settings.generator());
        else if (settings.generator() != null)
            throw new WorldCreationException(new BundledMessage(BukkitLanguage.WGEN_UNKNOWN_GENERATOR, settings.generator()));

        try {
            final World w = c.createWorld();
            final String name = w.getName();
            return new Callable<World>() {
                @Override
                public World call() throws Exception {
                    World w = Bukkit.getWorld(name);
                    if (w == null)
                        throw new NullPointerException("Multiverse lost track of Bukkit world '" + name + "'");
                    return w;
                }
            };
        } catch (Exception e) {
            throw new WorldCreationException(new BundledMessage(BukkitLanguage.CREATE_WORLD_ERROR, settings.name()), e);
        }
    }
}
