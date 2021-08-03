/******************************************************************************
 * Multiverse 2 Copyright (c) the Multiverse Team 2011.                       *
 * Multiverse 2 is licensed under the BSD License.                            *
 * For more information please check the README.md file included              *
 * with this project.                                                         *
 ******************************************************************************/

package com.onarandombox.MultiverseCore.utils;

import com.dumptruckman.minecraft.util.Logging;
import com.onarandombox.MultiverseCore.MVWorld;
import com.onarandombox.MultiverseCore.MultiverseCore;
import com.onarandombox.MultiverseCore.MultiverseCoreConfiguration;
import com.onarandombox.MultiverseCore.WorldProperties;
import com.onarandombox.MultiverseCore.api.MVWorldManager;
import com.onarandombox.MultiverseCore.api.MultiverseWorld;
import com.onarandombox.MultiverseCore.api.SafeTTeleporter;
import com.onarandombox.MultiverseCore.api.WorldPurger;
import com.onarandombox.MultiverseCore.event.MVWorldDeleteEvent;
import org.bukkit.Bukkit;
import org.bukkit.GameRule;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.World.Environment;
import org.bukkit.WorldCreator;
import org.bukkit.WorldType;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.generator.ChunkGenerator;
import org.bukkit.permissions.Permission;
import org.bukkit.permissions.PermissionDefault;
import org.bukkit.plugin.Plugin;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.Stack;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * Public facing API to add/remove Multiverse worlds.
 */
public class WorldManager implements MVWorldManager {
    private final MultiverseCore plugin;
    private final WorldPurger worldPurger;
    private final Map<String, MultiverseWorld> worlds;
    private Map<String, WorldProperties> worldsFromTheConfig;
    private FileConfiguration configWorlds = null;
    private Map<String, String> defaultGens;
    private String firstSpawn;

