package org.mvplugins.multiverse.core.world;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Function;
import java.util.stream.Collectors;

import com.dumptruckman.minecraft.util.Logging;
import com.google.common.base.Strings;
import io.vavr.control.Option;
import io.vavr.control.Try;
import jakarta.inject.Inject;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.WorldCreator;
import org.bukkit.WorldType;
import org.bukkit.plugin.PluginManager;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jvnet.hk2.annotations.Service;

import org.mvplugins.multiverse.core.config.CoreConfig;
import org.mvplugins.multiverse.core.event.world.MVWorldClonedEvent;
import org.mvplugins.multiverse.core.event.world.MVWorldCreatedEvent;
import org.mvplugins.multiverse.core.event.world.MVWorldDeleteEvent;
import org.mvplugins.multiverse.core.event.world.MVWorldImportedEvent;
import org.mvplugins.multiverse.core.event.world.MVWorldLoadedEvent;
import org.mvplugins.multiverse.core.event.world.MVWorldRegeneratedEvent;
import org.mvplugins.multiverse.core.event.world.MVWorldRemovedEvent;
import org.mvplugins.multiverse.core.event.world.MVWorldUnloadedEvent;
import org.mvplugins.multiverse.core.exceptions.world.MultiverseWorldException;
import org.mvplugins.multiverse.core.locale.MVCorei18n;
import org.mvplugins.multiverse.core.locale.message.Message;
import org.mvplugins.multiverse.core.locale.message.MessageReplacement.Replace;
import org.mvplugins.multiverse.core.permissions.CorePermissions;
import org.mvplugins.multiverse.core.teleportation.BlockSafety;
import org.mvplugins.multiverse.core.teleportation.LocationManipulation;
import org.mvplugins.multiverse.core.utils.ServerProperties;
import org.mvplugins.multiverse.core.utils.result.Attempt;
import org.mvplugins.multiverse.core.utils.result.FailureReason;
import org.mvplugins.multiverse.core.utils.FileUtils;
import org.mvplugins.multiverse.core.utils.text.ChatTextFormatter;
import org.mvplugins.multiverse.core.world.biomeprovider.BiomeProviderFactory;
import org.mvplugins.multiverse.core.world.entity.EntityPurger;
import org.mvplugins.multiverse.core.world.generators.GeneratorProvider;
import org.mvplugins.multiverse.core.world.helpers.DataStore.GameRulesStore;
import org.mvplugins.multiverse.core.world.helpers.DataTransfer;
import org.mvplugins.multiverse.core.world.helpers.DimensionFinder.DimensionFormat;
import org.mvplugins.multiverse.core.world.helpers.WorldNameChecker;
import org.mvplugins.multiverse.core.world.options.CloneWorldOptions;
import org.mvplugins.multiverse.core.world.options.CreateWorldOptions;
import org.mvplugins.multiverse.core.world.options.DeleteWorldOptions;
import org.mvplugins.multiverse.core.world.options.ImportWorldOptions;
import org.mvplugins.multiverse.core.world.options.KeepWorldSettingsOptions;
import org.mvplugins.multiverse.core.world.options.RegenWorldOptions;
import org.mvplugins.multiverse.core.world.options.UnloadWorldOptions;
import org.mvplugins.multiverse.core.world.reasons.CloneFailureReason;
import org.mvplugins.multiverse.core.world.reasons.CreateFailureReason;
import org.mvplugins.multiverse.core.world.reasons.DeleteFailureReason;
import org.mvplugins.multiverse.core.world.reasons.ImportFailureReason;
import org.mvplugins.multiverse.core.world.reasons.LoadFailureReason;
import org.mvplugins.multiverse.core.world.reasons.RegenFailureReason;
import org.mvplugins.multiverse.core.world.reasons.RemoveFailureReason;
import org.mvplugins.multiverse.core.world.reasons.UnloadFailureReason;
import org.mvplugins.multiverse.core.world.reasons.WorldCreatorFailureReason;

import static org.mvplugins.multiverse.core.locale.message.MessageReplacement.replace;
import static org.mvplugins.multiverse.core.world.helpers.DataStore.WorldBorderStore;
import static org.mvplugins.multiverse.core.world.helpers.DataStore.WorldConfigStore;

@Service // SUPPRESS CHECKSTYLE: ClassFanOutComplexity This is the world manager, it's going to be complex.
public final class WorldManager {

    private static final List<String> CLONE_IGNORE_FILES = Arrays.asList("uid.dat", "session.lock");
    private static final DimensionFormat DEFAULT_NETHER_FORMAT = new DimensionFormat("%overworld%_nether");
    private static final DimensionFormat DEFAULT_END_FORMAT = new DimensionFormat("%overworld%_the_end");

    private final Map<String, MultiverseWorld> worldsMap;
    private final Map<String, LoadedMultiverseWorld> loadedWorldsMap;
    private final List<String> unloadTracker;
    private final List<String> loadTracker;
    private final WorldsConfigManager worldsConfigManager;
    private final WorldNameChecker worldNameChecker;
    private final BiomeProviderFactory biomeProviderFactory;
    private final GeneratorProvider generatorProvider;
    private final FileUtils fileUtils;
    private final BlockSafety blockSafety;
    private final LocationManipulation locationManipulation;
    private final PluginManager pluginManager;
    private final CorePermissions corePermissions;
    private final ServerProperties serverProperties;
    private final CoreConfig config;
    private final EntityPurger entityPurger;

