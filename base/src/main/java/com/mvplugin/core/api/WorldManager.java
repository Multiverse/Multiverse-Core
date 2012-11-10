package com.mvplugin.core.api;

import com.mvplugin.core.WorldCreationException;
import com.mvplugin.core.minecraft.Generator;
import com.mvplugin.core.minecraft.WorldEnvironment;
import com.mvplugin.core.minecraft.WorldType;

import java.io.IOException;
import java.util.Collection;
import java.util.List;

/**
 * Multiverse 2 World Manager API
 * <p>
 * This API contains all of the world managing
 * functions that your heart desires!
 */
public interface WorldManager {

    public class WorldCreationSettings {

        private final String name;
        private WorldEnvironment env;
        private Long seed;
        private WorldType type;
        private Boolean generateStructures;
        private String generator;
        private boolean adjustSpawn = true;

        public WorldCreationSettings(final String name) {
            if (name == null) {
                throw new IllegalArgumentException("name cannot be null!");
            }
            this.name = name;
        }

        public String name() {
            return name;
        }

        public WorldEnvironment env() {
            return env;
        }

        public Long seed() {
            return seed;
        }

        public WorldType type() {
            return type;
        }

        public Boolean generateStructures() {
            return generateStructures;
        }

        public String generator() {
            return generator;
        }

        public boolean adjustSpawn() {
            return adjustSpawn;
        }

        public WorldCreationSettings env(WorldEnvironment e) {
            this.env = e;
            return this;
        }

        public WorldCreationSettings seed(Long l) {
            this.seed = l;
            return this;
        }

        public WorldCreationSettings type(WorldType t) {
            this.type = t;
            return this;
        }

        public WorldCreationSettings generateStructures(Boolean b) {
            this.generateStructures = b;
            return this;
        }

        public WorldCreationSettings generator(String g) {
            this.generator = g;
            return this;
        }

        public WorldCreationSettings adjustSpawn(boolean a) {
            this.adjustSpawn = a;
            return this;
        }
    }

    /**
     * Gets an existing WorldProperties object or creates a new one based on the name.
     *
     * TODO explain that they should use getWorld() in general.
     *
     * @param worldName The name of the world to get properties for.
     * @return The world properties for the given world name.
     * @throws IOException In case there are any issues accessing the persistence for the world properties.
     */
    WorldProperties getWorldProperties(final String worldName) throws IOException;

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
     * @return The new world.
     * @throws WorldCreationException If world creation fails.
     */
    MultiverseWorld addWorld(String name,
               WorldEnvironment env,
               String seedString,
               WorldType type,
               Boolean generateStructures,
               String generator) throws WorldCreationException;

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
     * @return The new world.
     * @throws WorldCreationException If world creation fails.
     */
    MultiverseWorld addWorld(String name,
               WorldEnvironment env,
               String seedString,
               WorldType type,
               Boolean generateStructures,
               String generator,
               boolean useSpawnAdjust) throws WorldCreationException;

    /**
     * Adds a new World to the Multiverse Setup.
     * @param settings Settings for the world creation.
     * @return The new world.
     * @throws WorldCreationException If world creation fails.
     */
    MultiverseWorld addWorld(WorldCreationSettings settings) throws WorldCreationException;

    /**
     * Make a copy of a world.
     *
     * @param oldName            Name of world to be copied
     * @param newName            Name of world to be created
     * @param generator          The Custom generator plugin to use.
     * @return True if the world is copied successfully, false if not.
     */
    //TODO boolean cloneWorld(String oldName, String newName, String generator);

    /**
     * Remove the world from the Multiverse list, from the
     * config and deletes the folder.
     *
     * @param name The name of the world to remove
     * @return True if success, false if failure.
     */
    //TODO boolean deleteWorld(String name);

