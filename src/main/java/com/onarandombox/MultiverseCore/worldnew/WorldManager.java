package com.onarandombox.MultiverseCore.worldnew;

import com.dumptruckman.minecraft.util.Logging;
import com.onarandombox.MultiverseCore.worldnew.config.WorldConfig;
import com.onarandombox.MultiverseCore.worldnew.config.WorldsConfigFile;
import jakarta.inject.Inject;
import org.bukkit.configuration.ConfigurationSection;
import org.jetbrains.annotations.NotNull;
import org.jvnet.hk2.annotations.Service;

@Service
public class WorldManager {
    private final WorldsConfigFile worldsConfigFile;

    @Inject
    WorldManager(@NotNull WorldsConfigFile worldsConfigFile) {
        this.worldsConfigFile = worldsConfigFile;
        this.worldsConfigFile.load();
    }

    public void loadAllWorlds() {
        for (String worldName : worldsConfigFile.getAllWorldsInConfig()) {
            Logging.fine("Loading world: " + worldName);
            loadWorld(worldName);
        }
        saveWorldsConfig();
    }

    public void addWorld(String worldName) {
        ConfigurationSection worldConfigSection = worldsConfigFile.addWorldConfigSection(worldName);
        WorldConfig worldConfig = new WorldConfig(worldConfigSection);
        //todo
        saveWorldsConfig();
    }

    public void loadWorld(String worldName) {
        ConfigurationSection worldConfigSection = worldsConfigFile.addWorldConfigSection(worldName);
        WorldConfig worldConfig = new WorldConfig(worldConfigSection);
        //todo
    }

    public void unloadWorld() {
        //todo
    }

    public void removeWorld(String worldName) {
        //todo
        worldsConfigFile.deleteWorldConfigSection(worldName);
    }

    public void deleteWorld(String worldName) {
        //todo
        worldsConfigFile.deleteWorldConfigSection(worldName);
    }

    public void getMVWorld(String worldName) {
        //todo
    }

    public void getUnloadedWorld(String worldName) {
        //todo
    }

    public void saveWorldsConfig() {
        worldsConfigFile.save();
    }
}
