package com.onarandombox.MultiverseCore.worldnew;

import com.dumptruckman.minecraft.util.Logging;
import com.google.common.base.Strings;
import com.onarandombox.MultiverseCore.api.BlockSafety;
import com.onarandombox.MultiverseCore.api.LocationManipulation;
import com.onarandombox.MultiverseCore.api.SafeTTeleporter;
import com.onarandombox.MultiverseCore.utils.result.Result;
import com.onarandombox.MultiverseCore.worldnew.config.WorldConfig;
import com.onarandombox.MultiverseCore.worldnew.config.WorldsConfigManager;
import com.onarandombox.MultiverseCore.worldnew.generators.GeneratorProvider;
import com.onarandombox.MultiverseCore.worldnew.helpers.DataStore.GameRulesStore;
import com.onarandombox.MultiverseCore.worldnew.helpers.DataTransfer;
import com.onarandombox.MultiverseCore.worldnew.helpers.FilesManipulator;
import com.onarandombox.MultiverseCore.worldnew.options.CloneWorldOptions;
import com.onarandombox.MultiverseCore.worldnew.options.CreateWorldOptions;
import com.onarandombox.MultiverseCore.worldnew.options.ImportWorldOptions;
import com.onarandombox.MultiverseCore.worldnew.options.RegenWorldOptions;
import com.onarandombox.MultiverseCore.worldnew.results.CloneWorldResult;
import com.onarandombox.MultiverseCore.worldnew.results.CreateWorldResult;
import com.onarandombox.MultiverseCore.worldnew.results.DeleteWorldResult;
import com.onarandombox.MultiverseCore.worldnew.results.ImportWorldResult;
import com.onarandombox.MultiverseCore.worldnew.results.LoadWorldResult;
import com.onarandombox.MultiverseCore.worldnew.results.RegenWorldResult;
import com.onarandombox.MultiverseCore.worldnew.results.RemoveWorldResult;
import com.onarandombox.MultiverseCore.worldnew.results.UnloadWorldResult;
import io.vavr.control.Option;
import io.vavr.control.Try;
import jakarta.inject.Inject;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.WorldCreator;
import org.bukkit.WorldType;
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
import static com.onarandombox.MultiverseCore.worldnew.helpers.DataStore.WorldBorderStore;
import static com.onarandombox.MultiverseCore.worldnew.helpers.DataStore.WorldConfigStore;

/**
 * This manager contains all the world managing functions that your heart desires!
 */
@Service
public class WorldManager {
    private static final List<String> CLONE_IGNORE_FILES = Arrays.asList("uid.dat", "session.lock");

    private final Map<String, OfflineWorld> offlineWorldsMap;
    private final Map<String, MVWorld> worldsMap;
    private final List<String> unloadTracker;
    private final List<String> loadTracker;
    private final WorldsConfigManager worldsConfigManager;
    private final WorldNameChecker worldNameChecker;
    private final GeneratorProvider generatorProvider;
    private final FilesManipulator filesManipulator;
    private final BlockSafety blockSafety;
    private final SafeTTeleporter safeTTeleporter;
    private final LocationManipulation locationManipulation;

    @Inject
    WorldManager(
            @NotNull WorldsConfigManager worldsConfigManager,
            @NotNull WorldNameChecker worldNameChecker,
            @NotNull GeneratorProvider generatorProvider,
            @NotNull FilesManipulator filesManipulator,
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
        this.filesManipulator = filesManipulator;
        this.blockSafety = blockSafety;
        this.safeTTeleporter = safeTTeleporter;
        this.locationManipulation = locationManipulation;
    }

    /**
     * Loads all worlds from the worlds config.
     */
    public void initAllWorlds() {
        if (!populateWorldFromConfig()) {
            return;
        }
        loadDefaultWorlds();
        autoLoadOfflineWorlds();
        saveWorldsConfig();
    }

