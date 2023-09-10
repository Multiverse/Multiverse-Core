package com.onarandombox.MultiverseCore.worldnew;

import com.dumptruckman.minecraft.util.Logging;
import com.google.common.base.Strings;
import com.onarandombox.MultiverseCore.api.BlockSafety;
import com.onarandombox.MultiverseCore.api.LocationManipulation;
import com.onarandombox.MultiverseCore.api.SafeTTeleporter;
import com.onarandombox.MultiverseCore.utils.message.MessageReplacement;
import com.onarandombox.MultiverseCore.utils.result.FailureReason;
import com.onarandombox.MultiverseCore.utils.result.Attempt;
import com.onarandombox.MultiverseCore.worldnew.config.WorldConfig;
import com.onarandombox.MultiverseCore.worldnew.config.WorldsConfigManager;
import com.onarandombox.MultiverseCore.worldnew.generators.GeneratorProvider;
import com.onarandombox.MultiverseCore.worldnew.helpers.DataStore.GameRulesStore;
import com.onarandombox.MultiverseCore.worldnew.helpers.DataTransfer;
import com.onarandombox.MultiverseCore.worldnew.helpers.FilesManipulator;
import com.onarandombox.MultiverseCore.worldnew.options.CloneWorldOptions;
import com.onarandombox.MultiverseCore.worldnew.options.CreateWorldOptions;
import com.onarandombox.MultiverseCore.worldnew.options.ImportWorldOptions;
import com.onarandombox.MultiverseCore.worldnew.options.KeepWorldSettingsOptions;
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
import java.util.concurrent.atomic.AtomicReference;

import static com.onarandombox.MultiverseCore.utils.message.MessageReplacement.replace;
import static com.onarandombox.MultiverseCore.worldnew.helpers.DataStore.WorldBorderStore;
import static com.onarandombox.MultiverseCore.worldnew.helpers.DataStore.WorldConfigStore;

/**
 * This manager contains all the world managing functions that your heart desires.
 */
@Service // SUPPRESS CHECKSTYLE: ClassFanOutComplexity This is the world manager, it's going to be complex.
public class WorldManager {

    private static final List<String> CLONE_IGNORE_FILES = Arrays.asList("uid.dat", "session.lock");

    private final Map<String, MultiverseWorld> worldsMap;
    private final Map<String, LoadedMultiverseWorld> loadedWorldsMap;
    private final List<String> unloadTracker;
    private final List<String> loadTracker;
    private final WorldsConfigManager worldsConfigManager;
    private final WorldNameChecker worldNameChecker;
    private final GeneratorProvider generatorProvider;
    private final FilesManipulator filesManipulator;
    private final BlockSafety blockSafety;
    private final SafeTTeleporter safetyTeleporter;
    private final LocationManipulation locationManipulation;

    @Inject
    WorldManager(
            @NotNull WorldsConfigManager worldsConfigManager,
            @NotNull WorldNameChecker worldNameChecker,
            @NotNull GeneratorProvider generatorProvider,
            @NotNull FilesManipulator filesManipulator,
            @NotNull BlockSafety blockSafety,
            @NotNull SafeTTeleporter safetyTeleporter,
            @NotNull LocationManipulation locationManipulation) {
        this.worldsMap = new HashMap<>();
        this.loadedWorldsMap = new HashMap<>();
        this.unloadTracker = new ArrayList<>();
        this.loadTracker = new ArrayList<>();

        this.worldsConfigManager = worldsConfigManager;
        this.worldNameChecker = worldNameChecker;
        this.generatorProvider = generatorProvider;
        this.filesManipulator = filesManipulator;
        this.blockSafety = blockSafety;
        this.safetyTeleporter = safetyTeleporter;
        this.locationManipulation = locationManipulation;
    }

    /**
     * Loads all worlds from the worlds config.
     *
     * @return The result of the load.
     */
    public Try<Void> initAllWorlds() {
        return updateWorldsFromConfig().andThenTry(() -> {
            loadDefaultWorlds();
            autoLoadWorlds();
            saveWorldsConfig();
        });
    }

    /**
     * Updates the current set of worlds to match the worlds config.
     *
     * @return A successful Try if the worlds.yml config was loaded successfully.
     */
    private Try<Void> updateWorldsFromConfig() {
        return worldsConfigManager.load().mapTry(result -> {
            loadNewWorldConfigs(result._1());
            removeWorldsNotInConfigs(result._2());
            return null;
        });
    }