    @Inject
    WorldManager(
            @NotNull WorldsConfigManager worldsConfigManager,
            @NotNull WorldNameChecker worldNameChecker,
            @NotNull BiomeProviderFactory biomeProviderFactory,
            @NotNull GeneratorProvider generatorProvider,
            @NotNull FileUtils fileUtils,
            @NotNull BlockSafety blockSafety,
            @NotNull LocationManipulation locationManipulation,
            @NotNull PluginManager pluginManager,
            @NotNull CorePermissions corePermissions,
            @NotNull ServerProperties serverProperties,
            @NotNull CoreConfig config,
            @NotNull EntityPurger entityPurger) {
        this.worldsConfigManager = worldsConfigManager;
        this.worldNameChecker = worldNameChecker;
        this.biomeProviderFactory = biomeProviderFactory;
        this.generatorProvider = generatorProvider;
        this.fileUtils = fileUtils;
        this.blockSafety = blockSafety;
        this.locationManipulation = locationManipulation;
        this.pluginManager = pluginManager;
        this.corePermissions = corePermissions;
        this.serverProperties = serverProperties;
        this.config = config;
        this.entityPurger = entityPurger;

        this.worldsMap = new HashMap<>();
        this.loadedWorldsMap = new HashMap<>();
        this.unloadTracker = new ArrayList<>();
        this.loadTracker = new ArrayList<>();
    }

    /**
     * Loads all worlds from the worlds config.
     *
     * @return The result of the load.
     */
    public Try<Void> initAllWorlds() {
        return updateWorldsFromConfig().andThenTry(() -> {
            importExistingWorlds();
            autoLoadWorlds();
        }).flatMap(ignore -> saveWorldsConfig());
    }

    /**
     * Updates the current set of worlds to match the worlds config.
     *
     * @return A successful Try if the worlds.yml config was loaded successfully.
     */
    private Try<Void> updateWorldsFromConfig() {
        return worldsConfigManager.load().mapTry(result -> {
            loadNewWorldConfigs(result.newWorlds());
            removeWorldsNotInConfigs(result.removedWorlds());
            return null;
        });
    }

    private void loadNewWorldConfigs(Collection<WorldConfig> newWorldConfigs) {
        newWorldConfigs.forEach(worldConfig -> Option.of(worldsMap.get(worldConfig.getWorldName()))
                .peek(unloadedWorld ->  unloadedWorld.setWorldConfig(worldConfig))
                .onEmpty(() -> newMultiverseWorld(worldConfig.getWorldName(), worldConfig)));
    }

    private void removeWorldsNotInConfigs(Collection<String> removedWorlds) {
        removedWorlds.forEach(worldName -> removeWorld(worldName)
                .onFailure(failure -> Logging.severe("Failed to unload world %s: %s", worldName, failure))
                .onSuccess(success -> Logging.fine("Unloaded world %s as it was removed from config", worldName)));
    }

    /**
     * Load worlds that are already loaded by bukkit before Multiverse-Core is loaded.
     */
    private void importExistingWorlds() {
        Map<String, World> bukkitWorlds = Bukkit.getWorlds()
                .stream()
                .collect(Collectors.toMap(World::getName, Function.identity()));

        serverProperties.getLevelName().peek(overworldName -> {
            World overworld = bukkitWorlds.remove(overworldName);
            World nether = bukkitWorlds.remove(DEFAULT_NETHER_FORMAT.replaceOverworld(overworldName));
            World end = bukkitWorlds.remove(DEFAULT_END_FORMAT.replaceOverworld(overworldName));

            if (config.getAutoImportDefaultWorlds()) {
                importExistingBukkitWorld(overworld);
                importExistingBukkitWorld(nether);
                importExistingBukkitWorld(end);
            }
        });

        if (config.getAutoImport3rdPartyWorlds()) {
            bukkitWorlds.values().forEach(this::importExistingBukkitWorld);
        }
    }

    private void importExistingBukkitWorld(World bukkitWorld) {
        if (bukkitWorld == null || isWorld(bukkitWorld.getName())) {
            return;
        }
        importWorld(ImportWorldOptions.worldName(bukkitWorld.getName())
                .environment(bukkitWorld.getEnvironment())
                .generator(generatorProvider.getDefaultGeneratorForWorld(bukkitWorld.getName())))
                .onFailure(failure ->
                        Logging.severe("Failed to import world %s: %s", bukkitWorld.getName(), failure))
                .onSuccess(success ->
                        Logging.fine("Imported existing world %s", bukkitWorld.getName()));
    }

    /**
     * Loads all worlds that are set to autoload.
     */
    private void autoLoadWorlds() {
        getWorlds().forEach(world -> {
            if (isLoadedWorld(world) || !world.isAutoLoad()) {
                return;
            }
            loadWorld(world).onFailure(failure -> Logging.severe("Failed to load world %s: %s",
                    world.getName(), failure));
        });
    }

