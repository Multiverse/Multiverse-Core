package com.mvplugin.core;

import com.dumptruckman.minecraft.pluginbase.messaging.BundledMessage;
import com.mvplugin.core.api.BukkitMultiverseWorld;
import com.mvplugin.core.api.WorldProperties;
import com.mvplugin.core.util.BukkitLanguage;
import com.mvplugin.core.util.Convert;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.WorldCreator;
import org.bukkit.plugin.Plugin;

import java.io.File;
import java.io.IOException;

public class BukkitWorldManager extends AbstractWorldManager<BukkitMultiverseWorld> {

    private final MultiverseCorePlugin plugin;
    private final File worldsFolder;

    public BukkitWorldManager(MultiverseCorePlugin plugin) {
        super(plugin);
        this.plugin = plugin;
        this.worldsFolder = new File(plugin.getDataFolder(), "worlds");
    }

    @Override
    public WorldProperties getWorldProperties(final String worldName) throws IOException {
        return new YamlWorldProperties(plugin, new File(worldsFolder, worldName + ".yml"));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public BukkitMultiverseWorld createWorld(WorldCreationSettings settings) throws WorldCreationException {
        if (Bukkit.getWorld(settings.name()) != null) {
            return null;
        }
        
        final WorldCreator c = WorldCreator.name(settings.name());
        if (settings.env() != null) {
            c.environment(Convert.toBukkit(settings.env()));
        }
        if (settings.type() != null) {
            c.type(Convert.toBukkit(settings.type()));
        }
        if (settings.seed() != null) {
            c.seed(settings.seed());
        }
        if (settings.generateStructures() != null) {
            c.generateStructures(settings.generateStructures());
        }
        if (settings.generator() != null) {
            final String[] split = settings.generator().split(":", 2);
            final String id = (split.length > 1) ? split[1] : null;
            final Plugin plugin = Bukkit.getPluginManager().getPlugin(split[0]);

            if (plugin == null) {
                throw new WorldCreationException(new BundledMessage(BukkitLanguage.WGEN_UNKNOWN_GENERATOR, settings.generator()));
            } else if (!plugin.isEnabled()) {
                throw new WorldCreationException(new BundledMessage(BukkitLanguage.WGEN_DISABLED_GENERATOR, settings.generator()));
            } else {
                c.generator(plugin.getDefaultWorldGenerator(settings.name(), id));
            }
        }

        try {
            final World w = c.createWorld();
            return new BukkitWorld(w, getWorldProperties(w.getName()));
        } catch (Exception e) {
            throw new WorldCreationException(new BundledMessage(BukkitLanguage.CREATE_WORLD_ERROR, settings.name()), e);
        }
    }
}