    private void loadNewWorldConfigs(Collection<WorldConfig> newWorldConfigs) {
        newWorldConfigs.forEach(worldConfig -> getWorld(worldConfig.getWorldName())
                .peek(unloadedWorld -> unloadedWorld.setWorldConfig(worldConfig))
                .onEmpty(() -> {
                    MultiverseWorld mvWorld = new MultiverseWorld(worldConfig.getWorldName(), worldConfig);
                    worldsMap.put(mvWorld.getName(), mvWorld);
                }));
    }

    private void removeWorldsNotInConfigs(Collection<String> removedWorlds) {
        removedWorlds.forEach(worldName -> removeWorld(worldName)
                .onFailure(failure -> Logging.severe("Failed to unload world %s: %s", worldName, failure))
                .onSuccess(success -> Logging.fine("Unloaded world %s as it was removed from config", worldName)));
    }

    /**
     * Load worlds that are already loaded by bukkit before Multiverse-Core is loaded.
     */
    private void loadDefaultWorlds() {
        Bukkit.getWorlds().forEach(bukkitWorld -> {
            if (isWorld(bukkitWorld.getName())) {
                return;
            }
            importWorld(ImportWorldOptions.worldName(bukkitWorld.getName())
                    .environment(bukkitWorld.getEnvironment())
                    .generator(generatorProvider.getDefaultGeneratorForWorld(bukkitWorld.getName())));
        });
    }

    /**
     * Loads all worlds that are set to autoload.
     */
    private void autoLoadWorlds() {
        getWorlds().forEach(world -> {
            if (isLoadedWorld(world) || !world.getAutoLoad()) {
                return;
            }
            loadWorld(world).onFailure(failure -> Logging.severe("Failed to load world %s: %s",
                    world.getName(), failure));
        });
    }

    /**
     * Creates a new world.
     *
     * @param options   The options for customizing the creation of a new world.
     * @return The result of the creation.
     */
    public Attempt<LoadedMultiverseWorld, CreateWorldResult.Failure> createWorld(CreateWorldOptions options) {
        return validateCreateWorldOptions(options).mapAttempt(this::createValidatedWorld);
    }

    private Attempt<CreateWorldOptions, CreateWorldResult.Failure> validateCreateWorldOptions(
            CreateWorldOptions options) {
        if (!worldNameChecker.isValidWorldName(options.worldName())) {
            return worldActionResult(CreateWorldResult.Failure.INVALID_WORLDNAME, options.worldName());
        } else if (getLoadedWorld(options.worldName()).isDefined()) {
            return worldActionResult(CreateWorldResult.Failure.WORLD_EXIST_LOADED, options.worldName());
        } else if (getWorld(options.worldName()).isDefined()) {
            return worldActionResult(CreateWorldResult.Failure.WORLD_EXIST_UNLOADED, options.worldName());
        } else if (hasWorldFolder(options.worldName())) {
            return worldActionResult(CreateWorldResult.Failure.WORLD_EXIST_FOLDER, options.worldName());
        }
        return worldActionResult(options);
    }

    private boolean hasWorldFolder(String worldName) {
        File worldFolder = new File(Bukkit.getWorldContainer(), worldName);
        return worldFolder.exists();
    }

    private Attempt<LoadedMultiverseWorld, CreateWorldResult.Failure> createValidatedWorld(
            CreateWorldOptions options) {
        String parsedGenerator = parseGenerator(options.worldName(), options.generator());
        WorldCreator worldCreator = WorldCreator.name(options.worldName())
                .environment(options.environment())
                .generateStructures(options.generateStructures())
                .generator(parsedGenerator)
                .seed(options.seed())
                .type(options.worldType());
        return createBukkitWorld(worldCreator).fold(
                exception -> worldActionResult(CreateWorldResult.Failure.BUKKIT_CREATION_FAILED,
                        options.worldName(), exception),
                world -> {
                    LoadedMultiverseWorld loadedWorld = newLoadedMultiverseWorld(
                            world,
                            parsedGenerator,
                            options.useSpawnAdjust());
                    return worldActionResult(loadedWorld);
                });
    }