    /**
     * Creates a new world.
     *
     * @param options The options for customizing the creation of a new world.
     * @return The result of the creation.
     */
    public Attempt<LoadedMultiverseWorld, CreateFailureReason> createWorld(CreateWorldOptions options) {
        return validateCreateWorldOptions(options).mapAttempt(this::doCreateWorld);
    }

    private Attempt<CreateWorldOptions, CreateFailureReason> validateCreateWorldOptions(
            CreateWorldOptions options) {
        if (!worldNameChecker.isValidWorldName(options.worldName())) {
            return worldActionResult(CreateFailureReason.INVALID_WORLDNAME, options.worldName());
        } else if (getLoadedWorld(options.worldName()).isDefined()) {
            return worldActionResult(CreateFailureReason.WORLD_EXIST_LOADED, options.worldName());
        } else if (getWorld(options.worldName()).isDefined()) {
            return worldActionResult(CreateFailureReason.WORLD_EXIST_UNLOADED, options.worldName());
        } else if (options.doFolderCheck() && worldNameChecker.hasWorldFolder(options.worldName())) {
            return worldActionResult(CreateFailureReason.WORLD_EXIST_FOLDER, options.worldName());
        }
        return worldActionResult(options);
    }

    private Attempt<LoadedMultiverseWorld, CreateFailureReason> doCreateWorld(
            CreateWorldOptions options) {
        String generatorString = generatorProvider.parseGeneratorString(options.worldName(), options.generator());
        WorldCreator worldCreator = WorldCreator.name(options.worldName())
                .environment(options.environment())
                .generateStructures(options.generateStructures())
                .generatorSettings(options.generatorSettings())
                .seed(options.seed())
                .type(options.worldType());

        return addBiomeProviderToCreator(worldCreator, options.worldName(), options.biome())
                .mapAttempt(creator -> addGeneratorToCreator(creator, generatorString))
                .mapAttempt(this::createBukkitWorld)
                .transform(CreateFailureReason.WORLD_CREATOR_FAILED)
                .map(bukkitWorld -> {
                    LoadedMultiverseWorld loadedWorld = newLoadedMultiverseWorld(
                            bukkitWorld,
                            generatorString,
                            options.biome(),
                            options.useSpawnAdjust());
                    pluginManager.callEvent(new MVWorldCreatedEvent(loadedWorld));
                    return loadedWorld;
                });
    }

    /**
     * Imports an existing world folder.
     *
     * @param options The options for customizing the import of an existing world folder.
     * @return The result of the import.
     */
    public Attempt<LoadedMultiverseWorld, ImportFailureReason> importWorld(
            ImportWorldOptions options) {
        return validateImportWorldOptions(options).mapAttempt(this::doImportWorld);
    }

    private Attempt<ImportWorldOptions, ImportFailureReason> validateImportWorldOptions(
            ImportWorldOptions options) {
        String worldName = options.worldName();
        if (!worldNameChecker.isValidWorldName(worldName)) {
            return worldActionResult(ImportFailureReason.INVALID_WORLDNAME, worldName);
        } else if (!worldNameChecker.isValidWorldFolder(worldName)) {
            return worldActionResult(ImportFailureReason.WORLD_FOLDER_INVALID, worldName);
        } else if (isLoadedWorld(worldName)) {
            return worldActionResult(ImportFailureReason.WORLD_EXIST_LOADED, worldName);
        } else if (isWorld(worldName)) {
            return worldActionResult(ImportFailureReason.WORLD_EXIST_UNLOADED, worldName);
        }
        return worldActionResult(options);
    }

    private Attempt<LoadedMultiverseWorld, ImportFailureReason> doImportWorld(
            ImportWorldOptions options) {
        String generatorString = generatorProvider.parseGeneratorString(options.worldName(), options.generator());
        WorldCreator worldCreator = WorldCreator.name(options.worldName())
                .environment(options.environment());

        return addBiomeProviderToCreator(worldCreator, options.worldName(), options.biome())
                .mapAttempt(creator -> addGeneratorToCreator(creator, generatorString))
                .mapAttempt(this::createBukkitWorld)
                .transform(ImportFailureReason.WORLD_CREATOR_FAILED)
                .map(bukkitWorld -> {
                    LoadedMultiverseWorld loadedWorld = newLoadedMultiverseWorld(
                            bukkitWorld,
                            generatorString,
                            options.biome(),
                            options.useSpawnAdjust());
                    pluginManager.callEvent(new MVWorldImportedEvent(loadedWorld));
                    return loadedWorld;
                });
    }

    private MultiverseWorld newMultiverseWorld(String worldName, WorldConfig worldConfig) {
        MultiverseWorld mvWorld = new MultiverseWorld(worldName, worldConfig, config);
        worldsMap.put(mvWorld.getName(), mvWorld);
        corePermissions.addWorldPermissions(mvWorld);
        return mvWorld;
    }

