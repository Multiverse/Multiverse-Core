package com.onarandombox.MultiverseCore.worldnew;

import com.dumptruckman.minecraft.util.Logging;
import com.google.common.base.Strings;
import com.onarandombox.MultiverseCore.api.BlockSafety;
import com.onarandombox.MultiverseCore.api.LocationManipulation;
import com.onarandombox.MultiverseCore.api.SafeTTeleporter;
import com.onarandombox.MultiverseCore.utils.file.FileUtils;
import com.onarandombox.MultiverseCore.utils.result.Result;
import com.onarandombox.MultiverseCore.worldnew.config.WorldConfig;
import com.onarandombox.MultiverseCore.worldnew.config.WorldsConfigManager;
import com.onarandombox.MultiverseCore.worldnew.generators.GeneratorProvider;
import com.onarandombox.MultiverseCore.worldnew.options.CreateWorldOptions;
import com.onarandombox.MultiverseCore.worldnew.options.ImportWorldOptions;
import com.onarandombox.MultiverseCore.worldnew.results.CreateWorldResult;
import com.onarandombox.MultiverseCore.worldnew.results.DeleteWorldResult;
import com.onarandombox.MultiverseCore.worldnew.results.ImportWorldResult;
import com.onarandombox.MultiverseCore.worldnew.results.LoadWorldResult;
import com.onarandombox.MultiverseCore.worldnew.results.RemoveWorldResult;
import com.onarandombox.MultiverseCore.worldnew.results.UnloadWorldResult;
import com.onarandombox.MultiverseCore.worldnew.results.WorldFailureReason;
import io.vavr.control.Option;
import jakarta.inject.Inject;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.WorldCreator;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jvnet.hk2.annotations.Service;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.onarandombox.MultiverseCore.utils.message.MessageReplacement.replace;

@Service
public class WorldManager {

    private final Map<String, OfflineWorld> offlineWorldsMap;
    private final Map<String, MVWorld> worldsMap;
    private final List<String> unloadTracker;
    private final List<String> loadTracker;
    private final WorldsConfigManager worldsConfigManager;
    private final WorldNameChecker worldNameChecker;
    private final GeneratorProvider generatorProvider;
    private final BlockSafety blockSafety;
    private final SafeTTeleporter safeTTeleporter;
    private final LocationManipulation locationManipulation;

    @Inject
    WorldManager(
            @NotNull WorldsConfigManager worldsConfigManager,
            @NotNull WorldNameChecker worldNameChecker,
            @NotNull GeneratorProvider generatorProvider,
            @NotNull BlockSafety blockSafety,
            @NotNull SafeTTeleporter safeTTeleporter,
            @NotNull LocationManipulation locationManipulation
    ) {
        this.offlineWorldsMap = new HashMap<>();
        this.worldsMap = new HashMap<>();
        this.unloadTracker = new ArrayList<>();
        this.loadTracker = new ArrayList<>();

        this.worldsConfigManager = worldsConfigManager;
        this.worldNameChecker = worldNameChecker;
        this.generatorProvider = generatorProvider;
        this.blockSafety = blockSafety;
        this.safeTTeleporter = safeTTeleporter;
        this.locationManipulation = locationManipulation;
    }

    public void initAllWorlds() {
        populateWorldFromConfig();
        loadDefaultWorlds();
        getOfflineWorlds().forEach(world -> {
            if (isMVWorld(world) || !world.getAutoLoad()) {
                return;
            }
            loadWorld(world).onFailure((failure) -> {
                Logging.severe("Failed to load world %s: %s", world.getName(), failure);
            });
        });
        saveWorldsConfig();
    }

    private void populateWorldFromConfig() {
        worldsConfigManager.load();
        worldsConfigManager.getAllWorldConfigs().forEach(worldConfig -> {
            getMVWorld(worldConfig.getWorldName())
                    .peek(mvWorld -> mvWorld.setWorldConfig(worldConfig));
            getOfflineWorld(worldConfig.getWorldName())
                    .peek(offlineWorld -> offlineWorld.setWorldConfig(worldConfig))
                    .onEmpty(() -> {
                        OfflineWorld offlineWorld = new OfflineWorld(worldConfig.getWorldName(), worldConfig);
                        offlineWorldsMap.put(offlineWorld.getName(), offlineWorld);
                    });
        });
    }

    private void loadDefaultWorlds() {
        Bukkit.getWorlds().forEach((world) -> {
            if (isOfflineWorld(world.getName())) {
                return;
            }
            importWorld(ImportWorldOptions.worldName(world.getName())
                    .environment(world.getEnvironment())
                    .generator(generatorProvider.getDefaultGeneratorForWorld(world.getName()))
            );
        });
    }