    /**
     * Imports an existing world folder.
     *
     * @param options   The options for customizing the import of an existing world folder.
     * @return The result of the import.
     */
    public Attempt<LoadedMultiverseWorld, ImportWorldResult.Failure> importWorld(
            ImportWorldOptions options) {
        return validateImportWorldOptions(options).mapAttempt(this::doImportWorld);
    }

    private Attempt<ImportWorldOptions, ImportWorldResult.Failure> validateImportWorldOptions(
            ImportWorldOptions options) {
        String worldName = options.worldName();
        if (!worldNameChecker.isValidWorldName(worldName)) {
            return worldActionResult(ImportWorldResult.Failure.INVALID_WORLDNAME, worldName);
        } else if (!worldNameChecker.isValidWorldFolder(worldName)) {
            return worldActionResult(ImportWorldResult.Failure.WORLD_FOLDER_INVALID, worldName);
        } else if (isLoadedWorld(worldName)) {
            return worldActionResult(ImportWorldResult.Failure.WORLD_EXIST_LOADED, worldName);
        } else if (isWorld(worldName)) {
            return worldActionResult(ImportWorldResult.Failure.WORLD_EXIST_UNLOADED, worldName);
        }
        return worldActionResult(options);
    }

    private Attempt<LoadedMultiverseWorld, ImportWorldResult.Failure> doImportWorld(
            ImportWorldOptions options) {
        String parsedGenerator = parseGenerator(options.worldName(), options.generator());
        WorldCreator worldCreator = WorldCreator.name(options.worldName())
                .environment(options.environment())
                .generator(parsedGenerator);
        return createBukkitWorld(worldCreator).fold(
                exception -> worldActionResult(ImportWorldResult.Failure.BUKKIT_CREATION_FAILED,
                        options.worldName(), exception),
                world -> {
                    LoadedMultiverseWorld loadedWorld = newLoadedMultiverseWorld(world,
                            parsedGenerator,
                            options.useSpawnAdjust());
                    return worldActionResult(loadedWorld);
                });
    }

    /**
     * Parses generator string and defaults to generator in bukkit.yml if not specified.
     *
     * @param worldName The name of the world.
     * @param generator The input generator string.
     * @return The parsed generator string.
     */
    private @Nullable String parseGenerator(@NotNull String worldName, @Nullable String generator) {
        return Strings.isNullOrEmpty(generator)
                ? generatorProvider.getDefaultGeneratorForWorld(worldName)
                : generator;
    }

    /**
     * Creates a new loaded multiverseWorld from a bukkit world.
     *
     * @param world         The bukkit world to create a multiverse world from.
     * @param generator     The generator string.
     * @param adjustSpawn   Whether to adjust spawn.
     */
    private LoadedMultiverseWorld newLoadedMultiverseWorld(
            @NotNull World world, @Nullable String generator, boolean adjustSpawn) {
        WorldConfig worldConfig = worldsConfigManager.addWorldConfig(world.getName());
        worldConfig.setAdjustSpawn(adjustSpawn);
        worldConfig.setGenerator(generator == null ? "" : generator);

        MultiverseWorld mvWorld = new MultiverseWorld(world.getName(), worldConfig);
        worldsMap.put(mvWorld.getName(), mvWorld);

        LoadedMultiverseWorld loadedWorld = new LoadedMultiverseWorld(
                world,
                worldConfig,
                blockSafety,
                safetyTeleporter,
                locationManipulation);
        loadedWorldsMap.put(loadedWorld.getName(), loadedWorld);
        saveWorldsConfig();
        return loadedWorld;
    }

    /**
     * Loads an existing world in config.
     *
     * @param worldName The name of the world to load.
     * @return The result of the load.
     */
    public Attempt<LoadedMultiverseWorld, LoadWorldResult.Failure> loadWorld(@NotNull String worldName) {
        return getWorld(worldName)
                .map(this::loadWorld)
                .getOrElse(() -> worldNameChecker.isValidWorldFolder(worldName)
                        ? worldActionResult(LoadWorldResult.Failure.WORLD_EXIST_FOLDER, worldName)
                        : worldActionResult(LoadWorldResult.Failure.WORLD_NON_EXISTENT, worldName));
    }