    /**
     * Creates a new loaded multiverseWorld from a bukkit world.
     *
     * @param world         The bukkit world to create a multiverse world from.
     * @param generator     The generator string.
     * @param adjustSpawn   Whether to adjust spawn.
     */
    private LoadedMultiverseWorld newLoadedMultiverseWorld(
            @NotNull World world, @Nullable String generator, @Nullable String biome, boolean adjustSpawn) {
        WorldConfig worldConfig = worldsConfigManager.addWorldConfig(world.getName());
        worldConfig.setAdjustSpawn(adjustSpawn);
        worldConfig.setGenerator(generator == null ? "" : generator);
        worldConfig.setBiome(biome == null ? "" : biome);
        worldConfig.save();

        MultiverseWorld mvWorld = newMultiverseWorld(world.getName(), worldConfig);
        LoadedMultiverseWorld loadedWorld = new LoadedMultiverseWorld(
                world,
                worldConfig,
                config,
                blockSafety,
                locationManipulation,
                entityPurger
        );
        setDefaultEnvironmentScale(mvWorld);
        loadedWorldsMap.put(loadedWorld.getName(), loadedWorld);
        saveWorldsConfig();
        pluginManager.callEvent(new MVWorldLoadedEvent(loadedWorld));
        return loadedWorld;
    }

    private void setDefaultEnvironmentScale(MultiverseWorld world) {
        double scale = switch (world.getEnvironment()) {
            case NETHER -> 8.0;
            case THE_END -> 16.0;
            default -> 1.0;
        };
        world.setScale(scale);
    }

    /**
     * Loads an existing world in config.
     *
     * @param worldName The name of the world to load.
     * @return The result of the load.
     */
    public Attempt<LoadedMultiverseWorld, LoadFailureReason> loadWorld(@NotNull String worldName) {
        return getWorld(worldName)
                .map(this::loadWorld)
                .getOrElse(() -> worldNameChecker.isValidWorldFolder(worldName)
                        ? worldActionResult(LoadFailureReason.WORLD_EXIST_FOLDER, worldName)
                        : worldActionResult(LoadFailureReason.WORLD_NON_EXISTENT, worldName));
    }

    /**
     * Loads an existing world in config.
     *
     * @param world The world to load.
     * @return The result of the load.
     */
    public Attempt<LoadedMultiverseWorld, LoadFailureReason> loadWorld(@NotNull MultiverseWorld world) {
        return validateWorldToLoad(world).mapAttempt(this::doLoadWorld);
    }

    private Attempt<MultiverseWorld, LoadFailureReason> validateWorldToLoad(@NotNull MultiverseWorld mvWorld) {
        if (loadTracker.contains(mvWorld.getName())) {
            // This is to prevent recursive calls by WorldLoadEvent
            Logging.fine("World already loading: " + mvWorld.getName());
            return worldActionResult(LoadFailureReason.WORLD_ALREADY_LOADING, mvWorld.getName());
        } else if (isLoadedWorld(mvWorld)) {
            Logging.severe("World already loaded: " + mvWorld.getName());
            return worldActionResult(LoadFailureReason.WORLD_EXIST_LOADED, mvWorld.getName());
        }
        return worldActionResult(mvWorld);
    }

    private Attempt<LoadedMultiverseWorld, LoadFailureReason> doLoadWorld(@NotNull MultiverseWorld mvWorld) {
        World bukkitWorld = Bukkit.getWorld(mvWorld.getName());
        if (bukkitWorld != null) {
            // World already loaded, maybe by another plugin
            Logging.finer("World already loaded in bukkit: " + mvWorld.getName());
            return newLoadedMultiverseWorld(mvWorld, bukkitWorld);
        }

        WorldCreator worldCreator = WorldCreator.name(mvWorld.getName())
                .environment(mvWorld.getEnvironment())
                .seed(mvWorld.getSeed());
        return addBiomeProviderToCreator(worldCreator, mvWorld.getName(), mvWorld.getBiome())
                .mapAttempt(creator -> addGeneratorToCreator(creator, mvWorld.getGenerator()))
                .mapAttempt(this::createBukkitWorld)
                .transform(LoadFailureReason.WORLD_CREATOR_FAILED)
                .mapAttempt(newBukkitWorld -> newLoadedMultiverseWorld(mvWorld, newBukkitWorld));
    }

    private Attempt<LoadedMultiverseWorld, LoadFailureReason> newLoadedMultiverseWorld(MultiverseWorld mvWorld, World bukkitWorld) {
        WorldConfig worldConfig = worldsConfigManager.getWorldConfig(mvWorld.getName()).get(); //TODO: null check here, but logically it should never be null.
        LoadedMultiverseWorld loadedWorld = new LoadedMultiverseWorld(
                bukkitWorld,
                worldConfig,
                config,
                blockSafety,
                locationManipulation,
                entityPurger
        );
        loadedWorldsMap.put(loadedWorld.getName(), loadedWorld);
        saveWorldsConfig();
        pluginManager.callEvent(new MVWorldLoadedEvent(loadedWorld));
        return Attempt.success(loadedWorld);
    }