    /**
     * Creates a new world.
     *
     * @param options   The options for customizing the creation of a new world.
     */
    public Result<CreateWorldResult.Success, CreateWorldResult.Failure> createWorld(CreateWorldOptions options) {
        if (!worldNameChecker.isValidWorldName(options.worldName())) {
            return Result.failure(CreateWorldResult.Failure.INVALID_WORLDNAME, replace("{world}").with(options.worldName()));
        }

        if (getMVWorld(options.worldName()).isDefined()) {
            return Result.failure(CreateWorldResult.Failure.WORLD_EXIST_LOADED, replace("{world}").with(options.worldName()));
        }

        if (getOfflineWorld(options.worldName()).isDefined()) {
            return Result.failure(CreateWorldResult.Failure.WORLD_EXIST_OFFLINE, replace("{world}").with(options.worldName()));
        }

        File worldFolder = new File(Bukkit.getWorldContainer(), options.worldName());
        if (worldFolder.exists()) {
            return Result.failure(CreateWorldResult.Failure.WORLD_EXIST_FOLDER, replace("{world}").with(options.worldName()));
        }

        String parsedGenerator = Strings.isNullOrEmpty(options.generator())
                ? generatorProvider.getDefaultGeneratorForWorld(options.worldName())
                : options.generator();

        // Create bukkit world
        this.loadTracker.add(options.worldName());
        World world = WorldCreator.name(options.worldName())
                .environment(options.environment())
                .generateStructures(options.generateStructures())
                .generator(Strings.isNullOrEmpty(parsedGenerator) ? null : parsedGenerator)
                .seed(options.seed())
                .type(options.worldType())
                .createWorld();
        this.loadTracker.remove(options.worldName());
        if (world == null) {
            Logging.severe("Failed to create world: " + options.worldName());
            return Result.failure(CreateWorldResult.Failure.BUKKIT_CREATION_FAILED, replace("{world}").with(options.worldName()));
        }
        Logging.fine("Loaded bukkit world: " + world.getName());

        // Our multiverse world
        MVWorld mvWorld = newMVWorld(world);
        mvWorld.getWorldConfig().setGenerator(Strings.isNullOrEmpty(parsedGenerator) ? "" : options.generator());
        saveWorldsConfig();
        return Result.success(CreateWorldResult.Success.CREATED, replace("{world}").with(world.getName()));
    }

    public Result<ImportWorldResult.Success, ImportWorldResult.Failure> importWorld(ImportWorldOptions options) {
        if (!worldNameChecker.isValidWorldName(options.worldName())) {
            return Result.failure(ImportWorldResult.Failure.INVALID_WORLDNAME, replace("{world}").with(options.worldName()));
        }

        if (!worldNameChecker.isValidWorldFolder(options.worldName())) {
            return Result.failure(ImportWorldResult.Failure.WORLD_FOLDER_INVALID, replace("{world}").with(options.worldName()));
        }

        if (isMVWorld(options.worldName())) {
            return Result.failure(ImportWorldResult.Failure.WORLD_EXIST_LOADED, replace("{world}").with(options.worldName()));
        }

        if (isOfflineWorld(options.worldName())) {
            return Result.failure(ImportWorldResult.Failure.WORLD_EXIST_OFFLINE, replace("{world}").with(options.worldName()));
        }

        String parsedGenerator = Strings.isNullOrEmpty(options.generator())
                ? generatorProvider.getDefaultGeneratorForWorld(options.worldName())
                : options.generator();

        // Create bukkit world
        this.loadTracker.add(options.worldName());
        World world = WorldCreator.name(options.worldName())
                .environment(options.environment())
                .generator(Strings.isNullOrEmpty(parsedGenerator) ? null : options.generator())
                .createWorld();
        this.loadTracker.remove(options.worldName());
        if (world == null) {
            Logging.severe("Failed to create world: " + options.worldName());
            return Result.failure(ImportWorldResult.Failure.BUKKIT_CREATION_FAILED, replace("{world}").with(options.worldName()));
        }
        Logging.fine("Loaded bukkit world: " + world.getName());

        // Our multiverse world
        MVWorld mvWorld = newMVWorld(world);
        mvWorld.getWorldConfig().setGenerator(Strings.isNullOrEmpty(parsedGenerator) ? "" : options.generator());
        saveWorldsConfig();
        return Result.success(ImportWorldResult.Success.IMPORTED, replace("{world}").with(options.worldName()));
    }

    private MVWorld newMVWorld(World world) {
        WorldConfig worldConfig = worldsConfigManager.addWorldConfig(world.getName());

        OfflineWorld offlineWorld = new OfflineWorld(world.getName(), worldConfig);
        offlineWorldsMap.put(offlineWorld.getName(), offlineWorld);

        MVWorld mvWorld = new MVWorld(world, worldConfig, blockSafety, safeTTeleporter, locationManipulation);
        worldsMap.put(mvWorld.getName(), mvWorld);
        return mvWorld;
    }