    public WorldManager(MultiverseCore core) {
        this.plugin = core;
        this.worldsFromTheConfig = new HashMap<String, WorldProperties>();
        this.worlds = new ConcurrentHashMap<String, MultiverseWorld>();
        this.worldPurger = new SimpleWorldPurger(plugin);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void getDefaultWorldGenerators() {
        this.defaultGens = new HashMap<String, String>();
        File[] files = this.plugin.getServerFolder().listFiles(new FilenameFilter() {
            @Override
            public boolean accept(File file, String s) {
                return s.equalsIgnoreCase("bukkit.yml");
            }
        });
        if (files != null && files.length == 1) {
            FileConfiguration bukkitConfig = YamlConfiguration.loadConfiguration(files[0]);
            if (bukkitConfig.isConfigurationSection("worlds")) {
                Set<String> keys = bukkitConfig.getConfigurationSection("worlds").getKeys(false);
                for (String key : keys) {
                    defaultGens.put(key, bukkitConfig.getString("worlds." + key + ".generator", ""));
                }
            }
        } else {
            Logging.warning("Could not read 'bukkit.yml'. Any Default worldgenerators will not be loaded!");
        }
    }

    /**
     * {@inheritDoc}
     * @deprecated Use {@link #cloneWorld(String, String)} instead.
     */
    @Override
    @Deprecated
    public boolean cloneWorld(String oldName, String newName, String generator) {
        return this.cloneWorld(oldName, newName);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean cloneWorld(String oldName, String newName) {
        // Make sure we already know about the old world and that we don't
        // already know about the new world.
        if (!this.worldsFromTheConfig.containsKey(oldName)) {
            for (Map.Entry<String, WorldProperties> entry : this.worldsFromTheConfig.entrySet()) {
                if (oldName.equals(entry.getValue().getAlias())) {
                    oldName = entry.getKey();
                    break;
                }
            }
            if (!this.worldsFromTheConfig.containsKey(oldName)) {
                Logging.warning("Old world '%s' does not exist", oldName);
                return false;
            }
        }
        if (this.isMVWorld(newName)) {
            Logging.warning("New world '%s' already exists", newName);
            return false;
        }

        // Check for valid world name
        if (!(WorldNameChecker.isValidWorldName(oldName) && WorldNameChecker.isValidWorldName(newName))) {
            return false;
        }

        final File oldWorldFile = new File(this.plugin.getServer().getWorldContainer(), oldName);
        final File newWorldFile = new File(this.plugin.getServer().getWorldContainer(), newName);
        final List<String> ignoreFiles = new ArrayList<>(Arrays.asList("session.lock", "uid.dat"));

        // Make sure the new world doesn't exist outside of multiverse.
        if (newWorldFile.exists()) {
            Logging.warning("Folder for new world '%s' already exists", newName);
            return false;
        }

        // Load the old world... but just the metadata.
        boolean wasJustLoaded = false;
        boolean wasLoadSpawn = false;
        if (this.plugin.getServer().getWorld(oldName) == null) {
            wasJustLoaded = true;
            WorldProperties props = this.worldsFromTheConfig.get(oldName);
            wasLoadSpawn = props.isKeepingSpawnInMemory();
            if (wasLoadSpawn) {
                // No chunks please.
                props.setKeepSpawnInMemory(false);
            }
            if (!this.loadWorld(oldName)) {
                return false;
            }
            this.plugin.getServer().getWorld(oldName).setAutoSave(false);
        }
        
        // Grab a bit of metadata from the old world.
        MultiverseWorld oldWorld = getMVWorld(oldName);

        // Don't need the loaded world anymore.
        if (wasJustLoaded) {
            this.unloadWorld(oldName, true);
            oldWorld = null;
            if (wasLoadSpawn) {
                this.worldsFromTheConfig.get(oldName).setKeepSpawnInMemory(true);
            }
        }

        boolean wasAutoSave = false;
        if (oldWorld != null && oldWorld.getCBWorld().isAutoSave()) {
            wasAutoSave = true;
            Logging.config("Saving world '%s'", oldName);
            oldWorld.getCBWorld().setAutoSave(false);
            oldWorld.getCBWorld().save();
        }
        Logging.config("Copying files for world '%s'", oldName);
        if (!FileUtils.copyFolder(oldWorldFile, newWorldFile, ignoreFiles)) {
            Logging.warning("Failed to copy files for world '%s', see the log info", newName);
            return false;
        }
        if (oldWorld != null && wasAutoSave) {
            oldWorld.getCBWorld().setAutoSave(true);
        }
        
        if (newWorldFile.exists()) {
            Logging.fine("Succeeded at copying files");

            // initialize new properties with old ones
            WorldProperties newProps = new WorldProperties();
            newProps.copyValues(this.worldsFromTheConfig.get(oldName));
            // don't keep the alias the same -- that would be useless
            newProps.setAlias("");
            // store the new properties in worlds config map
            this.worldsFromTheConfig.put(newName, newProps);

            // save the worlds config to disk (worlds.yml)
            if (!saveWorldsConfig()) {
                Logging.severe("Failed to save worlds.yml");
                return false;
            }

            // actually load the world
            if (doLoad(newName)) {
               Logging.fine("Succeeded at loading cloned world '" + newName + "'");
               return true;
            }
            Logging.severe("Failed to load the cloned world '" + newName + "'");
            return false;
        }

        Logging.warning("Failed to copy files for world '%s', see the log info", newName);
        return false;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean addWorld(String name, Environment env, String seedString, WorldType type, Boolean generateStructures,
                            String generator) {
        return this.addWorld(name, env, seedString, type, generateStructures, generator, true);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean addWorld(String name, Environment env, String seedString, WorldType type, Boolean generateStructures,
                            String generator, boolean useSpawnAdjust) {
        if (name.equalsIgnoreCase("plugins") || name.equalsIgnoreCase("logs")) {
            return false;
        }

        if (!WorldNameChecker.isValidWorldName(name)) {
            Logging.warning("Invalid world name '" + name + "'");
            Logging.warning("World name should not contain spaces or special characters!");
            return false;
        }

        Long seed = null;
        WorldCreator c = new WorldCreator(name);
        if (seedString != null && seedString.length() > 0) {
            try {
                seed = Long.parseLong(seedString);
            } catch (NumberFormatException numberformatexception) {
                seed = (long) seedString.hashCode();
            }
            c.seed(seed);
        }

        // TODO: Use the fancy kind with the commandSender
        if (generator != null && generator.length() != 0) {
            c.generator(generator);
        }
        c.environment(env);
        if (type != null) {
            c.type(type);
        }
        if (generateStructures != null) {
            c.generateStructures(generateStructures);
        }

        // Important: doLoad() needs the MVWorld-object in worldsFromTheConfig
        if (!worldsFromTheConfig.containsKey(name)) {
            WorldProperties props = new WorldProperties(useSpawnAdjust, env);
            worldsFromTheConfig.put(name, props);
        }

        StringBuilder builder = new StringBuilder();
        builder.append("Loading World & Settings - '").append(name).append("'");
        builder.append(" - Env: ").append(env);
        builder.append(" - Type: ").append(type);
        if (seed != null) {
            builder.append(" & seed: ").append(seed);
        }
        if (generator != null) {
            builder.append(" & generator: ").append(generator);
        }
        Logging.info(builder.toString());

        if (!doLoad(c, true)) {
            Logging.severe("Failed to Create/Load the world '" + name + "'");
            return false;
        }

        // set generator (special case because we can't read it from org.bukkit.World)
        this.worlds.get(name).setGenerator(generator);

        this.saveWorldsConfig();
        return true;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ChunkGenerator getChunkGenerator(String generator, final String generatorID, final String worldName) {
        if (generator == null) {
            return null;
        }

        final Plugin myPlugin = this.plugin.getServer().getPluginManager().getPlugin(generator);
        if (myPlugin == null) {
            return null;
        } else {
            return plugin.getUnsafeCallWrapper().wrap(new Callable<ChunkGenerator>() {
                @Override
                public ChunkGenerator call() throws Exception {
                    return myPlugin.getDefaultWorldGenerator(worldName, generatorID);
                }
            }, myPlugin.getName(), "Failed to get the default chunk generator: %s");
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean removeWorldFromConfig(String name) {
        if (!unloadWorld(name)) {
            return false;
        }
        if (this.worldsFromTheConfig.containsKey(name)) {
            this.worldsFromTheConfig.remove(name);
            Logging.info("World '%s' was removed from config.yml", name);

            this.saveWorldsConfig();
            return true;
        } else {
            Logging.info("World '%s' was already removed from config.yml", name);
        }
        return false;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setFirstSpawnWorld(String world) {
        if ((world == null) && (this.plugin.getServer().getWorlds().size() > 0)) {
            this.firstSpawn = this.plugin.getServer().getWorlds().get(0).getName();
        } else {
            this.firstSpawn = world;
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public MultiverseWorld getFirstSpawnWorld() {
        MultiverseWorld world = this.getMVWorld(this.firstSpawn);
        if (world == null) {
            // If the spawn world was unloaded, get the default world
            Logging.warning("The world specified as the spawn world (" + this.firstSpawn + ") did not exist!!");
            try {
                return this.getMVWorld(this.plugin.getServer().getWorlds().get(0));
            } catch (IndexOutOfBoundsException e) {
                // This should only happen in tests.
                return null;
            }
        }
        return world;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean unloadWorld(String name) {
        return this.unloadWorld(name, true);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean unloadWorld(String name, boolean unloadBukkit) {
        if (this.worlds.containsKey(name)) {
            this.worldsFromTheConfig.get(name).cacheVirtualProperties();
            if (unloadBukkit && this.unloadWorldFromBukkit(name, true)) {
                this.worlds.remove(name);
                Logging.info("World '%s' was unloaded from Bukkit.", name);
                return true;
            } else if (!unloadBukkit){
                this.worlds.remove(name);
                Logging.info("World '%s' was unloaded from Multiverse.", name);
                return true;
            } else {
                Logging.warning("World '%s' could not be unloaded from Bukkit. Is it a default world?", name);
            }
        } else if (this.plugin.getServer().getWorld(name) != null) {
            Logging.warning("Hmm Multiverse does not know about this world but it's loaded in memory.");
            Logging.warning("To let Multiverse know about it, use:");
            Logging.warning("/mv import %s %s", name, this.plugin.getServer().getWorld(name).getEnvironment().toString());
        } else if (this.worldsFromTheConfig.containsKey(name)) {
            return true; // it's already unloaded
        } else {
            Logging.info("Multiverse does not know about '%s' and it's not loaded by Bukkit.", name);
        }
        return false;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean loadWorld(String name) {
        // Check if the World is already loaded
        if (this.worlds.containsKey(name)) {
            return true;
        }

        // Check that the world is in the config
        if (worldsFromTheConfig.containsKey(name)) {
            return doLoad(name);
        } else {
            return false;
        }
    }

    private void brokenWorld(String name) {
        Logging.severe("The world '" + name + "' could NOT be loaded because it contains errors and is probably corrupt!");
        Logging.severe("Try using Minecraft Region Fixer to repair your world! '" + name + "'");
        Logging.severe("https://github.com/Fenixin/Minecraft-Region-Fixer");
    }

    private void nullWorld(String name) {
        Logging.severe("The world '" + name + "' could NOT be loaded because the server didn't like it!");
        Logging.severe("We don't really know why this is. Contact the developer of your server software!");
        Logging.severe("Server version info: " + Bukkit.getServer().getVersion());
    }

    private boolean doLoad(String name) {
        return doLoad(name, false, null);
    }

    private boolean doLoad(String name, boolean ignoreExists, WorldType type) {
        if (!worldsFromTheConfig.containsKey(name))
            throw new IllegalArgumentException("That world doesn't exist!");

        final WorldProperties world = worldsFromTheConfig.get(name);
        final WorldCreator creator = WorldCreator.name(name);

        creator.environment(world.getEnvironment()).seed(world.getSeed());
        if (type != null) {
            creator.type(type);
        }

        boolean generatorSuccess = true;
        if ((world.getGenerator() != null) && (!world.getGenerator().equals("null")))
            generatorSuccess = null != plugin.getUnsafeCallWrapper().wrap(new Callable<Object>() {
                @Override
                public Object call() throws Exception {
                    creator.generator(world.getGenerator());
                    return new Object();
                }
            }, "the generator plugin", "Failed to set the generator for world '%s' to '%s': %s", name, world.getGenerator());

        return generatorSuccess && doLoad(creator, ignoreExists);
    }

    private boolean doLoad(WorldCreator creator, boolean ignoreExists) {
        String worldName = creator.name();
        if (!worldsFromTheConfig.containsKey(worldName))
            throw new IllegalArgumentException("That world doesn't exist!");
        if (worlds.containsKey(worldName))
            throw new IllegalArgumentException("That world is already loaded!");

        if (!ignoreExists && !new File(this.plugin.getServer().getWorldContainer(), worldName).exists() && !new File(this.plugin.getServer().getWorldContainer().getParent(), worldName).exists()) {
            Logging.warning("WorldManager: Can't load this world because the folder was deleted/moved: " + worldName);
            Logging.warning("Use '/mv remove' to remove it from the config!");
            return false;
        }

        WorldProperties mvworld = worldsFromTheConfig.get(worldName);
        World cbworld;
        try {
            cbworld = creator.createWorld();
        } catch (Exception e) {
            e.printStackTrace();
            brokenWorld(worldName);
            return false;
        }
        if (cbworld == null) {
            nullWorld(worldName);
            return false;
        }
        MVWorld world = new MVWorld(plugin, cbworld, mvworld);
        if (MultiverseCoreConfiguration.getInstance().isAutoPurgeEnabled()) {
            this.worldPurger.purgeWorld(world);
        }
        this.worlds.put(worldName, world);
        return true;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean deleteWorld(String name, boolean removeFromConfig, boolean deleteWorldFolder) {
        if (this.hasUnloadedWorld(name, false)) {
            // Attempt to load if unloaded so we can actually delete the world
            if (!this.doLoad(name)) {
                return false;
            }
        }

        World world = this.plugin.getServer().getWorld(name);
        if (world == null) {
            // We can only delete loaded worlds
            return false;
        }

        // call the event!
        MVWorldDeleteEvent mvwde = new MVWorldDeleteEvent(getMVWorld(name), removeFromConfig);
        this.plugin.getServer().getPluginManager().callEvent(mvwde);
        if (mvwde.isCancelled()) {
            Logging.fine("Tried to delete a world, but the event was cancelled!");
            return false;
        }

        if (removeFromConfig) {
            if (!removeWorldFromConfig(name)) {
                return false;
            }
        } else {
            if (!this.unloadWorld(name)) {
                return false;
            }
        }

        try {
            File worldFile = world.getWorldFolder();
            Logging.finer("deleteWorld(): worldFile: " + worldFile.getAbsolutePath());
            if (deleteWorldFolder ? FileUtils.deleteFolder(worldFile) : FileUtils.deleteFolderContents(worldFile)) {
                Logging.info("World '%s' was DELETED.", name);
                return true;
            } else {
                Logging.severe("World '%s' was NOT deleted.", name);
                Logging.severe("Are you sure the folder %s exists?", name);
                Logging.severe("Please check your file permissions on '%s'", name);
                return false;
            }
        } catch (Throwable e) {
            Logging.severe("Hrm, something didn't go as planned. Here's an exception for ya.");
            Logging.severe("You can go politely explain your situation in #multiverse on esper.net");
            Logging.severe("But from here, it looks like your folder is oddly named.");
            Logging.severe("This world has been removed from Multiverse-Core so your best bet is to go delete the folder by hand. Sorry.");
            Logging.severe(e.getMessage());
            return false;
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean deleteWorld(String name, boolean removeFromConfig) {
        return this.deleteWorld(name, removeFromConfig, true);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean deleteWorld(String name) {
        return this.deleteWorld(name, true);
    }

    /**
     * Unload a world from Bukkit.
     *
     * @param name   Name of the world to unload
     * @param safely Perform this safely. Set to True to save world files before unloading.
     * @return True if the world was unloaded, false if not.
     */
    private boolean unloadWorldFromBukkit(String name, boolean safely) {
        this.removePlayersFromWorld(name);
        return this.plugin.getServer().unloadWorld(name, safely);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void removePlayersFromWorld(String name) {
        World w = this.plugin.getServer().getWorld(name);
        if (w != null) {
            World safeWorld = this.plugin.getServer().getWorlds().get(0);
            List<Player> ps = w.getPlayers();
            SafeTTeleporter teleporter = this.plugin.getSafeTTeleporter();
            for (Player p : ps) {
                // We're removing players forcefully from a world, they'd BETTER spawn safely.
                teleporter.safelyTeleport(null, p, safeWorld.getSpawnLocation(), true);
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Collection<MultiverseWorld> getMVWorlds() {
        return this.worlds.values();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public MultiverseWorld getMVWorld(String name) {
        return this.getMVWorld(name, true);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public MultiverseWorld getMVWorld(String name, boolean checkAliases) {
        if (name == null) {
            return null;
        }
        MultiverseWorld world = this.worlds.get(name);
        if (world != null) {
            return world;
        }
        return (checkAliases) ? this.getMVWorldByAlias(name) : null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public MultiverseWorld getMVWorld(World world) {
        if (world != null) {
            return this.getMVWorld(world.getName(), false);
        }
        return null;
    }

    /**
     * Returns a {@link MVWorld} if it exists, and null if it does not. This will search ONLY alias.
     *
     * @param alias The alias of the world to get.
     * @return A {@link MVWorld} or null.
     */
    private MultiverseWorld getMVWorldByAlias(String alias) {
        for (MultiverseWorld w : this.worlds.values()) {
            if (w.getAlias().equalsIgnoreCase(alias)) {
                return w;
            }
        }
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isMVWorld(final String name) {
        return this.isMVWorld(name, true);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isMVWorld(final String name, boolean checkAliases) {
        return this.worlds.containsKey(name) || (checkAliases && this.isMVWorldAlias(name));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isMVWorld(World world) {
        return world != null && this.isMVWorld(world.getName());
    }

    /**
     * This method ONLY checks the alias of each world.
     *
     * @param alias The alias of the world to check.
     * @return True if the world exists, false if not.
     */
    private boolean isMVWorldAlias(final String alias) {
        for (MultiverseWorld w : this.worlds.values()) {
            if (w.getAlias().equalsIgnoreCase(alias)) {
                return true;
            }
        }
        return false;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void loadDefaultWorlds() {
        this.ensureConfigIsPrepared();
        List<World> myWorlds = this.plugin.getServer().getWorlds();
        for (World w : myWorlds) {
            String name = w.getName();
            if (!worldsFromTheConfig.containsKey(name)) {
                String generator = null;
                if (this.defaultGens.containsKey(name)) {
                    generator = this.defaultGens.get(name);
                }
                this.addWorld(name, w.getEnvironment(), String.valueOf(w.getSeed()), w.getWorldType(), w.canGenerateStructures(), generator);
            }
        }
    }

    private void ensureConfigIsPrepared() {
        this.configWorlds.options().pathSeparator(SEPARATOR);
        if (this.configWorlds.getConfigurationSection("worlds") == null) {
            this.configWorlds.createSection("worlds");
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void loadWorlds(boolean forceLoad) {
        // Basic Counter to count how many Worlds we are loading.
        int count = 0;
        this.ensureConfigIsPrepared();
        this.ensureSecondNamespaceIsPrepared();

        // Force the worlds to be loaded, ie don't just load new worlds.
        if (forceLoad) {
            // Remove all world permissions.
            Permission allAccess = this.plugin.getServer().getPluginManager().getPermission("multiverse.access.*");
            Permission allExempt = this.plugin.getServer().getPluginManager().getPermission("multiverse.exempt.*");
            for (MultiverseWorld w : this.worlds.values()) {
                // Remove this world from the master list
                if (allAccess != null) {
                    allAccess.getChildren().remove(w.getAccessPermission().getName());
                }
                if (allExempt != null) {
                    allExempt.getChildren().remove(w.getAccessPermission().getName());
                }
                this.plugin.getServer().getPluginManager().removePermission(w.getAccessPermission().getName());
                this.plugin.getServer().getPluginManager().removePermission(w.getExemptPermission().getName());
                // Special namespace for gamemodes
                this.plugin.getServer().getPluginManager().removePermission("mv.bypass.gamemode." + w.getName());
                this.plugin.getServer().getPluginManager().removePermission("mv.bypass.fly." + w.getName());
            }
            // Recalc the all permission
            this.plugin.getServer().getPluginManager().recalculatePermissionDefaults(allAccess);
            this.plugin.getServer().getPluginManager().recalculatePermissionDefaults(allExempt);
            this.worlds.clear();
        }

        for (Map.Entry<String, WorldProperties> entry : worldsFromTheConfig.entrySet()) {
            if (worlds.containsKey(entry.getKey())) {
                continue;
            }
            if (!entry.getValue().getAutoLoad())
                continue;

            if (doLoad(entry.getKey()))
                count++;
        }

        // Simple Output to the Console to show how many Worlds were loaded.
        Logging.config("%s - World(s) loaded.", count);
        this.saveWorldsConfig();
    }

    private void ensureSecondNamespaceIsPrepared() {
        Permission specialGameMode = this.plugin.getServer().getPluginManager().getPermission("mv.bypass.gamemode.*");
        Permission specialFly = this.plugin.getServer().getPluginManager().getPermission("mv.bypass.fly.*");
        if (specialGameMode == null) {
            specialGameMode = new Permission("mv.bypass.gamemode.*", PermissionDefault.FALSE);
            this.plugin.getServer().getPluginManager().addPermission(specialGameMode);
        }
        if (specialFly == null) {
            specialFly = new Permission("mv.bypass.fly.*", PermissionDefault.FALSE);
            this.plugin.getServer().getPluginManager().addPermission(specialFly);
        }
    }

    /**
     * {@inheritDoc}
     * @deprecated This is deprecated!
     */
    @Override
    @Deprecated
    public PurgeWorlds getWorldPurger() {
        return new PurgeWorlds(plugin);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public WorldPurger getTheWorldPurger() {
        return worldPurger;
    }

    private static final char SEPARATOR = '\uF8FF';

    public boolean isKeepingSpawnInMemory(World world) {
        WorldProperties properties = worldsFromTheConfig.get(world.getName());
        return properties == null || properties.isKeepingSpawnInMemory();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public FileConfiguration loadWorldConfig(File file) {
        this.configWorlds = YamlConfiguration.loadConfiguration(file);
        this.ensureConfigIsPrepared();
        try {
            this.configWorlds.save(new File(this.plugin.getDataFolder(), "worlds.yml"));
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        // load world-objects
        Stack<String> worldKeys = new Stack<String>();
        worldKeys.addAll(this.configWorlds.getConfigurationSection("worlds").getKeys(false));
        Map<String, WorldProperties> newWorldsFromTheConfig = new HashMap<String, WorldProperties>();
        while (!worldKeys.isEmpty()) {
            String key = worldKeys.pop();
            String path = "worlds" + SEPARATOR + key;
            Object obj = this.configWorlds.get(path);
            if ((obj != null) && (obj instanceof WorldProperties)) {
                String worldName = key.replaceAll(String.valueOf(SEPARATOR), ".");
                WorldProperties props = (WorldProperties) obj;
                if (this.worldsFromTheConfig.containsKey(worldName)) {
                    // Object-Recycling :D
                    // TODO Why is is checking worldsFromTheConfig and then getting from worlds?  So confused... (DTM)
                    MVWorld mvWorld = (MVWorld) this.worlds.get(worldName);
                    if (mvWorld != null) {
                        mvWorld.copyValues((WorldProperties) obj);
                    }
                }
                newWorldsFromTheConfig.put(worldName, props);
            } else if (this.configWorlds.isConfigurationSection(path)) {
                ConfigurationSection section = this.configWorlds.getConfigurationSection(path);
                Set<String> subkeys = section.getKeys(false);
                for (String subkey : subkeys) {
                    worldKeys.push(key + SEPARATOR + subkey);
                }
            }
        }
        this.worldsFromTheConfig = newWorldsFromTheConfig;
        this.worlds.keySet().retainAll(this.worldsFromTheConfig.keySet());
        return this.configWorlds;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean saveWorldsConfig() {
        try {
            this.configWorlds.options().pathSeparator(SEPARATOR);
            this.configWorlds.set("worlds", null);
            for (Map.Entry<String, WorldProperties> entry : worldsFromTheConfig.entrySet()) {
                this.configWorlds.set("worlds" + SEPARATOR + entry.getKey(), entry.getValue());
            }
            this.configWorlds.save(new File(this.plugin.getDataFolder(), "worlds.yml"));
            return true;
        } catch (IOException e) {
            Logging.severe("Could not save worlds.yml. Please check your settings.");
            return false;
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public MultiverseWorld getSpawnWorld() {
        return this.getMVWorld(this.plugin.getServer().getWorlds().get(0));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<String> getUnloadedWorlds() {
        List<String> allNames = new ArrayList<String>(this.worldsFromTheConfig.keySet());
        allNames.removeAll(worlds.keySet());
        return allNames;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean regenWorld(String name, boolean useNewSeed, boolean randomSeed, String seed) {
        return regenWorld(name, useNewSeed, randomSeed, seed, false);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean regenWorld(String name, boolean useNewSeed, boolean randomSeed, String seed, boolean keepGameRules) {
        MultiverseWorld world = this.getMVWorld(name);
        if (world == null) {
            Logging.warning("Unable to regen a world that does not exist!");
            return false;
        }

        List<Player> ps = world.getCBWorld().getPlayers();

        // Apply new seed if needed.
        if (useNewSeed) {
            long theSeed;

            if (randomSeed) {
                theSeed = new Random().nextLong();
            } else {
                try {
                    theSeed = Long.parseLong(seed);
                } catch (NumberFormatException e) {
                    theSeed = seed.hashCode();
                }
            }

            world.setSeed(theSeed);
        }

        WorldType type = world.getWorldType();

        // Save current GameRules if needed.
        Map<GameRule<?>, Object> gameRuleMap = null;
        if (keepGameRules) {
            gameRuleMap = new HashMap<>(GameRule.values().length);
            World CBWorld = world.getCBWorld();
            for (GameRule<?> gameRule : GameRule.values()) {
                // Only save if not default value.
                Object value = CBWorld.getGameRuleValue(gameRule);
                if (value != CBWorld.getGameRuleDefault(gameRule)) {
                    gameRuleMap.put(gameRule, value);
                }
            }
        }

        // Do the regen.
        if (!this.deleteWorld(name, false, false)) {
            Logging.severe("Unable to regen world as world cannot be deleted.");
            return false;
        }
        if (!this.doLoad(name, true, type)) {
            Logging.severe("Unable to regen world as world cannot be loaded.");
            return false;
        }

        // Get new MultiverseWorld reference.
        world = this.getMVWorld(name);

        // Load back GameRules if needed.
        if (keepGameRules) {
            Logging.fine("Restoring previous world's GameRules...");
            World CBWorld = world.getCBWorld();
            for (Map.Entry<GameRule<?>, Object> gameRuleEntry : gameRuleMap.entrySet()) {
                if (!setGameRuleValue(CBWorld, gameRuleEntry.getKey(), gameRuleEntry.getValue())) {
                    Logging.warning("Unable to set GameRule '%s' to '%s' on regen world.",
                            gameRuleEntry.getKey().getName(), gameRuleEntry.getValue());
                }
            }
        }

        // Send all players that were in the old world, BACK to it!
        SafeTTeleporter teleporter = this.plugin.getSafeTTeleporter();
        Location newSpawn = world.getSpawnLocation();
        for (Player p : ps) {
            teleporter.safelyTeleport(null, p, newSpawn, true);
        }

        return true;
    }

    private <T> boolean setGameRuleValue(World world, GameRule<T> gameRule, Object value) {
        try {
            return world.setGameRule(gameRule, (T) value);
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Gets the {@link FileConfiguration} that this {@link WorldManager} is using.
     * @return The {@link FileConfiguration} that this {@link WorldManager} is using.
     */
    public FileConfiguration getConfigWorlds() {
        return this.configWorlds;
    }

    /**
     * {@inheritDoc}
     */
	@Override
	public boolean hasUnloadedWorld(String name, boolean includeLoaded) {
		if (getMVWorld(name) != null) {
			return includeLoaded;
		}
		for (Map.Entry<String, WorldProperties> entry : this.worldsFromTheConfig.entrySet()) {
			if (name.equals(entry.getKey()) || name.equals(entry.getValue().getAlias())) {
				return true;
			}
		}
		return false;
	}

    /**
     * {@inheritDoc}
     */
    @Override
	public Collection<String> getPotentialWorlds() {
        File worldContainer = this.plugin.getServer().getWorldContainer();
        if (worldContainer == null) {
            return Collections.emptyList();
        }
        return Arrays.stream(worldContainer.listFiles())
                .filter(File::isDirectory)
                .filter(folder -> !this.isMVWorld(folder.getName(), false))
                .filter(WorldNameChecker::isValidWorldFolder)
                .map(File::getName)
                .collect(Collectors.toList());
    }
}