    /**
     * Generate offline worlds from the worlds config.
     */
    private boolean populateWorldFromConfig() {
        Try<Void> load = worldsConfigManager.load();
        if (load.isFailure()) {
            Logging.severe("Failed to load worlds config: " + load.getCause().getMessage());
            load.getCause().printStackTrace();
            return false;
        }
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
        return true;
    }

    /**
     * Load worlds that are already loaded by bukkit before Multiverse-Core is loaded.
     */
    private void loadDefaultWorlds() {
        Bukkit.getWorlds().forEach(world -> {
            if (isOfflineWorld(world.getName())) {
                return;
            }
            importWorld(ImportWorldOptions.worldName(world.getName())
                    .environment(world.getEnvironment())
                    .generator(generatorProvider.getDefaultGeneratorForWorld(world.getName())));
        });
    }

    /**
     * Loads all worlds that are set to autoload.
     */
    private void autoLoadOfflineWorlds() {
        getOfflineWorlds().forEach(world -> {
            if (isMVWorld(world) || !world.getAutoLoad()) {
                return;
            }
            loadWorld(world).onFailure(failure -> Logging.severe("Failed to load world %s: %s", world.getName(), failure));
        });
    }

    /**
     * Creates a new world.
     *
     * @param options   The options for customizing the creation of a new world.
     */
    public Result<CreateWorldResult.Success, CreateWorldResult.Failure> createWorld(CreateWorldOptions options) {
        // Params validations
        if (!worldNameChecker.isValidWorldName(options.worldName())) {
            return Result.failure(CreateWorldResult.Failure.INVALID_WORLDNAME,
                    replace("{world}").with(options.worldName()));
        }
        if (getMVWorld(options.worldName()).isDefined()) {
            return Result.failure(CreateWorldResult.Failure.WORLD_EXIST_LOADED,
                    replace("{world}").with(options.worldName()));
        }
        if (getOfflineWorld(options.worldName()).isDefined()) {
            return Result.failure(CreateWorldResult.Failure.WORLD_EXIST_OFFLINE,
                    replace("{world}").with(options.worldName()));
        }
        File worldFolder = new File(Bukkit.getWorldContainer(), options.worldName());
        if (worldFolder.exists()) {
            return Result.failure(CreateWorldResult.Failure.WORLD_EXIST_FOLDER,
                    replace("{world}").with(options.worldName()));
        }

        String parsedGenerator = parseGenerator(options.worldName(), options.generator());
        return createBukkitWorld(WorldCreator.name(options.worldName())
                .environment(options.environment())
                .generateStructures(options.generateStructures())
                .generator(parsedGenerator)
                .seed(options.seed())
                .type(options.worldType()))
                .fold(
                        exception -> Result.failure(CreateWorldResult.Failure.BUKKIT_CREATION_FAILED,
                                replace("{world}").with(options.worldName()),
                                replace("{error}").with(exception.getMessage())),
                        world -> {
                            newMVWorld(world, parsedGenerator);
                            return Result.success(CreateWorldResult.Success.CREATED,
                                    replace("{world}").with(world.getName()));
                        });
    }

    /**
     * Imports an existing world folder.
     *
     * @param options   The options for customizing the import of an existing world folder.
     * @return The result of the import.
     */
    public Result<ImportWorldResult.Success, ImportWorldResult.Failure> importWorld(ImportWorldOptions options) {
        // Params validations
        if (!worldNameChecker.isValidWorldName(options.worldName())) {
            return Result.failure(ImportWorldResult.Failure.INVALID_WORLDNAME,
                    replace("{world}").with(options.worldName()));
        }
        if (!worldNameChecker.isValidWorldFolder(options.worldName())) {
            return Result.failure(ImportWorldResult.Failure.WORLD_FOLDER_INVALID,
                    replace("{world}").with(options.worldName()));
        }
        if (isMVWorld(options.worldName())) {
            return Result.failure(ImportWorldResult.Failure.WORLD_EXIST_LOADED,
                    replace("{world}").with(options.worldName()));
        }
        if (isOfflineWorld(options.worldName())) {
            return Result.failure(ImportWorldResult.Failure.WORLD_EXIST_OFFLINE,
                    replace("{world}").with(options.worldName()));
        }

        String parsedGenerator = parseGenerator(options.worldName(), options.generator());
        return createBukkitWorld(WorldCreator.name(options.worldName())
                .environment(options.environment())
                .generator(parsedGenerator))
                .fold(
                        exception -> Result.failure(ImportWorldResult.Failure.BUKKIT_CREATION_FAILED,
                                replace("{world}").with(options.worldName()),
                                replace("{error}").with(exception.getMessage())),
                        world -> {
                            newMVWorld(world, parsedGenerator);
                            return Result.success(ImportWorldResult.Success.IMPORTED,
                                    replace("{world}").with(options.worldName()));
                        });
    }