    /**
     * Loads an existing world in config.
     *
     * @param world  The world to load.
     * @return The result of the load.
     */
    public Attempt<LoadedMultiverseWorld, LoadWorldResult.Failure> loadWorld(@NotNull MultiverseWorld world) {
        return validateWorldToLoad(world).mapAttempt(this::doLoadWorld);
    }

    private Attempt<MultiverseWorld, LoadWorldResult.Failure> validateWorldToLoad(
            @NotNull MultiverseWorld mvWorld) {
        if (loadTracker.contains(mvWorld.getName())) {
            // This is to prevent recursive calls by WorldLoadEvent
            Logging.fine("World already loading: " + mvWorld.getName());
            return worldActionResult(LoadWorldResult.Failure.WORLD_ALREADY_LOADING, mvWorld.getName());
        } else if (isLoadedWorld(mvWorld)) {
            Logging.severe("World already loaded: " + mvWorld.getName());
            return worldActionResult(LoadWorldResult.Failure.WORLD_EXIST_LOADED, mvWorld.getName());
        }
        return worldActionResult(mvWorld);
    }

    private Attempt<LoadedMultiverseWorld, LoadWorldResult.Failure> doLoadWorld(@NotNull MultiverseWorld mvWorld) {
        return createBukkitWorld(WorldCreator.name(mvWorld.getName())
                .environment(mvWorld.getEnvironment())
                .generator(Strings.isNullOrEmpty(mvWorld.getGenerator()) ? null : mvWorld.getGenerator())
                .seed(mvWorld.getSeed())).fold(
                        exception -> worldActionResult(LoadWorldResult.Failure.BUKKIT_CREATION_FAILED,
                                mvWorld.getName(), exception),
                        world -> {
                            // TODO: Check worldConfig null
                            WorldConfig worldConfig = worldsConfigManager.getWorldConfig(mvWorld.getName());
                            LoadedMultiverseWorld loadedWorld = new LoadedMultiverseWorld(
                                    world,
                                    worldConfig,
                                    blockSafety,
                                    safetyTeleporter,
                                    locationManipulation);
                            loadedWorldsMap.put(loadedWorld.getName(), loadedWorld);
                            saveWorldsConfig();
                            return worldActionResult(loadedWorld);
                        });
    }

    /**
     * Unloads an existing multiverse world. It will still remain as an unloaded world in mv config.
     *
     * @param world The bukkit world to unload.
     * @return The result of the unload action.
     */
    public Attempt<MultiverseWorld, UnloadWorldResult.Failure> unloadWorld(@NotNull World world) {
        return unloadWorld(world.getName());
    }

    /**
     * Unloads an existing multiverse world. It will still remain as an unloaded world in mv config.
     *
     * @param worldName The name of the world to unload.
     * @return The result of the unload action.
     */
    public Attempt<MultiverseWorld, UnloadWorldResult.Failure> unloadWorld(@NotNull String worldName) {
        return getLoadedWorld(worldName)
                .map(this::unloadWorld)
                .getOrElse(() -> isUnloadedWorld(worldName)
                        ? worldActionResult(UnloadWorldResult.Failure.WORLD_UNLOADED, worldName)
                        : worldActionResult(UnloadWorldResult.Failure.WORLD_NON_EXISTENT, worldName));
    }

    /**
     * Unloads an existing multiverse world. It will still remain as an unloaded world.
     *
     * @param world The multiverse world to unload.
     * @return The result of the unload action.
     */
    public Attempt<MultiverseWorld, UnloadWorldResult.Failure> unloadWorld(
            @NotNull LoadedMultiverseWorld world) {
        if (unloadTracker.contains(world.getName())) {
            // This is to prevent recursive calls by WorldUnloadEvent
            Logging.fine("World already unloading: " + world.getName());
            return worldActionResult(UnloadWorldResult.Failure.WORLD_ALREADY_UNLOADING, world.getName());
        }

        // TODO: removePlayersFromWorld?

        return unloadBukkitWorld(world.getBukkitWorld().getOrNull()).fold(
                exception -> worldActionResult(UnloadWorldResult.Failure.BUKKIT_UNLOAD_FAILED,
                        world.getName(), exception),
                success -> Option.of(loadedWorldsMap.remove(world.getName())).fold(
                        () -> {
                            Logging.severe("Failed to remove world from map: " + world.getName());
                            return worldActionResult(UnloadWorldResult.Failure.WORLD_NON_EXISTENT, world.getName());
                        },
                        mvWorld -> {
                            Logging.fine("Removed MultiverseWorld from map: " + world.getName());
                            mvWorld.getWorldConfig().deferenceMVWorld();
                            return worldActionResult(getWorld(mvWorld.getName()).get());
                        }));
    }

