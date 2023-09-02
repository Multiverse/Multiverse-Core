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
import java.util.Collection;
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
        getOfflineWorlds().forEach(this::loadWorld);
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
        // TODO: Check valid worldname

        // TODO: Check if world already exists

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
        offlineWorldsMap.put(offlineWorld.getName(), offlineWorld);

        MVWorld mvWorld = new MVWorld(world.getName(), worldConfig, world.getUID());
        worldsMap.put(mvWorld.getName(), mvWorld);

        saveWorldsConfig();
    }

    public void loadWorld(@NotNull OfflineWorld offlineWorld) {
        // TODO: Reduce copy paste from createWorld method
        World world = WorldCreator.name(offlineWorld.getName())
                .environment(offlineWorld.getEnvironment())
                .generator(offlineWorld.getGenerator())
                .seed(offlineWorld.getSeed())
                .createWorld();
        if (world == null) {
            // TODO: Better result handling
            Logging.severe("Failed to create world: " + offlineWorld.getName());
            return;
        }

        // Our multiverse world
        WorldConfig worldConfig = worldsConfigManager.getWorldConfig(offlineWorld.getName());
        MVWorld mvWorld = new MVWorld(world.getName(), worldConfig, world.getUID());
        worldsMap.put(mvWorld.getName(), mvWorld);

        saveWorldsConfig();
    }

    public void unloadWorld(@NotNull MVWorld world) {
        World bukkitWorld = world.getBukkitWorld();
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
    }

    public void removeWorld(@NotNull OfflineWorld world) {
        if (world instanceof MVWorld mvWorld) {
            unloadWorld(mvWorld);
        }

        // Remove world from config
        offlineWorldsMap.remove(world.getName());
        worldsConfigManager.deleteWorldConfig(world.getName());
        saveWorldsConfig();
    }

    public void deleteWorld(@NotNull MVWorld world) {
        // TODO: Attempt to load if unloaded so we can actually delete the world

        File worldFolder = world.getBukkitWorld().getWorldFolder();
        removeWorld(world);

        // Erase world files from disk
        // TODO: Config options to keep certain files
        FileUtils.deleteFolder(worldFolder);
    }

    public Option<OfflineWorld> getOfflineWorld(@NotNull String worldName) {
        return Option.of(offlineWorldsMap.get(worldName));
    }

    public Collection<OfflineWorld> getOfflineWorlds() {
        return offlineWorldsMap.values();
    }

    public Option<MVWorld> getMVWorld(@NotNull String worldName) {
        return Option.of(worldsMap.get(worldName));
    }

    public void saveWorldsConfig() {
        worldsConfigManager.save();
    }
}
