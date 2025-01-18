package org.mvplugins.multiverse.core.api.world;

import io.vavr.control.Option;
import org.bukkit.World;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jvnet.hk2.annotations.Contract;
import org.mvplugins.multiverse.core.api.result.Attempt;
import org.mvplugins.multiverse.core.api.world.options.CloneWorldOptions;
import org.mvplugins.multiverse.core.api.world.options.CreateWorldOptions;
import org.mvplugins.multiverse.core.api.world.options.ImportWorldOptions;
import org.mvplugins.multiverse.core.api.world.options.RegenWorldOptions;
import org.mvplugins.multiverse.core.api.world.options.UnloadWorldOptions;
import org.mvplugins.multiverse.core.api.world.reasons.CloneFailureReason;
import org.mvplugins.multiverse.core.api.world.reasons.CreateFailureReason;
import org.mvplugins.multiverse.core.api.world.reasons.DeleteFailureReason;
import org.mvplugins.multiverse.core.api.world.reasons.ImportFailureReason;
import org.mvplugins.multiverse.core.api.world.reasons.LoadFailureReason;
import org.mvplugins.multiverse.core.api.world.reasons.RegenFailureReason;
import org.mvplugins.multiverse.core.api.world.reasons.RemoveFailureReason;
import org.mvplugins.multiverse.core.api.world.reasons.UnloadFailureReason;

import java.util.Collection;
import java.util.List;

/**
 * This manager contains all the world managing functions that your heart desires.
 *
 * @since 5.0
 */
@Contract
public interface WorldManager {
    /**
     * Creates a new world.
     *
     * @param options The options for customizing the creation of a new world.
     * @return The result of the creation.
     * @since 5.0
     */
    Attempt<LoadedMultiverseWorld, CreateFailureReason> createWorld(CreateWorldOptions options);

    /**
     * Imports an existing world folder.
     *
     * @param options The options for customizing the import of an existing world folder.
     * @return The result of the import.
     * @since 5.0
     */
    Attempt<LoadedMultiverseWorld, ImportFailureReason> importWorld(
            ImportWorldOptions options);

    /**
     * Loads an existing world in config.
     *
     * @param worldName The name of the world to load.
     * @return The result of the load.
     * @since 5.0
     */
    Attempt<LoadedMultiverseWorld, LoadFailureReason> loadWorld(@NotNull String worldName);

    /**
     * Loads an existing world in config.
     *
     * @param world The world to load.
     * @return The result of the load.
     * @since 5.0
     */
    Attempt<LoadedMultiverseWorld, LoadFailureReason> loadWorld(@NotNull MultiverseWorld world);

    /**
     * Unloads an existing multiverse world. It will still remain as an unloaded world.
     *
     * @param options The options for customizing the unloading of a world.
     * @return The result of the unload action.
     * @since 5.0
     */
    Attempt<MultiverseWorld, UnloadFailureReason> unloadWorld(@NotNull UnloadWorldOptions options);

    /**
     * Removes an existing multiverse world. It will be deleted from the worlds config and will no longer be an
     * unloaded world. World files will not be deleted.
     *
     * @param worldName The name of the world to remove.
     * @return The result of the remove.
     * @since 5.0
     */
    Attempt<String, RemoveFailureReason> removeWorld(
            @NotNull String worldName);

    /**
     * Removes an existing multiverse world. It will be deleted from the worlds config and will no longer be an
     * unloaded world. World files will not be deleted.
     *
     * @param world The multiverse world to remove.
     * @return The result of the remove.
     * @since 5.0
     */
    Attempt<String, RemoveFailureReason> removeWorld(@NotNull MultiverseWorld world);

    /**
     * Removes an existing multiverse world. It will be deleted from the worlds config and will no longer be an
     * unloaded world. World files will not be deleted.
     *
     * @param loadedWorld The multiverse world to remove.
     * @return The result of the remove.
     * @since 5.0
     */
    Attempt<String, RemoveFailureReason> removeWorld(@NotNull LoadedMultiverseWorld loadedWorld);

    /**
     * Deletes an existing multiverse world entirely. World will be loaded if it is not already loaded.
     * Warning: This will delete all world files.
     *
     * @param worldName The name of the world to delete.
     * @return The result of the delete action.
     * @since 5.0
     */
    Attempt<String, DeleteFailureReason> deleteWorld(@NotNull String worldName);

    /**
     * Deletes an existing multiverse world entirely. World will be loaded if it is not already loaded.
     * Warning: This will delete all world files.
     *
     * @param world The world to delete.
     * @return The result of the delete action.
     * @since 5.0
     */
    Attempt<String, DeleteFailureReason> deleteWorld(@NotNull MultiverseWorld world);

    /**
     * Deletes an existing multiverse world entirely. Warning: This will delete all world files.
     *
     * @param world The multiverse world to delete.
     * @return The result of the delete action.
     * @since 5.0
     */
    Attempt<String, DeleteFailureReason> deleteWorld(@NotNull LoadedMultiverseWorld world);

    /**
     * Clones an existing multiverse world.
     *
     * @param options The options for customizing the cloning of a world.
     * @return The result of the clone.
     * @since 5.0
     */
    Attempt<LoadedMultiverseWorld, CloneFailureReason> cloneWorld(@NotNull CloneWorldOptions options);

    /**
     * Regenerates a world.
     *
     * @param options The options for customizing the regeneration of a world.
     * @return The result of the regeneration.
     * @since 5.0
     */
    Attempt<LoadedMultiverseWorld, RegenFailureReason> regenWorld(@NotNull RegenWorldOptions options);