    /**
     * Removes an existing multiverse world. It will be deleted from the worlds config and will no longer be an
     * unloaded world. World files will not be deleted.
     *
     * @param worldName The name of the world to remove.
     * @return The result of the remove.
     */
    public Attempt<String, RemoveWorldResult.Failure> removeWorld(
            @NotNull String worldName) {
        return getWorld(worldName)
                .map(this::removeWorld)
                .getOrElse(() -> worldActionResult(RemoveWorldResult.Failure.WORLD_NON_EXISTENT, worldName));
    }

    /**
     * Removes an existing multiverse world. It will be deleted from the worlds config and will no longer be an
     * unloaded world. World files will not be deleted.
     *
     * @param world The multiverse world to remove.
     * @return The result of the remove.
     */
    public Attempt<String, RemoveWorldResult.Failure> removeWorld(@NotNull MultiverseWorld world) {
        return getLoadedWorld(world).fold(
                () -> removeWorldFromConfig(world),
                this::removeWorld);
    }

    /**
     * Removes an existing multiverse world. It will be deleted from the worlds config and will no longer be an
     * unloaded world. World files will not be deleted.
     *
     * @param loadedWorld The multiverse world to remove.
     * @return The result of the remove.
     */
    public Attempt<String, RemoveWorldResult.Failure> removeWorld(
            @NotNull LoadedMultiverseWorld loadedWorld) {
        var result = unloadWorld(loadedWorld);
        if (result.isFailure()) {
            return Attempt.failure(RemoveWorldResult.Failure.UNLOAD_FAILED, result.getFailureMessage());
        }
        return removeWorldFromConfig(loadedWorld);
    }

    /**
     * Removes an existing multiverse world from the world's config. It will no longer be a world known to Multiverse.
     *
     * @param world The multiverse world to remove.
     * @return The result of the remove.
     */
    private Attempt<String, RemoveWorldResult.Failure>
            removeWorldFromConfig(@NotNull MultiverseWorld world) {
        // Remove world from config
        worldsMap.remove(world.getName());
        worldsConfigManager.deleteWorldConfig(world.getName());
        saveWorldsConfig();

        return worldActionResult(world.getName());
    }

    /**
     * Deletes an existing multiverse world entirely. World will be loaded if it is not already loaded.
     * Warning: This will delete all world files.
     *
     * @param worldName The name of the world to delete.
     * @return The result of the delete action.
     */
    public Attempt<String, DeleteWorldResult.Failure> deleteWorld(@NotNull String worldName) {
        return getWorld(worldName)
                .map(this::deleteWorld)
                .getOrElse(() -> worldActionResult(DeleteWorldResult.Failure.WORLD_NON_EXISTENT, worldName));
    }

    /**
     * Deletes an existing multiverse world entirely. World will be loaded if it is not already loaded.
     * Warning: This will delete all world files.
     *
     * @param world The world to delete.
     * @return The result of the delete action.
     */
    public Attempt<String, DeleteWorldResult.Failure> deleteWorld(@NotNull MultiverseWorld world) {
        return getLoadedWorld(world).fold(
                () -> loadWorld(world)
                        .convertReason(DeleteWorldResult.Failure.LOAD_FAILED)
                        .mapAttempt(this::deleteWorld),
                this::deleteWorld);
    }

    /**
     * Deletes an existing multiverse world entirely. Warning: This will delete all world files.
     *
     * @param world The multiverse world to delete.
     * @return The result of the delete action.
     */
    public Attempt<String, DeleteWorldResult.Failure> deleteWorld(@NotNull LoadedMultiverseWorld world) {
        // TODO: Possible config options to keep certain files
        AtomicReference<File> worldFolder = new AtomicReference<>();
        return validateWorldToDelete(world)
                .peek(worldFolder::set)
                .mapAttempt(() -> removeWorld(world).transform(DeleteWorldResult.Failure.REMOVE_FAILED))
                .mapAttempt(() -> filesManipulator.deleteFolder(worldFolder.get()).fold(
                        exception -> worldActionResult(DeleteWorldResult.Failure.FAILED_TO_DELETE_FOLDER,
                                world.getName(), exception),
                        success -> worldActionResult(world.getName())));
    }