    private @Nullable String parseGenerator(@NotNull String worldName, @Nullable String generator) {
        return Strings.isNullOrEmpty(generator)
                ? generatorProvider.getDefaultGeneratorForWorld(worldName)
                : generator;
    }

    private void newMVWorld(@NotNull World world, @Nullable String generator) {
        WorldConfig worldConfig = worldsConfigManager.addWorldConfig(world.getName());

        OfflineWorld offlineWorld = new OfflineWorld(world.getName(), worldConfig);
        offlineWorldsMap.put(offlineWorld.getName(), offlineWorld);

        MVWorld mvWorld = new MVWorld(world, worldConfig, blockSafety, safeTTeleporter, locationManipulation);
        worldsMap.put(mvWorld.getName(), mvWorld);
        mvWorld.getWorldConfig().setGenerator(generator == null ? "" : generator);
        saveWorldsConfig();
    }

    /**
     * Loads an existing offline world.
     *
     * @param worldName The name of the world to load.
     * @return The result of the load.
     */
    public Result<LoadWorldResult.Success, LoadWorldResult.Failure> loadWorld(@NotNull String worldName) {
        return getOfflineWorld(worldName)
                .map(this::loadWorld)
                .getOrElse(() -> worldNameChecker.isValidWorldFolder(worldName)
                        ? Result.failure(LoadWorldResult.Failure.WORLD_EXIST_FOLDER,
                                replace("{world}").with(worldName))
                        : Result.failure(LoadWorldResult.Failure.WORLD_NON_EXISTENT,
                                replace("{world}").with(worldName)));
    }

    /**
     * Loads an existing offline world.
     *
     * @param offlineWorld  The offline world to load.
     * @return The result of the load.
     */
    public Result<LoadWorldResult.Success, LoadWorldResult.Failure> loadWorld(@NotNull OfflineWorld offlineWorld) {
        // Params validations
        if (loadTracker.contains(offlineWorld.getName())) {
            // This is to prevent recursive calls by WorldLoadEvent
            Logging.fine("World already loading: " + offlineWorld.getName());
            return Result.failure(LoadWorldResult.Failure.WORLD_ALREADY_LOADING,
                    replace("{world}").with(offlineWorld.getName()));
        }
        if (isMVWorld(offlineWorld)) {
            Logging.severe("World already loaded: " + offlineWorld.getName());
            return Result.failure(LoadWorldResult.Failure.WORLD_EXIST_LOADED,
                    replace("{world}").with(offlineWorld.getName()));
        }

        return createBukkitWorld(WorldCreator.name(offlineWorld.getName())
                .environment(offlineWorld.getEnvironment())
                .generator(Strings.isNullOrEmpty(offlineWorld.getGenerator()) ? null : offlineWorld.getGenerator())
                .seed(offlineWorld.getSeed())).fold(
                        exception -> Result.failure(LoadWorldResult.Failure.BUKKIT_CREATION_FAILED,
                                replace("{world}").with(offlineWorld.getName()),
                                replace("{error}").with(exception.getMessage())),
                        world -> {
                            WorldConfig worldConfig = worldsConfigManager.getWorldConfig(offlineWorld.getName());
                            MVWorld mvWorld = new MVWorld(world, worldConfig, blockSafety,
                                    safeTTeleporter, locationManipulation);
                            worldsMap.put(mvWorld.getName(), mvWorld);
                            saveWorldsConfig();
                            return Result.success(LoadWorldResult.Success.LOADED,
                                    replace("{world}").with(mvWorld.getName()));
                        });
    }