    /**
     * Unloads an existing multiverse world. It will still remain as an unloaded world.
     *
     * @param options The options for customizing the unloading of a world.
     * @return The result of the unload action.
     */
    public Attempt<MultiverseWorld, UnloadFailureReason> unloadWorld(@NotNull UnloadWorldOptions options) {
        LoadedMultiverseWorld world = options.world();
        if (unloadTracker.contains(world.getName())) {
            // This is to prevent recursive calls by WorldUnloadEvent
            Logging.fine("World already unloading: " + world.getName());
            return worldActionResult(UnloadFailureReason.WORLD_ALREADY_UNLOADING, world.getName());
        }

        if (!isLoadedWorld(world)) {
            Logging.severe("World is not loaded: " + world.getName());
            return worldActionResult(UnloadFailureReason.WORLD_NON_EXISTENT, world.getName());
        }

        if (!options.unloadBukkitWorld()) {
            return removeLoadedMultiverseWorld(world);
        }

        return unloadBukkitWorld(world.getBukkitWorld().getOrNull(), options.saveBukkitWorld()).fold(
                exception -> worldActionResult(UnloadFailureReason.BUKKIT_UNLOAD_FAILED, world.getName(), exception),
                success -> removeLoadedMultiverseWorld(world));
    }

    private Attempt<MultiverseWorld, UnloadFailureReason> removeLoadedMultiverseWorld(@NotNull LoadedMultiverseWorld world) {
        MultiverseWorld mvWorld = Objects.requireNonNull(loadedWorldsMap.remove(world.getName()),
                "For some reason, the loaded world isn't in the map... BUGGG");
        Logging.fine("Removed MultiverseWorld from map: " + world.getName());
        var unloadedWorld = Objects.requireNonNull(worldsMap.get(world.getName()),
                "For some reason, the unloaded world isn't in the map... BUGGG");
        mvWorld.getWorldConfig().setMVWorld(unloadedWorld);
        pluginManager.callEvent(new MVWorldUnloadedEvent(mvWorld));
        return worldActionResult(unloadedWorld);
    }

    /**
     * Removes an existing multiverse world. It will be deleted from the worlds config and will no longer be an
     * unloaded world. World files will not be deleted.
     *
     * @param worldName The name of the world to remove.
     * @return The result of the remove.
     */
    public Attempt<String, RemoveFailureReason> removeWorld(
            @NotNull String worldName) {
        return getWorld(worldName)
                .map(this::removeWorld)
                .getOrElse(() -> worldActionResult(RemoveFailureReason.WORLD_NON_EXISTENT, worldName));
    }