    private Attempt<File, DeleteWorldResult.Failure> validateWorldToDelete(
            @NotNull LoadedMultiverseWorld world) {
        File worldFolder = world.getBukkitWorld().map(World::getWorldFolder).getOrNull();
        if (worldFolder == null || !worldNameChecker.isValidWorldFolder(worldFolder)) {
            Logging.severe("Failed to get world folder for world: " + world.getName());
            return worldActionResult(DeleteWorldResult.Failure.WORLD_FOLDER_NOT_FOUND, world.getName());
        }
        return worldActionResult(worldFolder);
    }

    /**
     * Clones an existing multiverse world.
     *
     * @param options   The options for customizing the cloning of a world.
     * @return The result of the clone.
     */
    public Attempt<LoadedMultiverseWorld, CloneWorldResult.Failure> cloneWorld(@NotNull CloneWorldOptions options) {
        return cloneWorldValidateWorld(options)
                .mapAttempt(this::cloneWorldCopyFolder)
                .mapAttempt(validatedOptions -> {
                    ImportWorldOptions importWorldOptions = ImportWorldOptions.worldName(validatedOptions.newWorldName())
                            .environment(validatedOptions.world().getEnvironment())
                            .generator(validatedOptions.world().getGenerator());
                    return importWorld(importWorldOptions).convertReason(CloneWorldResult.Failure.IMPORT_FAILED);
                })
                .onSuccess(newWorld -> {
                    cloneWorldTransferData(options, newWorld);
                    saveWorldsConfig();
                });
    }

    private Attempt<CloneWorldOptions, CloneWorldResult.Failure> cloneWorldValidateWorld(
            @NotNull CloneWorldOptions options) {
        String newWorldName = options.newWorldName();
        if (!worldNameChecker.isValidWorldName(newWorldName)) {
            Logging.severe("Invalid world name: " + newWorldName);
            return worldActionResult(CloneWorldResult.Failure.INVALID_WORLDNAME, newWorldName);
        }
        if (worldNameChecker.isValidWorldFolder(newWorldName)) {
            return worldActionResult(CloneWorldResult.Failure.WORLD_EXIST_FOLDER, newWorldName);
        }
        if (isLoadedWorld(newWorldName)) {
            Logging.severe("World already loaded when attempting to clone: " + newWorldName);
            return worldActionResult(CloneWorldResult.Failure.WORLD_EXIST_LOADED, newWorldName);
        }
        if (isWorld(newWorldName)) {
            Logging.severe("World already exist unloaded: " + newWorldName);
            return worldActionResult(CloneWorldResult.Failure.WORLD_EXIST_UNLOADED, newWorldName);
        }
        return worldActionResult(options);
    }

    private Attempt<CloneWorldOptions, CloneWorldResult.Failure> cloneWorldCopyFolder(
            @NotNull CloneWorldOptions options) {
        // TODO: Check null?
        File worldFolder = options.world().getBukkitWorld().map(World::getWorldFolder).getOrNull();
        File newWorldFolder = new File(Bukkit.getWorldContainer(), options.newWorldName());
        return filesManipulator.copyFolder(worldFolder, newWorldFolder, CLONE_IGNORE_FILES).fold(
                exception -> worldActionResult(CloneWorldResult.Failure.COPY_FAILED,
                        options.world().getName(), exception),
                success -> worldActionResult(options));
    }

    private void cloneWorldTransferData(@NotNull CloneWorldOptions options, @NotNull LoadedMultiverseWorld newWorld) {
        DataTransfer<LoadedMultiverseWorld> dataTransfer = transferData(options, options.world());
        dataTransfer.pasteAllTo(newWorld);
    }

    private DataTransfer<LoadedMultiverseWorld> transferData(
            @NotNull KeepWorldSettingsOptions options, @NotNull LoadedMultiverseWorld world) {
        DataTransfer<LoadedMultiverseWorld> dataTransfer = new DataTransfer<>();

        if (options.keepWorldConfig()) {
            dataTransfer.addDataStore(new WorldConfigStore(), world);
        }
        if (options.keepGameRule()) {
            dataTransfer.addDataStore(new GameRulesStore(), world);
        }
        if (options.keepWorldBorder()) {
            dataTransfer.addDataStore(new WorldBorderStore(), world);
        }

        return dataTransfer;
    }