    /**
     * Unloads an existing multiverse world. It will still remain as an offline world.
     *
     * @param world The bukkit world to unload.
     * @return The result of the unload action.
     */
    public Result<UnloadWorldResult.Success, UnloadWorldResult.Failure> unloadWorld(@NotNull World world) {
        return unloadWorld(world.getName());
    }

    /**
     * Unloads an existing multiverse world. It will still remain as an offline world.
     *
     * @param worldName The name of the world to unload.
     * @return The result of the unload action.
     */
    public Result<UnloadWorldResult.Success, UnloadWorldResult.Failure> unloadWorld(@NotNull String worldName) {
        return getMVWorld(worldName)
                .map(this::unloadWorld)
                .getOrElse(() -> isOfflineOnlyWorld(worldName)
                        ? Result.failure(UnloadWorldResult.Failure.WORLD_OFFLINE,
                                replace("{world}").with(worldName))
                        : Result.failure(UnloadWorldResult.Failure.WORLD_NON_EXISTENT,
                                replace("{world}").with(worldName)));
    }

    /**
     * Unloads an existing multiverse world. It will still remain as an offline world.
     *
     * @param world The multiverse world to unload.
     * @return The result of the unload action.
     */
    public Result<UnloadWorldResult.Success, UnloadWorldResult.Failure> unloadWorld(@NotNull MVWorld world) {
        if (unloadTracker.contains(world.getName())) {
            // This is to prevent recursive calls by WorldUnloadEvent
            Logging.fine("World already unloading: " + world.getName());
            return Result.failure(UnloadWorldResult.Failure.WORLD_ALREADY_UNLOADING,
                    replace("{world}").with(world.getName()));
        }

        // TODO: removePlayersFromWorld?

        return unloadBukkitWorld(world.getBukkitWorld().getOrNull()).fold(
                exception -> Result.failure(UnloadWorldResult.Failure.BUKKIT_UNLOAD_FAILED,
                        replace("{world}").with(world.getName()),
                        replace("{error}").with(exception.getMessage())),
                success -> Option.of(worldsMap.remove(world.getName())).fold(
                        () -> {
                            Logging.severe("Failed to remove world from map: " + world.getName());
                            return Result.failure(UnloadWorldResult.Failure.WORLD_NON_EXISTENT,
                                    replace("{world}").with(world.getName()));
                        },
                        mvWorld -> {
                            Logging.fine("Removed MVWorld from map: " + world.getName());
                            mvWorld.getWorldConfig().deferenceMVWorld();
                            return Result.success(UnloadWorldResult.Success.UNLOADED,
                                    replace("{world}").with(world.getName()));
                        }));
    }

    /**
     * Removes an existing multiverse world. It will be deleted from the worlds config and will no longer be an offline world.
     * World files will not be deleted.
     *
     * @param worldName The name of the world to remove.
     * @return The result of the remove.
     */
    public Result<RemoveWorldResult.Success, RemoveWorldResult.Failure> removeWorld(@NotNull String worldName) {
        return getOfflineWorld(worldName)
                .map(this::removeWorld)
                .getOrElse(() -> Result.failure(RemoveWorldResult.Failure.WORLD_NON_EXISTENT, replace("{world}").with(worldName)));
    }

    /**
     * Removes an existing multiverse world. It will be deleted from the worlds config and will no longer be an offline world.
     * World files will not be deleted.
     *
     * @param world The multiverse world to remove.
     * @return The result of the remove.
     */
    public Result<RemoveWorldResult.Success, RemoveWorldResult.Failure> removeWorld(@NotNull OfflineWorld world) {
        return getMVWorld(world).fold(
                () -> removeWorldFromConfig(world),
                this::removeWorld);
    }

