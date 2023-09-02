package com.onarandombox.MultiverseCore.worldnew;

import com.dumptruckman.minecraft.util.Logging;
import com.onarandombox.MultiverseCore.utils.file.FileUtils;
import com.onarandombox.MultiverseCore.worldnew.config.WorldConfig;
import com.onarandombox.MultiverseCore.worldnew.config.WorldsConfigManager;
import com.onarandombox.MultiverseCore.worldnew.options.CreateWorldOptions;
import io.vavr.control.Option;
import jakarta.inject.Inject;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.WorldCreator;
import org.jetbrains.annotations.NotNull;
import org.jvnet.hk2.annotations.Service;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

@Service
public class WorldManager {

    private final Map<String, OfflineWorld> offlineWorldsMap;
    private final Map<String, MVWorld> worldsMap;
    private final WorldsConfigManager worldsConfigManager;

    @Inject
    WorldManager(@NotNull WorldsConfigManager worldsConfigFile) {
        this.offlineWorldsMap = new HashMap<>();
        this.worldsMap = new HashMap<>();
        this.worldsConfigManager = worldsConfigFile;
    }

    public void initAllWorlds() {
        populateOfflineWorlds();
        worldsConfigManager.getAllWorldsInConfig().forEach(worldName -> {
            Logging.fine("Loading world: " + worldName);
            loadWorld(worldName);
        });
        saveWorldsConfig();
    }

    private void populateOfflineWorlds() {
        // TODO: Check for worlds that are removed after config reload
        worldsConfigManager.getAllWorldConfigs().forEach(worldConfig -> {
            OfflineWorld offlineWorld = new OfflineWorld(worldConfig.getWorldName(), worldConfig);
            offlineWorldsMap.put(worldConfig.getWorldName(), offlineWorld);
        });
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
        WorldConfig worldConfig = worldsConfigManager.addWorldConfig(options.worldName());
        worldConfig.setEnvironment(options.environment());
        worldConfig.setGenerator(options.generator());
        worldConfig.setSeed(world.getSeed());

        OfflineWorld offlineWorld = new OfflineWorld(world.getName(), worldConfig);
        offlineWorldsMap.put(options.worldName(), offlineWorld);

        MVWorld mvWorld = new MVWorld(world.getName(), worldConfig, world.getUID());
        worldsMap.put(options.worldName(), mvWorld);

        saveWorldsConfig();
    }

    public void loadWorld(@NotNull String worldName) {
        // TODO: Implement logic
    }

    public void unloadWorld(@NotNull MVWorld world) {
        // TODO: Implement logic
    }

    public void removeWorld(@NotNull MVWorld world) {
        // TODO: Implement logic
        worldsConfigManager.deleteWorldConfig(world.getName());
        saveWorldsConfig();
    }

    public void deleteWorld(@NotNull MVWorld world) {
        World bukkitWorld = world.getBukkitWorld();
        File worldFolder = bukkitWorld.getWorldFolder();

        // Unload world
        // TODO: removePlayersFromWorld?
        if (!Bukkit.unloadWorld(bukkitWorld, true)) {
            Logging.severe("Failed to unload world: " + world.getName());
            return;
        }
        if (Bukkit.getWorld(world.getName()) != null) {
            Logging.severe("World still loaded: " + world.getName());
            return;
        }
        worldsMap.remove(world.getName());

        // Remove world from config
        offlineWorldsMap.remove(world.getName());
        worldsConfigManager.deleteWorldConfig(world.getName());
        saveWorldsConfig();

        // Erase world files from disk
        // TODO: Config options to keep certain files
        FileUtils.deleteFolder(worldFolder);
    }

    public Option<OfflineWorld> getOfflineWorld(@NotNull String worldName) {
        return Option.of(offlineWorldsMap.get(worldName));
    }

    public Option<MVWorld> getMVWorld(@NotNull String worldName) {
        return Option.of(worldsMap.get(worldName));
    }

    public void saveWorldsConfig() {
        worldsConfigManager.save();
    }
}
