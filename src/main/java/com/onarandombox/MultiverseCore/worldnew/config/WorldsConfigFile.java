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

@Service
public class WorldsConfigFile {
    private static final String CONFIG_FILENAME = "worlds2.yml";

    private final File worldConfigFile;
    private YamlConfiguration worldConfig;

    @Inject
    public WorldsConfigFile(@NotNull MultiverseCore core) {
        worldConfigFile = core.getDataFolder().toPath().resolve(CONFIG_FILENAME).toFile();
    }

    public void load() {
        // TODO: Migration from old worlds.yml
        worldConfig = YamlConfiguration.loadConfiguration(worldConfigFile);
    }

    public boolean isLoaded() {
        return worldConfig != null;
    }

    public void save() {
        try {
            worldConfig.save(worldConfigFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public Collection<String> getAllWorldsInConfig() {
        return worldConfig.getKeys(false);
    }

    public ConfigurationSection getWorldConfigSection(String worldName) {
        return worldConfig.isConfigurationSection(worldName)
                ? worldConfig.getConfigurationSection(worldName) : worldConfig.createSection(worldName);
    }

    public WorldConfig getWorldConfig(String worldName) {
        return new WorldConfig(getWorldConfigSection(worldName));
    }

    public void deleteWorldConfigSection(String worldName) {
        worldConfig.set(worldName, null);
    }
}