    /**
     * Removes an existing multiverse world. It will be deleted from the worlds config and will no longer be an offline
     * world. World files will not be deleted.
     *
     * @param world The multiverse world to remove.
     * @return The result of the remove.
     */
    public Result<RemoveWorldResult.Success, RemoveWorldResult.Failure> removeWorld(@NotNull MVWorld world) {
        var result = unloadWorld(world);
        if (result.isFailure()) {
            return Result.failure(RemoveWorldResult.Failure.UNLOAD_FAILED, result.getReasonMessage());
        }
        return removeWorldFromConfig(world);
    }

    /**
     * Removes an existing multiverse world from the world's config. It will no longer be an offline world.
     *
     * @param world The multiverse world to remove.
     * @return The result of the remove.
     */
    private Result<RemoveWorldResult.Success, RemoveWorldResult.Failure>
            removeWorldFromConfig(@NotNull OfflineWorld world) {
        // Remove world from config
        offlineWorldsMap.remove(world.getName());
        worldsConfigManager.deleteWorldConfig(world.getName());
        saveWorldsConfig();

        return Result.success(RemoveWorldResult.Success.REMOVED);
    }

    /**
     * Deletes an existing multiverse world entirely. World will be loaded if it is not already loaded.
     * Warning: This will delete all world files.
     *
     * @param worldName The name of the world to delete.
     * @return The result of the delete action.
     */
    public Result<DeleteWorldResult.Success, DeleteWorldResult.Failure> deleteWorld(@NotNull String worldName) {
        return getOfflineWorld(worldName)
                .map(this::deleteWorld)
                .getOrElse(() -> Result.failure(DeleteWorldResult.Failure.WORLD_NON_EXISTENT,
                        replace("{world}").with(worldName)));
    }

    /**
     * Deletes an existing multiverse world entirely. World will be loaded if it is not already loaded.
     * Warning: This will delete all world files.
     *
     * @param world The offline world to delete.
     * @return The result of the delete action.
     */
    public Result<DeleteWorldResult.Success, DeleteWorldResult.Failure> deleteWorld(@NotNull OfflineWorld world) {
        return getMVWorld(world).fold(
                () -> {
                    var result = loadWorld(world);
                    if (result.isFailure()) {
                        return Result.failure(DeleteWorldResult.Failure.LOAD_FAILED,
                                replace("{world}").with(world.getName()));
                    }
                    return deleteWorld(world);
                },
                this::deleteWorld);
    }

    /**
     * Deletes an existing multiverse world entirely. Warning: This will delete all world files.
     *
     * @param world The multiverse world to delete.
     * @return The result of the delete action.
     */
    public Result<DeleteWorldResult.Success, DeleteWorldResult.Failure> deleteWorld(@NotNull MVWorld world) {
        File worldFolder = world.getBukkitWorld().map(World::getWorldFolder).getOrNull();
        if (worldFolder == null || !worldNameChecker.isValidWorldFolder(worldFolder)) {
            Logging.severe("Failed to get world folder for world: " + world.getName());
            return Result.failure(DeleteWorldResult.Failure.WORLD_FOLDER_NOT_FOUND,
                    replace("{world}").with(world.getName()));
        }

        var result = removeWorld(world);
        if (result.isFailure()) {
            return Result.failure(DeleteWorldResult.Failure.REMOVE_FAILED, result.getReasonMessage());
        }

        // Erase world files from disk
        // TODO: Possible config options to keep certain files
        return filesManipulator.deleteFolder(worldFolder).fold(
                exception -> Result.failure(DeleteWorldResult.Failure.FAILED_TO_DELETE_FOLDER,
                        replace("{world}").with(world.getName()),
                        replace("{error}").with(exception.getMessage())),
                success -> Result.success(DeleteWorldResult.Success.DELETED,
                        replace("{world}").with(world.getName())));
    }