    public Result<LoadWorldResult.Success, LoadWorldResult.Failure> loadWorld(@NotNull String worldName) {
        return getOfflineWorld(worldName)
                .map(this::loadWorld)
                .getOrElse(() -> {
                    if (worldNameChecker.isValidWorldFolder(worldName)) {
                        return Result.failure(LoadWorldResult.Failure.WORLD_EXIST_FOLDER, replace("{world}").with(worldName));
                    }
                    return Result.failure(LoadWorldResult.Failure.WORLD_NON_EXISTENT, replace("{world}").with(worldName));
                });
    }

    public Result<LoadWorldResult.Success, LoadWorldResult.Failure> loadWorld(@NotNull OfflineWorld offlineWorld) {
        if (loadTracker.contains(offlineWorld.getName())) {
            // This is to prevent recursive calls by WorldLoadEvent
            Logging.fine("World already loading: " + offlineWorld.getName());
            return Result.failure(LoadWorldResult.Failure.WORLD_ALREADY_LOADING, replace("{world}").with(offlineWorld.getName()));
        }

        if (isMVWorld(offlineWorld)) {
            Logging.severe("World already loaded: " + offlineWorld.getName());
            return Result.failure(LoadWorldResult.Failure.WORLD_EXIST_LOADED, replace("{world}").with(offlineWorld.getName()));
        }

        // TODO: Reduce copy paste from createWorld method
        this.loadTracker.add(offlineWorld.getName());
        World world = WorldCreator.name(offlineWorld.getName())
                .environment(offlineWorld.getEnvironment())
                .generator(Strings.isNullOrEmpty(offlineWorld.getGenerator()) ? null : offlineWorld.getGenerator())
                .seed(offlineWorld.getSeed())
                .createWorld();
        this.loadTracker.remove(offlineWorld.getName());
        if (world == null) {
            Logging.severe("Failed to create world: " + offlineWorld.getName());
            return Result.failure(LoadWorldResult.Failure.BUKKIT_CREATION_FAILED, replace("{world}").with(offlineWorld.getName()));
        }
        Logging.fine("Loaded bukkit world: " + world.getName());

        // Our multiverse world
        WorldConfig worldConfig = worldsConfigManager.getWorldConfig(offlineWorld.getName());
        MVWorld mvWorld = new MVWorld(world, worldConfig, blockSafety, safeTTeleporter, locationManipulation);
        worldsMap.put(mvWorld.getName(), mvWorld);

        saveWorldsConfig();
        return Result.success(LoadWorldResult.Success.LOADED, replace("{world}").with(mvWorld.getName()));
    }

    public Result<UnloadWorldResult.Success, UnloadWorldResult.Failure> unloadWorld(@NotNull World world) {
        return unloadWorld(world.getName());
    }

    public Result<UnloadWorldResult.Success, UnloadWorldResult.Failure> unloadWorld(@NotNull String worldName) {
        return getMVWorld(worldName)
                .map(this::unloadWorld)
                .getOrElse(() -> {
                    if (isOfflineOnlyWorld(worldName)) {
                        return Result.failure(UnloadWorldResult.Failure.WORLD_OFFLINE, replace("{world}").with(worldName));
                    }
                    return Result.failure(UnloadWorldResult.Failure.WORLD_NON_EXISTENT, replace("{world}").with(worldName));
                });
    }

    public Result<UnloadWorldResult.Success, UnloadWorldResult.Failure> unloadWorld(@NotNull MVWorld world) {
        if (unloadTracker.contains(world.getName())) {
            // This is to prevent recursive calls by WorldUnloadEvent
            Logging.fine("World already unloading: " + world.getName());
            return Result.failure(UnloadWorldResult.Failure.WORLD_ALREADY_UNLOADING, replace("{world}").with(world.getName()));
        }

        World bukkitWorld = world.getBukkitWorld().getOrNull();
        // TODO: removePlayersFromWorld?
        unloadTracker.add(world.getName());
        boolean unloadSuccess = Bukkit.unloadWorld(bukkitWorld, true);
        unloadTracker.remove(world.getName());
        if (!unloadSuccess) {
            Logging.severe("Failed to unload world: " + world.getName());
            return Result.failure(UnloadWorldResult.Failure.BUKKIT_UNLOAD_FAILED, replace("{world}").with(world.getName()));
        }
        MVWorld mvWorld = worldsMap.remove(world.getName());
        if (mvWorld == null) {
            Logging.severe("Failed to remove world from map: " + world.getName());
            return Result.failure(UnloadWorldResult.Failure.WORLD_NON_EXISTENT, replace("{world}").with(world.getName()));
        }
        Logging.fine("Unloaded world: " + world.getName());

        mvWorld.getWorldConfig().deferenceMVWorld();
        return Result.success(UnloadWorldResult.Success.UNLOADED, replace("{world}").with(world.getName()));
    }

