/******************************************************************************
 * Multiverse 2 Copyright (c) the Multiverse Team 2011.                       *
 * Multiverse 2 is licensed under the BSD License.                            *
 * For more information please check the README.md file included              *
 * with this project.                                                         *
 ******************************************************************************/

package com.onarandombox.MultiverseCore.api;

import com.onarandombox.MultiverseCore.utils.PurgeWorlds;
import com.onarandombox.MultiverseCore.utils.SimpleWorldPurger;
import org.bukkit.World;
import org.bukkit.World.Environment;
import org.bukkit.WorldType;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.generator.ChunkGenerator;

import java.io.File;
import java.util.Collection;
import java.util.List;

/**
 * Multiverse 2 World Manager API
 * <p>
 * This API contains all of the world managing
 * functions that your heart desires!
 */
public interface

MVWorldManager {
    /**
     * Add a new World to the Multiverse Setup.
     *
     * @param name               World Name
     * @param env                Environment Type
     * @param seedString         The seed in the form of a string.
     *                             If the seed is a Long,
     *                             it will be interpreted as such.
     * @param type               The Type of the world to be made.
     * @param generateStructures If true, this world will get NPC villages.
     * @param generator          The Custom generator plugin to use.
     * @return True if the world is added, false if not.
     */
    boolean addWorld(String name, Environment env, String seedString, WorldType type, Boolean generateStructures,
                     String generator);

    /**
     * Add a new World to the Multiverse Setup.
     *
     * @param name               World Name
     * @param env                Environment Type
     * @param seedString         The seed in the form of a string.
     *                             If the seed is a Long,
     *                             it will be interpreted as such.
     * @param type               The Type of the world to be made.
     * @param generateStructures If true, this world will get NPC villages.
     * @param generator          The Custom generator plugin to use.
     * @param useSpawnAdjust If true, multiverse will search for a safe spawn. If not, It will not modify the level.dat.
     * @return True if the world is added, false if not.
     */
    boolean addWorld(String name, Environment env, String seedString, WorldType type, Boolean generateStructures,
                     String generator, boolean useSpawnAdjust);

    /**
     * Make a copy of a world.
     *
     * @param oldName            Name of world to be copied
     * @param newName            Name of world to be created
     * @param generator          The Custom generator plugin to use. Ignored.
     * @return True if the world is copied successfully, false if not.
     * @deprecated Use {@link #cloneWorld(String, String)} instead.
     */
    @Deprecated
    boolean cloneWorld(String oldName, String newName, String generator);

    /**
     * Make a copy of a world.
     *
     * @param oldName
     *            Name of world to be copied
     * @param newName
     *            Name of world to be created
     * @return True if the world is copied successfully, false if not.
     */
    boolean cloneWorld(String oldName, String newName);

    /**
     * Remove the world from the Multiverse list, from the config and deletes
     * the folder.
     *
     * @param name
     *            The name of the world to remove
     * @return True if success, false if failure.
     */
    boolean deleteWorld(String name);

    /**
     * Remove the world from the Multiverse list, from the
     * config if wanted, and deletes the folder.
     *
     * @param name         The name of the world to remove
     * @param removeConfig If true(default), we'll remove the entries from the
     *                     config. If false, they'll stay and the world may come back.
     * @return True if success, false if failure.
     */
    boolean deleteWorld(String name, boolean removeConfig);

    /**
     *
     * @param name The name of the world to remove
     * @param removeFromConfig If true(default), we'll remove the entries from the
     *                         config. If false, they'll stay and the world may come back.
     * @param deleteWorldFolder If true the world folder will be completely deleted. If false
     *                          only the contents of the world folder will be deleted
     * @return True if success, false if failure.
     */
    boolean deleteWorld(String name, boolean removeFromConfig, boolean deleteWorldFolder);

    /**
     * Unload a world from Multiverse.
     *
     * @param name Name of the world to unload
     * @return True if the world was unloaded, false if not.
     */
    boolean unloadWorld(String name);

    /**
     * Unload a world from Multiverse with option to prevent calling unloadWorld in Bukkit.
     *
     * @param name Name of the world to unload
     * @param unloadBukkit True if Bukkit world should be unloaded
     * @return True if the world was unloaded, false if not.
     */
    boolean unloadWorld(String name, boolean unloadBukkit);

    /**
     * Loads the world. Only use this if the world has been
     * unloaded with {@link #unloadWorld(String)}.
     *
     * @param name The name of the world to load
     * @return True if success, false if failure.
     */
    boolean loadWorld(String name);

    /**
     * Removes all players from the specified world.
     *
     * @param name World to remove players from.
     */
    void removePlayersFromWorld(String name);

    /**
     * Test if a given chunk generator is valid.
     *
     * @param generator   The generator name.
     * @param generatorID The generator id.
     * @param worldName   The worldName to use as the default.
     * @return A {@link ChunkGenerator} or null
     */
    ChunkGenerator getChunkGenerator(String generator, String generatorID, String worldName);

    /**
     * Returns a list of all the worlds Multiverse knows about.
     *
     * @return A list of {@link MultiverseWorld}.
     */
    Collection<MultiverseWorld> getMVWorlds();


    /**
     * Returns a {@link MultiverseWorld} if it exists, and null if it does not.
     * This will search name AND alias.
     *
     * @param name The name or alias of the world to get.
     * @return A {@link MultiverseWorld} or null.
     */
    MultiverseWorld getMVWorld(String name);

    /**
     * Returns a {@link MultiverseWorld} if the world with name given exists, and null if it does not.
     * This will search optionally for alias names.
     *
     * @param name          The name or optionally the alias of the world to get.
     * @param checkAliases  Indicates whether to check for world alias name.
     * @return A {@link MultiverseWorld} or null.
     */
    MultiverseWorld getMVWorld(String name, boolean checkAliases);

    /**
     * Returns a {@link MultiverseWorld} if it exists, and null if it does not.
     *
     * @param world The Bukkit world to check.
     * @return A {@link MultiverseWorld} or null.
     */
    MultiverseWorld getMVWorld(World world);

    /**
     * Checks to see if the given name is a valid {@link MultiverseWorld}
     * Searches based on world name AND alias.
     *
     * @param name The name or alias of the world to check.
     * @return True if the world exists, false if not.
     */
    boolean isMVWorld(String name);

    /**
     * Checks to see if the given name is a valid {@link MultiverseWorld}.
     * Optionally searches by alias is specified.
     *
     * @param name          The name or alias of the world to check.
     * @param checkAliases  Indicates whether to check for world alias name.
     * @return True if the world exists, false if not.
     */
    boolean isMVWorld(String name, boolean checkAliases);

    /**
     * Checks to see if the given world is a valid {@link MultiverseWorld}.
     *
     * @param world The Bukkit world to check.
     * @return True if the world has been loaded into MV2, false if not.
     */
    boolean isMVWorld(World world);

    /**
     * Load the Worlds &amp; Settings from the configuration file.
     *
     * @param forceLoad If set to true, this will perform a total
     *                  reset and not just load new worlds.
     */
    void loadWorlds(boolean forceLoad);

    /**
     * Loads the Worlds &amp; Settings for any worlds that bukkit loaded before us.
     * <p>
     * This way people will _always_ have some worlds in the list.
     */
    void loadDefaultWorlds();

    /**
     * Return the World Purger.
     *
     * @return A valid {@link PurgeWorlds}.
     * @deprecated {@link PurgeWorlds} is deprecated!
     */
    @Deprecated
    PurgeWorlds getWorldPurger();

    /**
     * Gets the {@link WorldPurger}.
     * <p>
     * TODO: Remove {@link #getWorldPurger()} and replace it with this method.
     * @return The {@link WorldPurger} this {@link MVWorldManager} is using.
     * @see WorldPurger
     * @see SimpleWorldPurger
     */
    WorldPurger getTheWorldPurger();

    /**
     * Gets the world players will spawn in on first join.
     * Currently this always returns worlds.get(0) from Bukkit.
     *
     * @return A Multiverse world that players will spawn in or null if no MV world has been set.
     */
    MultiverseWorld getSpawnWorld();

    /**
     * Gets the list of worlds in the config, but unloaded.
     *
     * @return A List of worlds as strings.
     */
    List<String> getUnloadedWorlds();

    /**
     * This method populates an internal list and needs to be called after multiverse initialization.
     */
    void getDefaultWorldGenerators();

    /**
     * Load the config from a file.
     *
     * @param file The file to load.
     * @return A loaded configuration.
     */
    FileConfiguration loadWorldConfig(File file);

    /**
     * Saves the world config to disk.
     *
     * @return True if success, false if fail.
     */
    boolean saveWorldsConfig();

    /**
     * Remove the world from the Multiverse list and from the config.
     *
     * @param name The name of the world to remove
     * @return True if success, false if failure.
     */
    boolean removeWorldFromConfig(String name);

    /**
     * Sets the initial spawn world for new players.
     *
     * @param world The World new players should spawn in.
     */
    void setFirstSpawnWorld(String world);

    /**
     * Gets the world players should spawn in first.
     *
     * @return The {@link MultiverseWorld} new players should spawn in.
     */
    MultiverseWorld getFirstSpawnWorld();

    /**
     * Regenerates a world.
     *
     * @param name          Name of the world to regenerate
     * @param useNewSeed    If a new seed should be used
     * @param randomSeed    If the new seed should be random
     * @param seed          The seed of the world.
     *
     * @return True if success, false if fail.
     */
    boolean regenWorld(String name, boolean useNewSeed, boolean randomSeed, String seed);

    /**
     * Regenerates a world.
     *
     * @param name          Name of the world to regenerate
     * @param useNewSeed    If a new seed should be used
     * @param randomSeed    If the new seed should be random
     * @param seed          The seed of the world.
     * @param keepGameRules If GameRules should be kept on world regen.
     *
     * @return True if success, false if fail.
     */
    boolean regenWorld(String name, boolean useNewSeed, boolean randomSeed, String seed, boolean keepGameRules);

    boolean isKeepingSpawnInMemory(World world);
    
    /**
     * Checks whether Multiverse knows about a provided unloaded world. This
     * method will check the parameter against the alias mappings.
     *
     * @param name The name of the unloaded world
     * @param includeLoaded The value to return if the world is loaded
     *
     * @return True if the world exists and is unloaded. False if the world
     * does not exist. {@code includeLoaded} if the world exists and is loaded.
     */
    boolean hasUnloadedWorld(String name, boolean includeLoaded);

    /**
     * Get all the possible worlds that Multiverse has detected to be importable.
     *
     * @return A collection of world names that are deemed importable.
     */
    Collection<String> getPotentialWorlds();
}