    /**
     * Clones an existing multiverse world.
     *
     * @param options   The options for customizing the cloning of a world.
     */
    public Result<CloneWorldResult.Success, CloneWorldResult.Failure> cloneWorld(@NotNull CloneWorldOptions options) {
        return cloneWorldValidateWorld(options)
                .onSuccessThen(s -> cloneWorldCopyFolder(options))
                .onSuccessThen(s -> importWorld(
                        ImportWorldOptions.worldName(options.newWorldName())
                                .environment(options.world().getEnvironment())
                                .generator(options.world().getGenerator()))
                        .fold(
                                failure -> Result.failure(CloneWorldResult.Failure.IMPORT_FAILED, failure.getReasonMessage()),
                                success -> Result.success()))
                .onSuccessThen(s -> getMVWorld(options.newWorldName()).fold(
                        () -> Result.failure(CloneWorldResult.Failure.MV_WORLD_FAILED,
                                replace("{world}").with(options.newWorldName())),
                        mvWorld -> {
                            cloneWorldTransferData(options, mvWorld);
                            saveWorldsConfig();
                            return Result.success(CloneWorldResult.Success.CLONED,
                                    replace("{world}").with(options.world().getName()),
                                    replace("{newworld}").with(mvWorld.getName()));
                        }));
    }

    private Result<CloneWorldResult.Success, CloneWorldResult.Failure>
            cloneWorldValidateWorld(@NotNull CloneWorldOptions options) {
        String newWorldName = options.newWorldName();
        if (!worldNameChecker.isValidWorldName(newWorldName)) {
            Logging.severe("Invalid world name: " + newWorldName);
            return Result.failure(CloneWorldResult.Failure.INVALID_WORLDNAME, replace("{world}").with(newWorldName));
        }
        if (worldNameChecker.isValidWorldFolder(newWorldName)) {
            return Result.failure(CloneWorldResult.Failure.WORLD_EXIST_FOLDER, replace("{world}").with(newWorldName));
        }
        if (isMVWorld(newWorldName)) {
            Logging.severe("World already loaded: " + newWorldName);
            return Result.failure(CloneWorldResult.Failure.WORLD_EXIST_LOADED, replace("{world}").with(newWorldName));
        }
        if (isOfflineWorld(newWorldName)) {
            Logging.severe("World already exist offline: " + newWorldName);
            return Result.failure(CloneWorldResult.Failure.WORLD_EXIST_OFFLINE, replace("{world}").with(newWorldName));
        }
        return Result.success();
    }

    private Result<CloneWorldResult.Success, CloneWorldResult.Failure>
            cloneWorldCopyFolder(@NotNull CloneWorldOptions options) {
        File worldFolder = options.world().getBukkitWorld().map(World::getWorldFolder).getOrNull(); // TODO: Check null?
        File newWorldFolder = new File(Bukkit.getWorldContainer(), options.newWorldName());
        return filesManipulator.copyFolder(worldFolder, newWorldFolder, CLONE_IGNORE_FILES).fold(
                exception -> Result.failure(CloneWorldResult.Failure.COPY_FAILED,
                        replace("{world}").with(options.world().getName()),
                        replace("{error}").with(exception.getMessage())),
                success -> Result.success());
    }

    private void cloneWorldTransferData(@NotNull CloneWorldOptions options, @NotNull MVWorld newWorld) {
        MVWorld world = options.world();
        DataTransfer<MVWorld> dataTransfer = new DataTransfer<>();
        if (options.keepWorldConfig()) {
            dataTransfer.addDataStore(new WorldConfigStore(), world);
        }
        if (options.keepGameRule()) {
            dataTransfer.addDataStore(new GameRulesStore(), world);
        }
        if (options.keepWorldBorder()) {
            dataTransfer.addDataStore(new WorldBorderStore(), world);
        }
        dataTransfer.pasteAllTo(newWorld);
    }

