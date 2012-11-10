package com.mvplugin.core;

import com.dumptruckman.minecraft.pluginbase.logging.Logging;
import com.dumptruckman.minecraft.pluginbase.messaging.BundledMessage;
import com.mvplugin.core.api.BukkitMultiverseWorld;
import com.mvplugin.core.api.WorldProperties;
import com.mvplugin.core.util.BukkitLanguage;
import com.mvplugin.core.util.Convert;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.WorldCreator;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;

public class BukkitWorldManager extends AbstractWorldManager {

    private final MultiverseCorePlugin plugin;
    private final File worldsFolder;

    private final Map<String, WorldProperties> worldPropertiesMap;
    private final Map<String, String> defaultGens;

    public BukkitWorldManager(MultiverseCorePlugin plugin) {
        super(plugin);
        this.plugin = plugin;
        this.worldsFolder = new File(plugin.getDataFolder(), "worlds");
        this.worldPropertiesMap = new HashMap<String, WorldProperties>();
        this.defaultGens = new HashMap<String, String>();
        initializeDefaultWorldGenerators();
        initializeWorlds();
    }

    private void initializeDefaultWorldGenerators() {
        File[] files = this.plugin.getServerFolder().listFiles(new FilenameFilter() {
            @Override
            public boolean accept(File file, String s) {
                return s.equalsIgnoreCase("bukkit.yml");
            }
        });
        if (files != null && files.length == 1) {
            FileConfiguration bukkitConfig = YamlConfiguration.loadConfiguration(files[0]);
            if (bukkitConfig.isConfigurationSection("worlds")) {
                Set<String> keys = bukkitConfig.getConfigurationSection("worlds").getKeys(false);
                for (String key : keys) {
                    defaultGens.put(key, bukkitConfig.getString("worlds." + key + ".generator", ""));
                }
            }
        } else {
            Logging.warning("Could not read 'bukkit.yml'. Any Default worldgenerators will not be loaded!");
        }
    }

    private void initializeWorlds() {
        StringBuilder builder = new StringBuilder();
        for (final World w : this.plugin.getServer().getWorlds()) {
            try {
                worldsMap.put(w.getName(), getBukkitWorld(w));
                if (builder.length() != 0) {
                    builder.append(", ");
                }
                builder.append(w.getName());
            } catch (IOException e) {
                Logging.severe("Multiverse could not initialize loaded Bukkit world '%s'", w.getName());
            }
        }

        for (File file : getPotentialWorldFiles()) {
            final String worldName = getWorldNameFromFile(file);
            if (worldsMap.containsKey(worldName)) {
                continue;
            }
            try {
                WorldProperties worldProperties = getWorldProperties(file);
                if (worldProperties.get(WorldProperties.AUTO_LOAD)) {
                    addWorld(worldName, worldProperties.get(WorldProperties.ENVIRONMENT), null, null,
                            null, worldProperties.get(WorldProperties.GENERATOR),
                            worldProperties.get(WorldProperties.ADJUST_SPAWN));
                    if (builder.length() != 0) {
                        builder.append(", ");
                    }
                    builder.append(worldName);
                } else {
                    Logging.fine("Not loading '%s' because it is set to autoLoad: false", worldName);
                }
            } catch (IOException e) {
                Logging.getLogger().log(Level.WARNING, String.format("Could not load world from file '%s'", file), e);
            } catch (WorldCreationException e) {
                Logging.getLogger().log(Level.WARNING, String.format("Error while attempting to load world '%s'", worldName), e);
            }
        }

        // Simple Output to the Console to show how many Worlds were loaded.
        Logging.config("Multiverse is now managing: %s", builder.toString());
    }

    private String getWorldNameFromFile(final File file) {
        final String simpleName = file.getName();
        if (simpleName.endsWith(".yml")) {
            return simpleName.substring(0, simpleName.indexOf(".yml"));
        }
        return simpleName;
    }

    private File[] getPotentialWorldFiles() {
        return worldsFolder.listFiles(new FilenameFilter() {
            @Override
            public boolean accept(File dir, String name) {
                return name.endsWith(".yml");
            }
        });
    }

    private WorldProperties getWorldProperties(final File file) throws IOException {
        return new YamlWorldProperties(file);
    }

    @Override
    public WorldProperties getWorldProperties(String worldName) throws IOException {
        final World world = Bukkit.getWorld(worldName);
        if (world != null) {
            worldName = world.getName();
        }
        if (worldPropertiesMap.containsKey(worldName)) {
            return worldPropertiesMap.get(worldName);
        } else {
            final WorldProperties worldProperties = getWorldProperties(new File(worldsFolder, worldName + ".yml"));
            worldPropertiesMap.put(worldName, worldProperties);
            return worldProperties;
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public BukkitMultiverseWorld createWorld(final WorldCreationSettings settings) throws WorldCreationException {
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
            return getBukkitWorld(w);
        } catch (Exception e) {
            throw new WorldCreationException(new BundledMessage(BukkitLanguage.CREATE_WORLD_ERROR, settings.name()), e);
        }
    }

    private BukkitWorld getBukkitWorld(final World world) throws IOException {
        return new BukkitWorld(world, getWorldProperties(world.getName()));
    }

    @Override
    public List<String> getUnloadedWorlds() {
        return Collections.unmodifiableList(new ArrayList<String>(worldPropertiesMap.keySet()));
    }
}