    /**
     * Regenerates a world.
     *
     * @param options   The options for customizing the regeneration of a world.
     * @return The result of the regeneration.
     */
    public Attempt<LoadedMultiverseWorld, RegenWorldResult.Failure> regenWorld(@NotNull RegenWorldOptions options) {
        // TODO: Teleport players out of world, and back in after regen

        LoadedMultiverseWorld world = options.world();
        DataTransfer<LoadedMultiverseWorld> dataTransfer = transferData(options, world);
        CreateWorldOptions createWorldOptions = CreateWorldOptions.worldName(world.getName())
                .environment(world.getEnvironment())
                .generateStructures(world.canGenerateStructures().getOrElse(true))
                .generator(world.getGenerator())
                .seed(options.seed())
                .worldType(world.getWorldType().getOrElse(WorldType.NORMAL));

        return deleteWorld(world)
                .convertReason(RegenWorldResult.Failure.DELETE_FAILED)
                .mapAttempt(() -> createWorld(createWorldOptions).convertReason(RegenWorldResult.Failure.CREATE_FAILED))
                .onSuccess(newWorld -> {
                    dataTransfer.pasteAllTo(newWorld);
                    saveWorldsConfig();
                });
    }

    private <T, F extends FailureReason> Attempt<T, F> worldActionResult(@NotNull T value) {
        return Attempt.success(value);
    }

    private <T, F extends FailureReason> Attempt<T, F> worldActionResult(
            @NotNull F messageKeyProvider, @NotNull String worldName) {
        return Attempt.failure(messageKeyProvider, replaceWorldName(worldName));
    }

    private <T, F extends FailureReason> Attempt<T, F> worldActionResult(
            @NotNull F messageKeyProvider, @NotNull String worldName, @NotNull Throwable error) {
        // TODO: Localize error message if its a MultiverseException
        return Attempt.failure(messageKeyProvider, replaceWorldName(worldName), replaceError(error.getMessage()));
    }

    private MessageReplacement replaceWorldName(@NotNull String worldName) {
        return replace("{world}").with(worldName);
    }

    private MessageReplacement replaceError(@NotNull String errorMessage) {
        return replace("{error}").with(errorMessage);
    }

    /**
     * Creates a bukkit world.
     *
     * @param worldCreator  The world parameters.
     * @return The created world.
     */
    private Try<World> createBukkitWorld(WorldCreator worldCreator) {
        return Try.of(() -> {
            this.loadTracker.add(worldCreator.name());
            World world = worldCreator.createWorld();
            if (world == null) {
                // TODO: Localize this
                throw new Exception("World created returned null!");
            }
            Logging.fine("Bukkit created world: " + world.getName());
            return world;
        }).onFailure(exception -> {
            Logging.severe("Failed to create bukkit world: " + worldCreator.name());
            exception.printStackTrace();
        }).andFinally(() -> this.loadTracker.remove(worldCreator.name()));
    }

    /**
     * Unloads a bukkit world.
     *
     * @param world The bukkit world to unload.
     * @return The unloaded world.
     */
    private Try<Void> unloadBukkitWorld(World world) {
        return Try.run(() -> {
            unloadTracker.add(world.getName());
            if (!Bukkit.unloadWorld(world, true)) {
                // TODO: Localize this, maybe with MultiverseException
                throw new Exception("Is this the default world? You can't unload the default world!");
            }
            Logging.fine("Bukkit unloaded world: " + world.getName());
        }).onFailure(exception -> {
            Logging.severe("Failed to unload bukkit world: " + world.getName());
            exception.printStackTrace();
        }).andFinally(() -> unloadTracker.remove(world.getName()));
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
                .filter(file -> !isWorld(file.getName()))
                .filter(worldNameChecker::isValidWorldFolder)
                .map(File::getName)
                .toList();
    }

    /**
     * Get a world that is not loaded.
     *
     * @param worldName The name of the world to get.
     * @return The world if it exists.
     */
    public Option<MultiverseWorld> getUnloadedWorld(@Nullable String worldName) {
        return isLoadedWorld(worldName) ? Option.none() : Option.of(worldsMap.get(worldName));
    }