    /**
     * Remove the world from the Multiverse list, from the
     * config if wanted, and deletes the folder.
     *
     * @param name         The name of the world to remove
     * @param removeConfig If true(default), we'll remove the entries from the
     *                     config. If false, they'll stay and the world may come back.
     * @return True if success, false if failure.
     */
    //TODO boolean deleteWorld(String name, boolean removeConfig);

    /**
     *
     * @param name The name of the world to remove
     * @param removeFromConfig If true(default), we'll remove the entries from the
     *                         config. If false, they'll stay and the world may come back.
     * @param deleteWorldFolder If true the world folder will be completely deleted. If false
     *                          only the contents of the world folder will be deleted
     * @return True if success, false if failure.
     */
    //TODO boolean deleteWorld(String name, boolean removeFromConfig, boolean deleteWorldFolder);

    /**
     * Unload a world from Multiverse.
     *
     * @param name Name of the world to unload
     * @return True if the world was unloaded, false if not.
     */
    //TODO boolean unloadWorld(String name);

    /**
     * Loads the world. Only use this if the world has been
     * unloaded with {@link #unloadWorld(String)}.
     *
     * @param name The name of the world to load
     * @return True if success, false if failure.
     */
    //TODO boolean loadWorld(String name);

    /**
     * Removes all players from the specified world.
     *
     * @param name World to remove players from.
     */
    //TODO void removePlayersFromWorld(String name);

    /**
     * Test if a given chunk generator is valid.
     *
     * @param generator   The generator name.
     * @param generatorID The generator id.
     * @param worldName   The worldName to use as the default.
     * @return A {@link Generator} or null
     */
    //TODO Generator getChunkGenerator(String generator, String generatorID, String worldName);

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
    //TODO W getMVWorld(String name);

    /**
     * Checks to see if the given name is a valid {@link MultiverseWorld}.
     *
     * @param name The name or alias of the world to check.
     * @return True if the world exists, false if not.
     */
    boolean isMVWorld(String name);

    /**
     * Load the Worlds & Settings from the configuration file.
     *
     * @param forceLoad If set to true, this will perform a total
     *                  reset and not just load new worlds.
     */
    //TODO void loadWorlds(boolean forceLoad);

    /**
     * Loads the Worlds & Settings for any worlds that bukkit loaded before us.
     * <p>
     * This way people will _always_ have some worlds in the list.
     */
    //TODO void loadDefaultWorlds();

    //TODO WorldPurger getWorldPurger();

    /**
     * Gets the world players will spawn in on first join.
     * Currently this always returns worlds.get(0) from Bukkit.
     *
     * @return A Multiverse world that players will spawn in or null if no MV world has been set.
     */
    //TODO MultiverseWorld getSpawnWorld();

    /**
     * Gets the list of worlds in the config, but unloaded.
     *
     * @return A List of worlds as strings.
     */
    List<String> getUnloadedWorlds();

    /**
     * Saves the world config to disk.
     *
     * @return True if success, false if fail.
     */
    //TODO boolean saveWorldsConfig();

    /**
     * Remove the world from the Multiverse list and from the config.
     *
     * @param name The name of the world to remove
     * @return True if success, false if failure.
     */
    //TODO boolean removeWorldFromConfig(String name);

    /**
     * Sets the initial spawn world for new players.
     *
     * @param world The World new players should spawn in.
     */
    //TODO void setFirstSpawnWorld(String world);

    /**
     * Gets the world players should spawn in first.
     *
     * @return The {@link MultiverseWorld} new players should spawn in.
     */
    //TODO MultiverseWorld getFirstSpawnWorld();

    /**
     * Regenerates a world.
     *
     * @param name Name of the world to regenerate
     * @param useNewSeed If a new seed should be used
     * @param randomSeed IF the new seed should be random
     * @param seed The seed of the world.
     *
     * @return True if success, false if fail.
     */
    //TODO boolean regenWorld(String name, boolean useNewSeed, boolean randomSeed, String seed);
}