    /**
     * Removes an existing multiverse world. It will be deleted from the worlds config and will no longer be an
     * unloaded world. World files will not be deleted.
     *
     * @param world The multiverse world to remove.
     * @return The result of the remove.
     */
    public Attempt<String, RemoveFailureReason> removeWorld(@NotNull MultiverseWorld world) {
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
    public Attempt<String, RemoveFailureReason> removeWorld(@NotNull LoadedMultiverseWorld loadedWorld) {
        return unloadWorld(UnloadWorldOptions.world(loadedWorld))
                .transform(RemoveFailureReason.UNLOAD_FAILED)
                .mapAttempt(this::removeWorldFromConfig);
    }

    /**
     * Removes an existing multiverse world from the world's config. It will no longer be a world known to Multiverse.
     *
     * @param world The multiverse world to remove.
     * @return The result of the remove.
     */
    private Attempt<String, RemoveFailureReason> removeWorldFromConfig(@NotNull MultiverseWorld world) {
        // Remove world from config
        worldsMap.remove(world.getName());
        world.getWorldConfig().deferenceMVWorld();
        worldsConfigManager.deleteWorldConfig(world.getName());
        saveWorldsConfig();
        corePermissions.removeWorldPermissions(world);
        pluginManager.callEvent(new MVWorldRemovedEvent(world));
        return worldActionResult(world.getName());
    }

    /**
     * Deletes an existing multiverse world entirely. World will be loaded if it is not already loaded.
     * Warning: This will delete all world files.
     *
     * @param options The options for customizing the deletion of a world.
     * @return The result of the delete action.
     */
    public Attempt<String, DeleteFailureReason> deleteWorld(@NotNull DeleteWorldOptions options) {
        return getLoadedWorld(options.world()).fold(
                () -> loadWorld(options.world())
                        .transform(DeleteFailureReason.LOAD_FAILED)
                        .mapAttempt(world -> doDeleteWorld(world, options)),
                world -> doDeleteWorld(world, options));
    }

    /**
     * Deletes an existing multiverse world entirely. Warning: This will delete all world files.
     *
     * @param world The multiverse world to delete.
     * @return The result of the delete action.
     */
    private Attempt<String, DeleteFailureReason> doDeleteWorld(@NotNull LoadedMultiverseWorld world, @NotNull DeleteWorldOptions options) {
        AtomicReference<File> worldFolder = new AtomicReference<>();
        return validateWorldToDelete(world)
                .peek(worldFolder::set)
                .mapAttempt(() -> {
                    MVWorldDeleteEvent event = new MVWorldDeleteEvent(world);
                    pluginManager.callEvent(event);
                    return event.isCancelled()
                            ? Attempt.failure(DeleteFailureReason.EVENT_CANCELLED)
                            : Attempt.success(null);
                })
                .mapAttempt(() -> removeWorld(world).transform(DeleteFailureReason.REMOVE_FAILED))
                .mapAttempt(() -> fileUtils.deleteFolder(worldFolder.get(), options.keepFiles()).fold(
                        exception -> worldActionResult(DeleteFailureReason.FAILED_TO_DELETE_FOLDER,
                                world.getName(), exception),
                        success -> worldActionResult(world.getName())));
    }

    private Attempt<File, DeleteFailureReason> validateWorldToDelete(
            @NotNull LoadedMultiverseWorld world) {
        File worldFolder = world.getBukkitWorld().map(World::getWorldFolder).getOrNull();
        if (worldFolder == null || !worldNameChecker.isValidWorldFolder(worldFolder)) {
            Logging.severe("Failed to get world folder for world: " + world.getName());
            return worldActionResult(DeleteFailureReason.WORLD_FOLDER_NOT_FOUND, world.getName());
        }
        return worldActionResult(worldFolder);
    }

    /**
     * Clones an existing multiverse world.
     *
     * @param options The options for customizing the cloning of a world.
     * @return The result of the clone.
     */
    public Attempt<LoadedMultiverseWorld, CloneFailureReason> cloneWorld(@NotNull CloneWorldOptions options) {
        return cloneWorldValidateWorld(options)
                .mapAttempt(this::cloneWorldCopyFolder)
                .mapAttempt(validatedOptions -> {
                    ImportWorldOptions importWorldOptions = ImportWorldOptions
                            .worldName(validatedOptions.newWorldName())
                            .biome(validatedOptions.world().getBiome())
                            .environment(validatedOptions.world().getEnvironment())
                            .generator(validatedOptions.world().getGenerator());
                    return importWorld(importWorldOptions).transform(CloneFailureReason.IMPORT_FAILED);
                })
                .onSuccess(newWorld -> {
                    cloneWorldTransferData(options, newWorld);
                    if (options.keepWorldConfig()) {
                        newWorld.setSpawnLocation(options.world().getSpawnLocation());
                    }
                    saveWorldsConfig();
                    pluginManager.callEvent(new MVWorldClonedEvent(newWorld, options.world()));
                });
    }

    private Attempt<CloneWorldOptions, CloneFailureReason> cloneWorldValidateWorld(
            @NotNull CloneWorldOptions options) {
        String newWorldName = options.newWorldName();
        if (!worldNameChecker.isValidWorldName(newWorldName)) {
            Logging.severe("Invalid world name: " + newWorldName);
            return worldActionResult(CloneFailureReason.INVALID_WORLDNAME, newWorldName);
        }
        if (isLoadedWorld(newWorldName)) {
            Logging.severe("World already loaded when attempting to clone: " + newWorldName);
            return worldActionResult(CloneFailureReason.WORLD_EXIST_LOADED, newWorldName);
        }
        if (isWorld(newWorldName)) {
            Logging.severe("World already exist unloaded: " + newWorldName);
            return worldActionResult(CloneFailureReason.WORLD_EXIST_UNLOADED, newWorldName);
        }
        if (worldNameChecker.hasWorldFolder(newWorldName)) {
            return worldActionResult(CloneFailureReason.WORLD_EXIST_FOLDER, newWorldName);
        }
        return worldActionResult(options);
    }

    private Attempt<CloneWorldOptions, CloneFailureReason> cloneWorldCopyFolder(@NotNull CloneWorldOptions options) {
        if (options.saveBukkitWorld()) {
            Logging.finer("Saving bukkit world before cloning: " + options.world().getName());
            options.world().getBukkitWorld().peek(World::save);
        }
        File worldFolder = options.world().getBukkitWorld().map(World::getWorldFolder).get();
        File newWorldFolder = new File(Bukkit.getWorldContainer(), options.newWorldName());
        return fileUtils.copyFolder(worldFolder, newWorldFolder, CLONE_IGNORE_FILES).fold(
                exception -> worldActionResult(CloneFailureReason.COPY_FAILED,
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
     * @param options The options for customizing the regeneration of a world.
     * @return The result of the regeneration.
     */
    public Attempt<LoadedMultiverseWorld, RegenFailureReason> regenWorld(@NotNull RegenWorldOptions options) {
        LoadedMultiverseWorld world = options.world();
        DataTransfer<LoadedMultiverseWorld> dataTransfer = transferData(options, world);
        boolean shouldKeepSpawnLocation = options.keepWorldConfig() && options.seed() == world.getSeed();
        Location spawnLocation = world.getSpawnLocation();

        CreateWorldOptions createWorldOptions = CreateWorldOptions.worldName(world.getName())
                .environment(world.getEnvironment())
                .generateStructures(world.canGenerateStructures().getOrElse(true))
                .generator(world.getGenerator())
                .seed(options.seed())
                .useSpawnAdjust(!shouldKeepSpawnLocation && world.getAdjustSpawn())
                .worldType(world.getWorldType().getOrElse(WorldType.NORMAL))
                .doFolderCheck(options.keepFiles().isEmpty());

        return deleteWorld(DeleteWorldOptions.world(world).keepFiles(options.keepFiles()))
                .transform(RegenFailureReason.DELETE_FAILED)
                .mapAttempt(() -> createWorld(createWorldOptions).transform(RegenFailureReason.CREATE_FAILED))
                .onSuccess(newWorld -> {
                    dataTransfer.pasteAllTo(newWorld);
                    if (shouldKeepSpawnLocation) {
                        // Special case for spawn location to prevent unsafe location if world was regen using a
                        // different seed.
                        newWorld.setSpawnLocation(spawnLocation);
                    }
                    saveWorldsConfig();
                    pluginManager.callEvent(new MVWorldRegeneratedEvent(newWorld));
                });
    }

    private <T, F extends FailureReason> Attempt<T, F> worldActionResult(@NotNull T value) {
        return Attempt.success(value);
    }

    private <T, F extends FailureReason> Attempt<T, F> worldActionResult(
            @NotNull F failureReason, @NotNull String worldName) {
        return Attempt.failure(failureReason, Replace.WORLD.with(worldName));
    }

    private <T, F extends FailureReason> Attempt<T, F> worldActionResult(
            @NotNull F failureReason, @NotNull String worldName, @NotNull Throwable error) {
        return Attempt.failure(failureReason, Replace.WORLD.with(worldName), Replace.ERROR.with(error));
    }

    private Attempt<WorldCreator, WorldCreatorFailureReason> addBiomeProviderToCreator(
            WorldCreator worldCreator, String worldName, String biomeString) {
        return Try.of(() -> worldCreator.biomeProvider(biomeProviderFactory.parseBiomeProvider(worldName, biomeString)))
                .fold(
                        throwable -> {
                            throwable.printStackTrace();
                            return Attempt.failure(WorldCreatorFailureReason.INVALID_BIOME_PROVIDER,
                                    replace("{biome}").with(biomeString),
                                    Replace.ERROR.with(throwable));
                        },
                        Attempt::success
                );
    }

    private Attempt<WorldCreator, WorldCreatorFailureReason> addGeneratorToCreator(
            WorldCreator worldCreator, String generatorString) {
        if (Strings.isNullOrEmpty(generatorString)) {
            return Attempt.success(worldCreator);
        }
        return Try.of(() -> worldCreator.generator(generatorString))
                .fold(
                        throwable -> {
                            throwable.printStackTrace();
                            return Attempt.failure(WorldCreatorFailureReason.INVALID_CHUNK_GENERATOR,
                                    replace("{generator}").with(generatorString),
                                    Replace.ERROR.with(throwable));
                        },
                        Attempt::success);
    }

    /**
     * Creates a bukkit world.
     *
     * @param worldCreator  The world parameters.
     * @return The created world.
     */
    private Attempt<World, WorldCreatorFailureReason> createBukkitWorld(WorldCreator worldCreator) {
        return Try.of(() -> {
            this.loadTracker.add(worldCreator.name());
            World world = worldCreator.createWorld();
            if (world == null) {
                throw new MultiverseWorldException(Message.of(MVCorei18n.EXCEPTION_MULTIVERSEWORLD_CREATENULL));
            }
            Logging.fine("Bukkit created world: " + world.getName());
            return world;
        }).onFailure(exception -> {
            Logging.severe("Failed to create bukkit world: " + worldCreator.name());
            exception.printStackTrace();
        }).andFinally(() -> {
            this.loadTracker.remove(worldCreator.name());
        }).fold(throwable -> Attempt.failure(WorldCreatorFailureReason.BUKKIT_CREATION_FAILED,
                        Replace.WORLD.with(worldCreator.name()),
                        Replace.ERROR.with(throwable)),
                Attempt::success);
    }

    /**
     * Unloads a bukkit world.
     *
     * @param world The bukkit world to unload.
     * @return The unloaded world.
     */
    private Try<Void> unloadBukkitWorld(World world, boolean save) {
        return Try.run(() -> {
            if (world == null) {
                return;
            }
            unloadTracker.add(world.getName());
            if (!Bukkit.unloadWorld(world, save)) {
                throwUnloadException(world);
            }
            Logging.fine("Bukkit unloaded world: " + world.getName());
        }).andFinally(() -> unloadTracker.remove(world.getName()));
    }

    private void throwUnloadException(World world) throws MultiverseWorldException {
        var defaultWorldName = getDefaultWorld().map(LoadedMultiverseWorld::getName).getOrElse("");
        if (Objects.equals(world.getName(), defaultWorldName)) {
            throw new MultiverseWorldException(Message.of(MVCorei18n.EXCEPTION_MULTIVERSEWORLD_UNLOADDEFAULTWORLD,
                    Replace.WORLD.with(world.getName())));
        }
        if (!world.getPlayers().isEmpty()) {
            throw new MultiverseWorldException(Message.of(MVCorei18n.EXCEPTION_MULTIVERSEWORLD_UNLOADPLAYERSINWORLD,
                    replace("{count}").with(world.getPlayers().size())));
        }
        throw new MultiverseWorldException(Message.of(MVCorei18n.EXCEPTION_MULTIVERSEWORLD_UNLOADERROR));
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
     * Get a world that may or may not be loaded. It will an {@link LoadedMultiverseWorld} if the world is loaded,
     * otherwise returns an {@link MultiverseWorld} instance.
     *
     * @param world The bukkit world to get.
     * @return The world if it exists.
     */
    public Option<MultiverseWorld> getWorld(@Nullable World world) {
        return Option.of(world).map(World::getName).flatMap(this::getWorld);
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
     * Get a world that may or may not be loaded by name or alias. It will an {@link LoadedMultiverseWorld} if the world is loaded,
     * otherwise returns an {@link MultiverseWorld} instance. World name will still be prioritized over alias.
     *
     * @param worldNameOrAlias The name or alias of the world to get.
     * @return The world if it exists.
     */
    public Option<MultiverseWorld> getWorldByNameOrAlias(@Nullable String worldNameOrAlias) {
        return getLoadedWorldByNameOrAlias(worldNameOrAlias)
                .fold(() -> getUnloadedWorldByNameOrAlias(worldNameOrAlias), Option::of);
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
                .map(world -> getLoadedWorld(world)
                        .fold(() -> world, loadedWorld -> loadedWorld))
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
     * Get a world that is not loaded.
     *
     * @param worldName The name of the world to get.
     * @return The world if it exists.
     */
    public Option<MultiverseWorld> getUnloadedWorld(@Nullable String worldName) {
        return isLoadedWorld(worldName) ? Option.none() : Option.of(worldsMap.get(worldName));
    }

    /**
     * Get a world that is not loaded by name or alias. World name will still be prioritized over alias.
     *
     * @param worldNameOrAlias The name or alias of the world to get.
     * @return The world if it exists.
     */
    public Option<MultiverseWorld> getUnloadedWorldByNameOrAlias(@Nullable String worldNameOrAlias) {
        return getUnloadedWorld(worldNameOrAlias).orElse(() -> getUnloadedWorldByAlias(worldNameOrAlias));
    }

    private Option<MultiverseWorld> getUnloadedWorldByAlias(@Nullable String alias) {
        String colourlessAlias = ChatTextFormatter.removeColor(alias);
        return Option.ofOptional(worldsMap.values().stream()
                .filter(world -> !world.isLoaded())
                .filter(world -> world.getColourlessAlias().equalsIgnoreCase(colourlessAlias))
                .findFirst());
    }

    /**
     * Get a list of all worlds that are not loaded.
     *
     * @return A list of all worlds that are not loaded.
     */
    public Collection<MultiverseWorld> getUnloadedWorlds() {
        return worldsMap.values().stream()
                .filter(world -> !world.isLoaded())
                .toList();
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
     * Get a multiverse world that is loaded.
     *
     * @param world The bukkit world that should be loaded.
     * @return The multiverse world if it exists.
     */
    public Option<LoadedMultiverseWorld> getLoadedWorld(@Nullable World world) {
        return world == null ? Option.none() : Option.of((LoadedMultiverseWorld) loadedWorldsMap.get(world.getName()));
    }

    /**
     * Get a multiverse world that is loaded.
     *
     * @param world The world that should be loaded.
     * @return The multiverse world if it exists.
     */
    public Option<LoadedMultiverseWorld> getLoadedWorld(@Nullable MultiverseWorld world) {
        return world == null ? Option.none() : Option.of((LoadedMultiverseWorld) loadedWorldsMap.get(world.getName()));
    }

    /**
     * Get a multiverse world that is loaded.
     *
     * @param worldName The name of the world to get.
     * @return The multiverse world if it exists.
     */
    public Option<LoadedMultiverseWorld> getLoadedWorld(@Nullable String worldName) {
        return Option.of((LoadedMultiverseWorld) loadedWorldsMap.get(worldName));
    }

    /**
     * Get a multiverse world that is loaded by name or alias. World name will still be prioritized over alias.
     *
     * @param worldNameOrAlias The name or alias of the world to get.
     * @return The multiverse world if it exists.
     */
    public Option<LoadedMultiverseWorld> getLoadedWorldByNameOrAlias(@Nullable String worldNameOrAlias) {
        return getLoadedWorld(worldNameOrAlias)
                .orElse(() -> getLoadedWorldByAlias(worldNameOrAlias));
    }

    private Option<LoadedMultiverseWorld> getLoadedWorldByAlias(@Nullable String alias) {
        return Option.ofOptional(loadedWorldsMap.values().stream()
                .filter(world -> world.getColourlessAlias().equalsIgnoreCase(ChatColor.stripColor(alias)))
                .map(world -> (LoadedMultiverseWorld) world)
                .findFirst());
    }

    /**
     * Get a read-only list of all multiverse worlds that are loaded.
     *
     * @return A list of all multiverse worlds that are loaded.
     */
    public Collection<LoadedMultiverseWorld> getLoadedWorlds() {
        return loadedWorldsMap.values().stream()
                .map(world -> (LoadedMultiverseWorld) world)
                .toList();
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
     * Gets the default world defined by `level-name` in server.properties. If server.properties is not found,
     * the first world in the bukkit world list will be returned.
     * <br/>
     * This world cannot be unloaded.
     *
     * @return The default world.
     */
    public Option<LoadedMultiverseWorld> getDefaultWorld() {
        return serverProperties.getLevelName().flatMap(this::getLoadedWorld)
                .orElse(getLoadedWorld(Bukkit.getWorlds().get(0)));
    }

    /**
     * Saves the worlds.yml config to disk.
     *
     * @return A successful try if the file is saved to disk, else the exception object throw.
     */
    public Try<Void> saveWorldsConfig() {
        return worldsConfigManager.save()
                .onFailure(failure -> {
                    Logging.severe("Failed to save worlds config: %s", failure);
                    failure.printStackTrace();
                });
    }
}