    /**
     * Regenerates a world.
     *
     * @param options   The options for customizing the regeneration of a world.
     */
    public Result<RegenWorldResult.Success, RegenWorldResult.Failure> regenWorld(@NotNull RegenWorldOptions options) {
        // TODO: Teleport players out of world, and back in after regen
        MVWorld world = options.world();

        DataTransfer<MVWorld> dataTransfer = new DataTransfer<>();
        if (options.keepWorldConfig()) {
            dataTransfer.addDataStore(new WorldConfigStore(), world);
        }
        if (options.keepGameRule()) {
            dataTransfer.addDataStore(new GameRulesStore(), world);
        }
        if (options.keepWorldBorder()) {
            dataTransfer.addDataStore(new WorldBorderStore(), world);
        }

        CreateWorldOptions createWorldOptions = CreateWorldOptions.worldName(world.getName())
                .environment(world.getEnvironment())
                .generateStructures(world.canGenerateStructures().getOrElse(true))
                .generator(world.getGenerator())
                .seed(options.seed())
                .worldType(world.getWorldType().getOrElse(WorldType.NORMAL));

        var deleteResult = deleteWorld(world);
        if (deleteResult.isFailure()) {
            return Result.failure(RegenWorldResult.Failure.DELETE_FAILED, deleteResult.getReasonMessage());
        }

        var createResult = createWorld(createWorldOptions);
        if (createResult.isFailure()) {
            return Result.failure(RegenWorldResult.Failure.CREATE_FAILED, createResult.getReasonMessage());
        }

        getMVWorld(createWorldOptions.worldName()).peek(newWorld -> {
            dataTransfer.pasteAllTo(newWorld);
            saveWorldsConfig();
        });
        return Result.success(RegenWorldResult.Success.REGENERATED, replace("{world}").with(world.getName()));
    }

    /**
     * Creates a bukkit world.
     *
     * @param worldCreator  The world parameters.
     * @return The created world.
     */
    private Try<World> createBukkitWorld(WorldCreator worldCreator) {
        this.loadTracker.add(worldCreator.name());
        try {
            World world = worldCreator.createWorld();
            this.loadTracker.remove(worldCreator.name());
            if (world == null) {
                Logging.severe("Failed to create bukkit world: " + worldCreator.name());
                return Try.failure(new Exception("World created was null!")); // TODO: Localize this
            }
            Logging.fine("Bukkit created world: " + world.getName());
            return Try.success(world);
        } catch (Exception e) {
            this.loadTracker.remove(worldCreator.name());
            Logging.severe("Failed to create bukkit world: " + worldCreator.name());
            e.printStackTrace();
            return Try.failure(e);
        }
    }

    /**
     * Unloads a bukkit world.
     *
     * @param world The bukkit world to unload.
     * @return The unloaded world.
     */
    private Try<Void> unloadBukkitWorld(World world) {
        try {
            unloadTracker.add(world.getName());
            boolean unloadSuccess = Bukkit.unloadWorld(world, true);
            unloadTracker.remove(world.getName());
            if (unloadSuccess) {
                Logging.fine("Bukkit unloaded world: " + world.getName());
                return Try.success(null);
            }
            return Try.failure(new Exception("Is this the default world? You can't unload the default world!")); // TODO: Localize this, maybe with MultiverseException
        } catch (Exception e) {
            unloadTracker.remove(world.getName());
            Logging.severe("Failed to unload bukkit world: " + world.getName());
            e.printStackTrace();
            return Try.failure(e);
        }
    }

    /**
     * Gets a list of all potential worlds that can be loaded from the server folders.
     * Checks based on folder contents and name.
     *
     * @return A list of all potential worlds.
     */
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

    /**
     * Get an offline world that is not loaded.
     *
     * @param worldName The name of the world to get.
     * @return The offline world if it exists.
     */
    public Option<OfflineWorld> getOfflineOnlyWorld(@Nullable String worldName) {
        return isMVWorld(worldName) ? Option.none() : Option.of(offlineWorldsMap.get(worldName));
    }

