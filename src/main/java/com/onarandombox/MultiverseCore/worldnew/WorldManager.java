package com.onarandombox.MultiverseCore.worldnew;

import com.dumptruckman.minecraft.util.Logging;
import com.onarandombox.MultiverseCore.worldnew.config.WorldConfig;
import com.onarandombox.MultiverseCore.worldnew.config.WorldsConfigFile;
import com.onarandombox.MultiverseCore.worldnew.options.CreateWorldOptions;
import io.vavr.control.Option;
import jakarta.inject.Inject;
import org.bukkit.World;
import org.bukkit.WorldCreator;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jvnet.hk2.annotations.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class WorldManager {

    private final Map<String, OfflineWorld> offlineWorldsMap;
    private final Map<String, MVWorld> worldsMap;
    private final WorldsConfigFile worldsConfigFile;

    @Inject
    WorldManager(@NotNull WorldsConfigFile worldsConfigFile) {
        this.offlineWorldsMap = new HashMap<>();
        this.worldsMap = new HashMap<>();
        this.worldsConfigFile = worldsConfigFile;
    }

    public void loadAllWorlds() {
        for (String worldName : worldsConfigFile.getAllWorldsInConfig()) {
            Logging.fine("Loading world: " + worldName);
            loadWorld(worldName);
        }
        saveWorldsConfig();
    }

    /**
     * Creates a new world.
     *
     * @param options   The options for customizing the creation of a new world.
     */
    public void createWorld(CreateWorldOptions options) {
        // Check valid worldname

        // Check if world already exists

        // Create bukkit world
        World world = WorldCreator.name(options.worldName())
                .environment(options.environment())
                .generateStructures(options.generateStructures())
                .generator(options.generator())
                .seed(options.seed())
                .type(options.worldType())
                .createWorld();
        if (world == null) {
            // TODO: Better result handling
            Logging.severe("Failed to create world: " + options.worldName());
            return;
        }

        // Our multiverse world
        WorldConfig worldConfig = worldsConfigFile.getWorldConfig(options.worldName());
        worldConfig.setEnvironment(options.environment());
        worldConfig.setGenerator(options.generator());
        worldConfig.setSeed(world.getSeed());

        OfflineWorld offlineWorld = new OfflineWorld(world.getName(), worldConfig);
        offlineWorldsMap.put(options.worldName(), offlineWorld);

        MVWorld mvWorld = new MVWorld(world.getName(), worldConfig, world.getUID());
        worldsMap.put(options.worldName(), mvWorld);

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

    public Option<OfflineWorld> getOfflineWorld(@NotNull String worldName) {
        return Option.of(offlineWorldsMap.get(worldName));
    }

    public Option<MVWorld> getMVWorld(@NotNull String worldName) {
        return Option.of(worldsMap.get(worldName));
    }

    public void saveWorldsConfig() {
        worldsConfigFile.save();
    }
}