    public Result<RemoveWorldResult.Success, WorldFailureReason> removeWorld(@NotNull String worldName) {
        return getOfflineWorld(worldName)
                .map(this::removeWorld)
                .getOrElse(() -> Result.failure(RemoveWorldResult.Failure.WORLD_NON_EXISTENT, replace("{world}").with(worldName)));
    }

    public Result<RemoveWorldResult.Success, WorldFailureReason> removeWorld(@NotNull OfflineWorld world) {
        MVWorld mvWorld = getMVWorld(world).getOrNull();
        if (mvWorld != null) {
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

    public Result<DeleteWorldResult.Success, WorldFailureReason> deleteWorld(@NotNull String worldName) {
        return getMVWorld(worldName)
                .map(this::deleteWorld)
                .getOrElse(() -> Result.failure(DeleteWorldResult.Failure.WORLD_NON_EXISTENT, replace("{world}").with(worldName)));
    }

    public Result<DeleteWorldResult.Success, WorldFailureReason> deleteWorld(@NotNull MVWorld world) {
        File worldFolder = world.getBukkitWorld().map(World::getWorldFolder).getOrNull();
        if (worldFolder == null || !worldNameChecker.isValidWorldFolder(worldFolder)) {
            Logging.severe("Failed to get world folder for world: " + world.getName());
            return Result.failure(DeleteWorldResult.Failure.WORLD_FOLDER_NOT_FOUND, replace("{world}").with(world.getName()));
        }

        var result = removeWorld(world);
        if (result.isFailure()) {
            return Result.failure(result.getFailureReason());
        }

        // Erase world files from disk
        // TODO: Config options to keep certain files
        if (!FileUtils.deleteFolder(worldFolder)) {
            Logging.severe("Failed to delete world folder: " + worldFolder);
            return Result.failure(DeleteWorldResult.Failure.FAILED_TO_DELETE_FOLDER, replace("{world}").with(world.getName()));
        }

        return Result.success(DeleteWorldResult.Success.DELETED, replace("{world}").with(world.getName()));
    }

    public List<String> getPotentialWorlds() {
        File[] files = Bukkit.getWorldContainer().listFiles();
        if (files == null) {
            return Collections.emptyList();
        }
        return Arrays.stream(files)
                .filter(file -> !isOfflineWorld(file.getName()))
                .filter(worldNameChecker::isValidWorldFolder)
                .map(File::getName)
                .toList();
    }

    public Option<OfflineWorld> getOfflineOnlyWorld(@Nullable String worldName) {
        return isMVWorld(worldName) ? Option.none() : Option.of(offlineWorldsMap.get(worldName));
    }

    public Collection<OfflineWorld> getOfflineOnlyWorlds() {
        return offlineWorldsMap.values().stream().filter(world -> !world.isLoaded()).toList();
    }

    public boolean isOfflineOnlyWorld(@Nullable String worldName) {
        return !isMVWorld(worldName) && isOfflineWorld(worldName);
    }

    public Option<OfflineWorld> getOfflineWorld(@Nullable String worldName) {
        return Option.of(offlineWorldsMap.get(worldName));
    }

    public Collection<OfflineWorld> getOfflineWorlds() {
        return offlineWorldsMap.values();
    }

    public boolean isOfflineWorld(@Nullable String worldName) {
        return offlineWorldsMap.containsKey(worldName);
    }

    public Option<MVWorld> getMVWorld(@Nullable World world) {
        return world == null ? Option.none() : Option.of(worldsMap.get(world.getName()));
    }

    public Option<MVWorld> getMVWorld(@Nullable OfflineWorld world) {
        return world == null ? Option.none() : Option.of(worldsMap.get(world.getName()));
    }

    public Option<MVWorld> getMVWorld(@Nullable String worldName) {
        return Option.of(worldsMap.get(worldName));
    }

    public Collection<MVWorld> getMVWorlds() {
        return worldsMap.values();
    }

    public boolean isMVWorld(@Nullable World world) {
        return world != null && isMVWorld(world.getName());
    }

    public boolean isMVWorld(@Nullable OfflineWorld world) {
        return world != null && isMVWorld(world.getName());
    }

    public boolean isMVWorld(@Nullable String worldName) {
        return worldsMap.containsKey(worldName);
    }

    public boolean saveWorldsConfig() {
        return worldsConfigManager.save();
    }
}
