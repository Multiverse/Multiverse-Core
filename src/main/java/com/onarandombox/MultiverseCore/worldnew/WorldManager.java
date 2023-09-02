package com.onarandombox.MultiverseCore.worldnew;

import com.dumptruckman.minecraft.util.Logging;
import com.onarandombox.MultiverseCore.worldnew.config.WorldConfig;
import com.onarandombox.MultiverseCore.worldnew.config.WorldsConfigFile;
import com.onarandombox.MultiverseCore.worldnew.options.AddWorldOptions;
import jakarta.inject.Inject;
import org.bukkit.configuration.ConfigurationSection;
import org.jetbrains.annotations.NotNull;
import org.jvnet.hk2.annotations.Service;

import java.util.List;

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

    /**
     * Adds a world to the worlds config file.
     *
     * @param options   The options for customizing the creation of a new world.
     */
    public void addWorld(AddWorldOptions options) {
        WorldConfig worldConfig = worldsConfigFile.getWorldConfig(options.worldName());
        // TODO: Implement logic
        saveWorldsConfig();
    }

    public void loadWorld(String worldName) {
        WorldConfig worldConfig = worldsConfigFile.getWorldConfig(worldName);
        // TODO: Implement logic
    }

    public void unloadWorld() {
        // TODO: Implement logic
    }

    public void removeWorld(String worldName) {
        // TODO: Implement logic
        worldsConfigFile.deleteWorldConfigSection(worldName);
        saveWorldsConfig();
    }

    public void deleteWorld(String worldName) {
        // TODO: Implement logic
        worldsConfigFile.deleteWorldConfigSection(worldName);
        saveWorldsConfig();
    }

    public void getMVWorld(String worldName) {
        // TODO: Implement logic
    }

    public void getUnloadedWorld(String worldName) {
        // TODO: Implement logic
    }

    public void saveWorldsConfig() {
        worldsConfigFile.save();
    }
}
