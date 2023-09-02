package com.onarandombox.MultiverseCore.worldnew.config;

import com.onarandombox.MultiverseCore.MultiverseCore;
import jakarta.inject.Inject;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.jetbrains.annotations.NotNull;
import org.jvnet.hk2.annotations.Service;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@Service
public class WorldsConfigManager {
    private static final String CONFIG_FILENAME = "worlds2.yml";

    private final Map<String, WorldConfig> worldConfigMap;
    private final File worldConfigFile;
    private YamlConfiguration worldsConfig;

    @Inject
    public WorldsConfigManager(@NotNull MultiverseCore core) {
        worldConfigMap = new HashMap<>();
        worldConfigFile = core.getDataFolder().toPath().resolve(CONFIG_FILENAME).toFile();
        load();
    }

    private void load() {
        // TODO: Migration from old worlds.yml
        worldsConfig = YamlConfiguration.loadConfiguration(worldConfigFile);
        for (String worldName : getAllWorldsInConfig()) {
            worldConfigMap.put(worldName, new WorldConfig(worldName, getWorldConfigSection(worldName)));
        }
    }

    public boolean isLoaded() {
        return worldsConfig != null;
    }

    public void save() {
        try {
            worldsConfig.save(worldConfigFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public Collection<String> getAllWorldsInConfig() {
        return worldsConfig.getKeys(false);
    }

    public Collection<WorldConfig> getAllWorldConfigs() {
        return worldConfigMap.values();
    }

    public WorldConfig getWorldConfig(String worldName) {
        return worldConfigMap.get(worldName);
    }

    public WorldConfig addWorldConfig(String worldName) {
        if (worldConfigMap.containsKey(worldName)) {
            throw new IllegalArgumentException("WorldConfig for world " + worldName + " already exists.");
        }
        WorldConfig worldConfig = new WorldConfig(worldName, getWorldConfigSection(worldName));
        worldConfigMap.put(worldName, worldConfig);
        return worldConfig;
    }

    public void deleteWorldConfig(String worldName) {
        worldConfigMap.remove(worldName);
        worldsConfig.set(worldName, null);
    }

    private ConfigurationSection getWorldConfigSection(String worldName) {
        return worldsConfig.isConfigurationSection(worldName)
                ? worldsConfig.getConfigurationSection(worldName) : worldsConfig.createSection(worldName);
    }
}
