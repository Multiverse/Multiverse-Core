package com.onarandombox.MultiverseCore.worldnew;

import com.dumptruckman.minecraft.util.Logging;
import com.google.common.base.Strings;
import com.onarandombox.MultiverseCore.utils.file.FileUtils;
import com.onarandombox.MultiverseCore.utils.result.Result;
import com.onarandombox.MultiverseCore.utils.result.SuccessReason;
import com.onarandombox.MultiverseCore.world.WorldNameChecker;
import com.onarandombox.MultiverseCore.worldnew.config.WorldConfig;
import com.onarandombox.MultiverseCore.worldnew.config.WorldsConfigManager;
import com.onarandombox.MultiverseCore.worldnew.options.CreateWorldOptions;
import com.onarandombox.MultiverseCore.worldnew.results.CreateWorldResult;
import com.onarandombox.MultiverseCore.worldnew.results.DeleteWorldResult;
import com.onarandombox.MultiverseCore.worldnew.results.LoadWorldResult;
import com.onarandombox.MultiverseCore.worldnew.results.RemoveWorldResult;
import com.onarandombox.MultiverseCore.worldnew.results.UnloadWorldResult;
import io.vavr.control.Option;
import jakarta.inject.Inject;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.WorldCreator;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
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
        getOfflineWorlds().forEach(offlineWorld -> {
            if (!offlineWorld.getAutoLoad()) {
                return;
            }
            loadWorld(offlineWorld).onFailure((failure) -> {
                Logging.severe("Failed to load world %s: %s", offlineWorld.getName(), failure);
            });
        });
        saveWorldsConfig();
    }

    private void populateOfflineWorlds() {
        // TODO: Check for worlds that are removed after config reload
        worldsConfigManager.getAllWorldConfigs().forEach(worldConfig -> {
            OfflineWorld offlineWorld = new OfflineWorld(worldConfig.getWorldName(), worldConfig);
            offlineWorldsMap.put(worldConfig.getWorldName(), offlineWorld);
            Logging.fine("Loaded world %s from config", worldConfig.getWorldName());
        });
    }

    /**
     * Creates a new world.
     *
     * @param options   The options for customizing the creation of a new world.
     */
    public Result<CreateWorldResult.Success, CreateWorldResult.Failure> createWorld(CreateWorldOptions options) {
        if (!WorldNameChecker.isValidWorldName(options.worldName())) {
            return Result.failure(CreateWorldResult.Failure.INVALID_WORLDNAME);
        }

        if (getMVWorld(options.worldName()).isDefined()) {
            return Result.failure(CreateWorldResult.Failure.WORLD_EXIST_LOADED);
        }

        if (getOfflineWorld(options.worldName()).isDefined()) {
            return Result.failure(CreateWorldResult.Failure.WORLD_EXIST_OFFLINE);
        }

        File worldFolder = new File(Bukkit.getWorldContainer(), options.worldName());
        if (worldFolder.exists()) {
            return Result.failure(CreateWorldResult.Failure.WORLD_EXIST_FOLDER);
        }

        // Create bukkit world
        World world = WorldCreator.name(options.worldName())
                .environment(options.environment())
                .generateStructures(options.generateStructures())
                .generator(Strings.isNullOrEmpty(options.generator()) ? null : options.generator())
                .seed(options.seed())
                .type(options.worldType())
                .createWorld();
        if (world == null) {
            Logging.severe("Failed to create world: " + options.worldName());
            return Result.failure(CreateWorldResult.Failure.BUKKIT_CREATION_FAILED);
        }
        Logging.fine("Loaded bukkit world: " + world.getName());

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
        return Result.success(CreateWorldResult.Success.CREATED);
    }

    public Result<LoadWorldResult.Success, LoadWorldResult.Failure> loadWorld(@NotNull String worldName) {
        return getOfflineWorld(worldName)
                .map(this::loadWorld)
                .getOrElse(() -> Result.failure(LoadWorldResult.Failure.WORLD_NON_EXISTENT));
    }

    public Result<LoadWorldResult.Success, LoadWorldResult.Failure> loadWorld(@NotNull OfflineWorld offlineWorld) {
        if (isMVWorld(offlineWorld)) {
            Logging.severe("World already loaded: " + offlineWorld.getName());
            return Result.failure(LoadWorldResult.Failure.WORLD_EXIST_LOADED);
        }

        // TODO: Reduce copy paste from createWorld method
        World world = WorldCreator.name(offlineWorld.getName())
                .environment(offlineWorld.getEnvironment())
                .generator(Strings.isNullOrEmpty(offlineWorld.getGenerator()) ? null : offlineWorld.getGenerator())
                .seed(offlineWorld.getSeed())
                .createWorld();
        if (world == null) {
            Logging.severe("Failed to create world: " + offlineWorld.getName());
            return Result.failure(LoadWorldResult.Failure.BUKKIT_CREATION_FAILED);
        }
        Logging.fine("Loaded bukkit world: " + world.getName());

        // Our multiverse world
        WorldConfig worldConfig = worldsConfigManager.getWorldConfig(offlineWorld.getName());
        MVWorld mvWorld = new MVWorld(world.getName(), worldConfig, world.getUID());
        worldsMap.put(mvWorld.getName(), mvWorld);

        saveWorldsConfig();
        return Result.success(LoadWorldResult.Success.LOADED);
    }

    public Result<UnloadWorldResult.Success, UnloadWorldResult.Failure> unloadWorld(@NotNull String worldName) {
        return getMVWorld(worldName)
                .map(this::unloadWorld)
                .getOrElse(() -> Result.failure(UnloadWorldResult.Failure.WORLD_NON_EXISTENT));
    }

    public Result<UnloadWorldResult.Success, UnloadWorldResult.Failure> unloadWorld(@NotNull MVWorld world) {
        World bukkitWorld = world.getBukkitWorld();
        // TODO: removePlayersFromWorld?
        if (!Bukkit.unloadWorld(bukkitWorld, true)) {
            Logging.severe("Failed to unload world: " + world.getName());
            return Result.failure(UnloadWorldResult.Failure.BUKKIT_UNLOAD_FAILED);
        }
        worldsMap.remove(world.getName());
        return Result.success(UnloadWorldResult.Success.UNLOADED);
    }

    public Result<RemoveWorldResult.Success, UnloadWorldResult.Failure> removeWorld(@NotNull String worldName) {
        return getOfflineWorld(worldName)
                .map(this::removeWorld)
                .getOrElse(() -> Result.failure(RemoveWorldResult.Failure.WORLD_NON_EXISTENT));
    }

    public Result<RemoveWorldResult.Success, UnloadWorldResult.Failure> removeWorld(@NotNull OfflineWorld world) {
        if (world instanceof MVWorld mvWorld) {
            var result = unloadWorld(mvWorld);
            if (result.isFailure()) {
                return Result.failure(result.getFailureReason());
            }
        }

        // Remove world from config
        offlineWorldsMap.remove(world.getName());
        worldsConfigManager.deleteWorldConfig(world.getName());
        saveWorldsConfig();

        return Result.success(RemoveWorldResult.Success.REMOVED);
    }

    public Result<DeleteWorldResult.Success, UnloadWorldResult.Failure> deleteWorld(@NotNull String worldName) {
        return getMVWorld(worldName)
                .map(this::deleteWorld)
                .getOrElse(() -> Result.failure(DeleteWorldResult.Failure.WORLD_NON_EXISTENT));
    }

    public Result<DeleteWorldResult.Success, UnloadWorldResult.Failure> deleteWorld(@NotNull MVWorld world) {
        // TODO: Attempt to load if unloaded so we can actually delete the world

        File worldFolder = world.getBukkitWorld().getWorldFolder();
        var result = removeWorld(world);
        if (result.isFailure()) {
            return Result.failure(result.getFailureReason());
        }

        // Erase world files from disk
        // TODO: Config options to keep certain files
        if (!FileUtils.deleteFolder(worldFolder)) {
            Logging.severe("Failed to delete world folder: " + worldFolder);
            return Result.failure(DeleteWorldResult.Failure.FAILED_TO_DELETE_FOLDER);
        }

        return Result.success(DeleteWorldResult.Success.DELETED);
    }

    public Option<OfflineWorld> getOfflineWorld(@Nullable String worldName) {
        return Option.of(offlineWorldsMap.get(worldName));
    }

    public Collection<OfflineWorld> getOfflineWorlds() {
        return offlineWorldsMap.values();
    }

    public Option<MVWorld> getMVWorld(@Nullable String worldName) {
        return Option.of(worldsMap.get(worldName));
    }

    public Collection<MVWorld> getMVWorlds() {
        return worldsMap.values();
    }

    public boolean isMVWorld(@Nullable OfflineWorld world) {
        return world != null && isMVWorld(world.getName());
    }

    public boolean isMVWorld(@Nullable String worldName) {
        return worldsMap.containsKey(worldName);
    }

    public void saveWorldsConfig() {
        worldsConfigManager.save();
    }
}