    /**
     * Get a list of all worlds that are not loaded.
     *
     * @return A list of all worlds that are not loaded.
     */
    public Collection<MultiverseWorld> getUnloadedWorlds() {
        return worldsMap.values().stream().filter(world -> !world.isLoaded()).toList();
    }

    /**
     * Check if a world is a world that is not loaded.
     *
     * @param worldName The name of the world to check.
     * @return True if the world is a world that is not loaded.
     */
    public boolean isUnloadedWorld(@Nullable String worldName) {
        return !isLoadedWorld(worldName) && isWorld(worldName);
    }

    /**
     * Get a world that may or may not be loaded. It will an {@link LoadedMultiverseWorld} if the world is loaded,
     * otherwise returns an {@link MultiverseWorld} instance.
     *
     * @param worldName The name of the world to get.
     * @return The world if it exists.
     */
    public Option<MultiverseWorld> getWorld(@Nullable String worldName) {
        return getLoadedWorld(worldName).fold(() -> getUnloadedWorld(worldName), Option::of);
    }

    /**
     * <p>Get a list of all worlds that may or may not be loaded. It will an {@link LoadedMultiverseWorld} if the world
     * is loaded, otherwise you will get an {@link MultiverseWorld} instance.</p>
     *
     * <p>If you want only unloaded worlds, use {@link #getUnloadedWorlds()}. If you want only loaded worlds, use
     * {@link #getLoadedWorlds()}.</p>
     *
     * @return A list of all worlds that may or may not be loaded.
     */
    public Collection<MultiverseWorld> getWorlds() {
        return worldsMap.values().stream()
                .map(world -> getLoadedWorld(world).fold(() -> world, loadedWorld -> loadedWorld))
                .toList();
    }

    /**
     * Check if a world is a world is known to multiverse, but may or may not be loaded.
     *
     * @param worldName The name of the world to check.
     * @return True if the world is a world is known to multiverse, but may or may not be loaded.
     */
    public boolean isWorld(@Nullable String worldName) {
        return worldsMap.containsKey(worldName);
    }

    /**
     * Get a multiverse world that is loaded.
     *
     * @param world The bukkit world that should be loaded.
     * @return The multiverse world if it exists.
     */
    public Option<LoadedMultiverseWorld> getLoadedWorld(@Nullable World world) {
        return world == null ? Option.none() : Option.of(loadedWorldsMap.get(world.getName()));
    }

    /**
     * Get a multiverse world that is loaded.
     *
     * @param world The world that should be loaded.
     * @return The multiverse world if it exists.
     */
    public Option<LoadedMultiverseWorld> getLoadedWorld(@Nullable MultiverseWorld world) {
        return world == null ? Option.none() : Option.of(loadedWorldsMap.get(world.getName()));
    }

    /**
     * Get a multiverse world that is loaded.
     *
     * @param worldName The name of the world to get.
     * @return The multiverse world if it exists.
     */
    public Option<LoadedMultiverseWorld> getLoadedWorld(@Nullable String worldName) {
        return Option.of(loadedWorldsMap.get(worldName));
    }

    /**
     * Get a list of all multiverse worlds that are loaded.
     *
     * @return A list of all multiverse worlds that are loaded.
     */
    public Collection<LoadedMultiverseWorld> getLoadedWorlds() {
        return loadedWorldsMap.values();
    }

    /**
     * Check if a world is a multiverse world that is loaded.
     *
     * @param world The bukkit world to check.
     * @return True if the world is a multiverse world that is loaded.
     */
    public boolean isLoadedWorld(@Nullable World world) {
        return world != null && isLoadedWorld(world.getName());
    }

    /**
     * Check if a world is a multiverse world that is loaded.
     *
     * @param world The world to check.
     * @return True if the world is a multiverse world that is loaded.
     */
    public boolean isLoadedWorld(@Nullable MultiverseWorld world) {
        return world != null && isLoadedWorld(world.getName());
    }

    /**
     * Check if a world is a multiverse world that is loaded.
     *
     * @param worldName The name of the world to check.
     * @return True if the world is a multiverse world that is loaded.
     */
    public boolean isLoadedWorld(@Nullable String worldName) {
        return loadedWorldsMap.containsKey(worldName);
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