    /**
     * Gets a list of all potential worlds that can be loaded from the server folders.
     * Checks based on folder contents and name.
     *
     * @return A list of all potential worlds.
     * @since 5.0
     */
    List<String> getPotentialWorlds();

    /**
     * Get a world that may or may not be loaded. It will an {@link LoadedMultiverseWorld} if the world is loaded,
     * otherwise returns an {@link MultiverseWorld} instance.
     *
     * @param world The bukkit world to get.
     * @return The world if it exists.
     * @since 5.0
     */
    Option<MultiverseWorld> getWorld(@Nullable World world);

    /**
     * Get a world that may or may not be loaded. It will an {@link LoadedMultiverseWorld} if the world is loaded,
     * otherwise returns an {@link MultiverseWorld} instance.
     *
     * @param worldName The name of the world to get.
     * @return The world if it exists.
     * @since 5.0
     */
    Option<MultiverseWorld> getWorld(@Nullable String worldName);

    /**
     * Get a world that may or may not be loaded by name or alias. It will an {@link LoadedMultiverseWorld} if the world is loaded,
     * otherwise returns an {@link MultiverseWorld} instance. World name will still be prioritized over alias.
     *
     * @param worldNameOrAlias The name or alias of the world to get.
     * @return The world if it exists.
     * @since 5.0
     */
    Option<MultiverseWorld> getWorldByNameOrAlias(@Nullable String worldNameOrAlias);

    /**
     * <p>Get a list of all worlds that may or may not be loaded. It will an {@link LoadedMultiverseWorld} if the world
     * is loaded, otherwise you will get an {@link MultiverseWorld} instance.</p>
     *
     * <p>If you want only unloaded worlds, use {@link #getUnloadedWorlds()}. If you want only loaded worlds, use
     * {@link #getLoadedWorlds()}.</p>
     *
     * @return A list of all worlds that may or may not be loaded.
     * @since 5.0
     */
    Collection<MultiverseWorld> getWorlds();

    /**
     * Check if a world is a world is known to multiverse, but may or may not be loaded.
     *
     * @param worldName The name of the world to check.
     * @return True if the world is a world is known to multiverse, but may or may not be loaded.
     * @since 5.0
     */
    boolean isWorld(@Nullable String worldName);

    /**
     * Get a world that is not loaded.
     *
     * @param worldName The name of the world to get.
     * @return The world if it exists.
     * @since 5.0
     */
    Option<MultiverseWorld> getUnloadedWorld(@Nullable String worldName);

    /**
     * Get a world that is not loaded by name or alias. World name will still be prioritized over alias.
     *
     * @param worldNameOrAlias The name or alias of the world to get.
     * @return The world if it exists.
     * @since 5.0
     */
    Option<MultiverseWorld> getUnloadedWorldByNameOrAlias(@Nullable String worldNameOrAlias);

    /**
     * Get a list of all worlds that are not loaded.
     *
     * @return A list of all worlds that are not loaded.
     * @since 5.0
     */
    Collection<MultiverseWorld> getUnloadedWorlds();

    /**
     * Check if a world is a world that is not loaded.
     *
     * @param worldName The name of the world to check.
     * @return True if the world is a world that is not loaded.
     * @since 5.0
     */
    boolean isUnloadedWorld(@Nullable String worldName);

    /**
     * Get a multiverse world that is loaded.
     *
     * @param world The bukkit world that should be loaded.
     * @return The multiverse world if it exists.
     * @since 5.0
     */
    Option<LoadedMultiverseWorld> getLoadedWorld(@Nullable World world);

    /**
     * Get a multiverse world that is loaded.
     *
     * @param world The world that should be loaded.
     * @return The multiverse world if it exists.
     * @since 5.0
     */
    Option<LoadedMultiverseWorld> getLoadedWorld(@Nullable MultiverseWorld world);

    /**
     * Get a multiverse world that is loaded.
     *
     * @param worldName The name of the world to get.
     * @return The multiverse world if it exists.
     * @since 5.0
     */
    Option<LoadedMultiverseWorld> getLoadedWorld(@Nullable String worldName);

    /**
     * Get a multiverse world that is loaded by name or alias. World name will still be prioritized over alias.
     *
     * @param worldNameOrAlias The name or alias of the world to get.
     * @return The multiverse world if it exists.
     * @since 5.0
     */
    Option<LoadedMultiverseWorld> getLoadedWorldByNameOrAlias(@Nullable String worldNameOrAlias);

    /**
     * Get a read-only list of all multiverse worlds that are loaded.
     *
     * @return A list of all multiverse worlds that are loaded.
     * @since 5.0
     */
    Collection<LoadedMultiverseWorld> getLoadedWorlds();

    /**
     * Check if a world is a multiverse world that is loaded.
     *
     * @param world The bukkit world to check.
     * @return True if the world is a multiverse world that is loaded.
     * @since 5.0
     */
    boolean isLoadedWorld(@Nullable World world);

    /**
     * Check if a world is a multiverse world that is loaded.
     *
     * @param world The world to check.
     * @return True if the world is a multiverse world that is loaded.
     * @since 5.0
     */
    boolean isLoadedWorld(@Nullable MultiverseWorld world);

    /**
     * Check if a world is a multiverse world that is loaded.
     *
     * @param worldName The name of the world to check.
     * @return True if the world is a multiverse world that is loaded.
     * @since 5.0
     */
    boolean isLoadedWorld(@Nullable String worldName);

    /**
     * Saves the worlds.yml config.
     *
     * @return true if it had successfully saved the file.
     * @since 5.0
     */
    boolean saveWorldsConfig();
}