    /**
     * Get a list of all offline worlds that are not loaded.
     *
     * @return A list of all offline worlds that are not loaded.
     */
    public Collection<OfflineWorld> getOfflineOnlyWorlds() {
        return offlineWorldsMap.values().stream().filter(world -> !world.isLoaded()).toList();
    }

    /**
     * Check if a world is an offline world that is not loaded.
     *
     * @param worldName The name of the world to check.
     * @return True if the world is an offline world that is not loaded.
     */
    public boolean isOfflineOnlyWorld(@Nullable String worldName) {
        return !isMVWorld(worldName) && isOfflineWorld(worldName);
    }

    /**
     * Get an offline world that may or may not be loaded.
     *
     * @param worldName The name of the world to get.
     * @return The offline world if it exists.
     */
    public Option<OfflineWorld> getOfflineWorld(@Nullable String worldName) {
        return Option.of(offlineWorldsMap.get(worldName));
    }

    /**
     * Get a list of all offline worlds that may or may not be loaded.
     *
     * @return A list of all offline worlds that may or may not be loaded.
     */
    public Collection<OfflineWorld> getOfflineWorlds() {
        return offlineWorldsMap.values();
    }

    /**
     * Check if a world is an offline world that may or may not be loaded.
     *
     * @param worldName The name of the world to check.
     * @return True if the world is an offline world that may or may not be loaded.
     */
    public boolean isOfflineWorld(@Nullable String worldName) {
        return offlineWorldsMap.containsKey(worldName);
    }

    /**
     * Get a multiverse world that is loaded.
     *
     * @param world The bukkit world that should be loaded.
     * @return The multiverse world if it exists.
     */
    public Option<MVWorld> getMVWorld(@Nullable World world) {
        return world == null ? Option.none() : Option.of(worldsMap.get(world.getName()));
    }

    /**
     * Get a multiverse world that is loaded.
     *
     * @param world The offline world that should be loaded.
     * @return The multiverse world if it exists.
     */
    public Option<MVWorld> getMVWorld(@Nullable OfflineWorld world) {
        return world == null ? Option.none() : Option.of(worldsMap.get(world.getName()));
    }

    /**
     * Get a multiverse world that is loaded.
     *
     * @param worldName The name of the world to get.
     * @return The multiverse world if it exists.
     */
    public Option<MVWorld> getMVWorld(@Nullable String worldName) {
        return Option.of(worldsMap.get(worldName));
    }

    /**
     * Get a list of all multiverse worlds that are loaded.
     *
     * @return A list of all multiverse worlds that are loaded.
     */
    public Collection<MVWorld> getMVWorlds() {
        return worldsMap.values();
    }

    /**
     * Check if a world is a multiverse world that is loaded.
     *
     * @param world The bukkit world to check.
     * @return True if the world is a multiverse world that is loaded.
     */
    public boolean isMVWorld(@Nullable World world) {
        return world != null && isMVWorld(world.getName());
    }

    /**
     * Check if a world is a multiverse world that is loaded.
     *
     * @param world The offline world to check.
     * @return True if the world is a multiverse world that is loaded.
     */
    public boolean isMVWorld(@Nullable OfflineWorld world) {
        return world != null && isMVWorld(world.getName());
    }

    /**
     * Check if a world is a multiverse world that is loaded.
     *
     * @param worldName The name of the world to check.
     * @return True if the world is a multiverse world that is loaded.
     */
    public boolean isMVWorld(@Nullable String worldName) {
        return worldsMap.containsKey(worldName);
    }

    /**
     * Saves the worlds.yml config.
     *
     * @return true if it had successfully saved the file.
     */
    public boolean saveWorldsConfig() {
        return worldsConfigManager.save()
                .onFailure(failure -> {
                    Logging.severe("Failed to save worlds config: %s", failure);
                    failure.printStackTrace();
                })
                .isSuccess();
    }
}
