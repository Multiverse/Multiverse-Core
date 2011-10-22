/******************************************************************************
 * Multiverse 2 Copyright (c) the Multiverse Team 2011.                       *
 * Multiverse 2 is licensed under the BSD License.                            *
 * For more information please check the README.md file included              *
 * with this project.                                                         *
 ******************************************************************************/

package com.onarandombox.MultiverseCore;

import com.onarandombox.MultiverseCore.api.MultiverseWorld;
import com.onarandombox.MultiverseCore.configuration.ConfigPropertyFactory;
import com.onarandombox.MultiverseCore.configuration.MVConfigProperty;
import com.onarandombox.MultiverseCore.enums.EnglishChatColor;
import com.onarandombox.MultiverseCore.exceptions.PropertyDoesNotExistException;
import com.onarandombox.MultiverseCore.utils.BlockSafety;
import com.onarandombox.MultiverseCore.utils.LocationManipulation;
import com.onarandombox.MultiverseCore.utils.SafeTTeleporter;
import org.bukkit.*;
import org.bukkit.World.Environment;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.permissions.Permission;
import org.bukkit.permissions.PermissionDefault;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;


public class MVWorld implements MultiverseWorld {

    private MultiverseCore plugin; // Hold the Plugin Instance.
    private FileConfiguration config; // Hold the Configuration File.
    private ConfigurationSection worldSection; // Holds the section of the config file for this world.

    private World world; // The World Instance.
    private Environment environment; // Hold the Environment type EG Environment.NETHER / Environment.NORMAL
    private Long seed;

    private String name; // The Worlds Name, EG its folder name.
    private String alias = ""; // Short Alias for the World, this will be used in Chat Prefixes.
    private ChatColor aliasColor; // Color for this world

    private boolean allowAnimals; // Does this World allow Animals to Spawn?
    private boolean allowMonsters; // Does this World allow Monsters to Spawn?

    private boolean keepSpawnInMemory; // Does the World have the spawn loaded all the time?

    private boolean pvp; // Does this World allow PVP?
    private boolean fakePVP; // Should this world have fakePVP on? (used for PVP zones)

    private GameMode gameMode = GameMode.SURVIVAL;

    private String respawnWorld; // Contains the name of the World to respawn the player to


    private Map<String, List<String>> masterList;
    private Map<String, MVConfigProperty> propertyList;

    private double scaling; // How stretched/compressed distances are
    private double price; // How much does it cost to enter this world
    private int currency = -1; // What is the currency
    private boolean hunger = true;
    private Permission permission;
    private Permission exempt;

    private boolean canSave = false; // Prevents all the setters from constantly saving to the config when being called from the constructor.
    private boolean allowWeather;
    private Location spawnLocation;
    //private boolean isHidden = false;
    private boolean autoheal = true;
    private boolean adjustSpawn = true;

    public MVWorld(World world, FileConfiguration config, MultiverseCore instance, Long seed, String generatorString) {
        this.config = config;
        this.plugin = instance;

        // Set local values that CANNOT be changed by user
        this.world = world;
        this.name = world.getName();
        this.seed = seed;
        this.environment = world.getEnvironment();

        // Initialize our lists
        this.initLists();
        worldSection = config.getConfigurationSection("worlds." + this.name);
        if (worldSection == null) {
            config.createSection("worlds." + this.name);
            worldSection = config.getConfigurationSection("worlds." + this.name);
        }
        // Write these files to the config (once it's saved)
        if (generatorString != null) {
            worldSection.set("generator", generatorString);
        }
        if (seed != null) {
            worldSection.set("seed", this.seed);
        }
        worldSection.set("environment", this.environment.toString());

        // Start NEW config awesomeness.
        ConfigPropertyFactory fac = new ConfigPropertyFactory(this.worldSection);
        this.propertyList = new HashMap<String, MVConfigProperty>();
        this.propertyList.put("hidden", fac.getNewProperty("hidden", false));
        this.propertyList.put("alias", fac.getNewProperty("alias", "", "alias.name"));
        this.propertyList.put("color", fac.getNewProperty("color", EnglishChatColor.WHITE, "alias.color"));
        this.propertyList.put("pvp", fac.getNewProperty("pvp", true));
        this.propertyList.put("fakepvp", fac.getNewProperty("fakepvp", false));
        this.propertyList.put("scale", fac.getNewProperty("scale", this.getDefaultScale(this.environment)));
        this.propertyList.put("respawn", fac.getNewProperty("respawn", "", "respawnworld"));
        this.propertyList.put("weather", fac.getNewProperty("weather", true, "allowweather"));
        this.propertyList.put("difficulty", fac.getNewProperty("difficulty", Difficulty.EASY));
        this.propertyList.put("animals", fac.getNewProperty("animals", true, "animals.spawn"));
        this.propertyList.put("monsters", fac.getNewProperty("monsters", true, "monsters.spawn"));
        this.propertyList.put("currency", fac.getNewProperty("currency", -1, "entryfee.currency"));
        this.propertyList.put("price", fac.getNewProperty("price", 0.0, "entryfee.price"));
        this.propertyList.put("hunger", fac.getNewProperty("hunger", true));
        this.propertyList.put("autoheal", fac.getNewProperty("autoheal", true));
        this.propertyList.put("adjustspawn", fac.getNewProperty("adjustspawn", true));
        this.propertyList.put("gamemode", fac.getNewProperty("gamemode", GameMode.SURVIVAL));
        this.propertyList.put("memory", fac.getNewProperty("keepspawninmemory", true, "keepspawninmemory"));
        this.propertyList.put("spawn", fac.getNewProperty("spawn", new Location(this.getCBWorld(), 0,0,0)));

        // Set aliases
        this.propertyList.put("curr", this.propertyList.get("currency"));
        this.propertyList.put("scaling", this.propertyList.get("scale"));
        this.propertyList.put("aliascolor", this.propertyList.get("color"));
        this.propertyList.put("heal", this.propertyList.get("autoheal"));
        this.propertyList.put("storm", this.propertyList.get("weather"));
        this.propertyList.put("spawnmemory", this.propertyList.get("memory"));
        this.propertyList.put("mode", this.propertyList.get("gamemode"));
        this.propertyList.put("diff", this.propertyList.get("difficulty"));

        // Things I haven't converted yet.
        this.getMobExceptions();
        this.getWorldBlacklist().addAll(worldSection.getList("worldblacklist", new ArrayList<String>()));

        // This method translates a MV1 style spawn stored in the config.
        this.translateTempSpawn(worldSection);

        // This method takes a look at the given spawn and ensures its safe.
        this.readSpawnFromConfig(this.getCBWorld());

        // Enable and do the save.
        this.canSave = true;
        this.saveConfig();

        this.permission = new Permission("multiverse.access." + this.getName(), "Allows access to " + this.getName(), PermissionDefault.OP);
        this.exempt = new Permission("multiverse.exempt." + this.getName(), "A player who has this does not pay to enter this world, or use any MV portals in it " + this.getName(), PermissionDefault.OP);
        try {
            this.plugin.getServer().getPluginManager().addPermission(this.permission);
            this.plugin.getServer().getPluginManager().addPermission(this.exempt);
            addToUpperLists(this.permission);
        } catch (IllegalArgumentException e) {
            this.plugin.log(Level.FINER, "Permissions nodes were already added for " + this.name);
        }
    }

    public boolean setWorldProperty(String name, String value) {
        MVConfigProperty property = this.propertyList.get(name);
        return property.parseValue(value);
    }

    public String getWorldProperty(String name) {
        MVConfigProperty property = this.propertyList.get(name);
        return property.toString();
    }

    private double getDefaultScale(Environment environment) {
        if (environment == Environment.NETHER) {
            return 8.0;
        }
        return 1.0;
    }

    public void setEnableWeather(boolean weather) {
        this.allowWeather = weather;
        // Disable any current weather
        if (!weather) {
            this.getCBWorld().setStorm(false);
            this.getCBWorld().setThundering(false);
        }
        this.worldSection.set("allowweather", weather);
        saveConfig();
    }

    private void addToUpperLists(Permission permission) {
        Permission all = this.plugin.getServer().getPluginManager().getPermission("multiverse.*");
        Permission allWorlds = this.plugin.getServer().getPluginManager().getPermission("multiverse.access.*");
        Permission allExemption = this.plugin.getServer().getPluginManager().getPermission("multiverse.exempt.*");

        if (allWorlds == null) {
            allWorlds = new Permission("multiverse.access.*");
            this.plugin.getServer().getPluginManager().addPermission(allWorlds);
        }
        allWorlds.getChildren().put(permission.getName(), true);
        if (allExemption == null) {
            allExemption = new Permission("multiverse.exempt.*");
            this.plugin.getServer().getPluginManager().addPermission(allExemption);
        }
        allExemption.getChildren().put(this.exempt.getName(), true);
        if (all == null) {
            all = new Permission("multiverse.*");
            this.plugin.getServer().getPluginManager().addPermission(all);
        }
        all.getChildren().put("multiverse.access.*", true);
        all.getChildren().put("multiverse.exempt.*", true);

        this.plugin.getServer().getPluginManager().recalculatePermissionDefaults(all);
        this.plugin.getServer().getPluginManager().recalculatePermissionDefaults(allWorlds);
    }

    private void translateTempSpawn(ConfigurationSection section) {
        String tempspawn = section.getString("tempspawn", "");
        if (tempspawn.length() > 0) {
            String[] coordsString = tempspawn.split(":");
            if (coordsString.length >= 3) {
                int[] coords = new int[3];
                try {
                    for (int i = 0; i < 3; i++) {
                        coords[i] = Integer.parseInt(coordsString[i]);
                    }
                    this.setSpawnLocation(new Location(this.getCBWorld(), coords[0], coords[1], coords[2]));
                } catch (NumberFormatException e) {
                    this.plugin.log(Level.WARNING, "A MV1 spawn value was found, but it could not be migrated. Format Error. Sorry.");
                }
            } else {
                this.plugin.log(Level.WARNING, "A MV1 spawn value was found, but it could not be migrated. Format Error. Sorry.");
            }

            this.worldSection.set("tempspawn", null);
        }
    }

    public String getColoredWorldString() {
        if (this.getColor() == null) {
            return this.getAlias() + ChatColor.WHITE;
        }
        return this.getColor() + this.getAlias() + ChatColor.WHITE;
    }

    private void getMobExceptions() {
        List<String> temp;
        temp = this.worldSection.getList("animals.exceptions", new ArrayList<String>());
        // Add Animals to the exclusion list

        for (String s : temp) {
            this.masterList.get("animals").add(s.toUpperCase());
        }
        temp = this.worldSection.getList("monsters.exceptions", new ArrayList<String>());
        // Add Monsters to the exclusion list
        for (String s : temp) {
            this.masterList.get("monsters").add(s.toUpperCase());
        }
    }

    public World getCBWorld() {
        return this.world;
    }

    private void initLists() {
        this.masterList = new HashMap<String, List<String>>();
        this.masterList.put("worldblacklist", new ArrayList<String>());
        this.masterList.put("animals", new ArrayList<String>());
        this.masterList.put("monsters", new ArrayList<String>());
    }

    @Override
    public boolean clearVariable(String property) {
        if (this.masterList.keySet().contains(property)) {
            this.masterList.get(property).clear();
        } else {
            return false;
        }
        this.worldSection.set(property.toLowerCase(), new ArrayList<String>());
        this.saveConfig();
        return true;
    }

    @Override
    public boolean addToVariable(String property, String value) {
        if (this.masterList.keySet().contains(property)) {

            if (property.equalsIgnoreCase("animals") || property.equalsIgnoreCase("monsters")) {
                this.masterList.get(property).add(value.toUpperCase());
                this.worldSection.set(property.toLowerCase() + ".exceptions", this.masterList.get(property));
                this.syncMobs();
            } else {
                this.masterList.get(property).add(value);
                this.worldSection.set(property.toLowerCase(), this.masterList.get(property));
            }
            saveConfig();
            return true;
        }
        return false;
    }

    @Override
    public boolean removeFromVariable(String property, String value) {
        if (this.masterList.keySet().contains(property)) {

            if (property.equalsIgnoreCase("animals") || property.equalsIgnoreCase("monsters")) {
                this.masterList.get(property).remove(value.toUpperCase());
                this.worldSection.set("" + property.toLowerCase() + ".exceptions", this.masterList.get(property));
                this.syncMobs();
            } else {
                this.masterList.get(property).remove(value);
                this.worldSection.set("" + property.toLowerCase(), this.masterList.get(property));
            }
            saveConfig();
            return true;
        }
        return false;
    }

    /** Deprecated, use {@link #addToVariable(String, String)} now. */
    @Deprecated
    public boolean addToList(String list, String value) {
        return this.addToVariable(list, value);
    }

    // Deprecated, use {@link #removeFromVariable(String, String)} now.
    @Deprecated
    public boolean removeFromList(String list, String value) {
        return this.removeFromVariable(list, value);
    }

    private void syncMobs() {

        if (this.getAnimalList().isEmpty()) {
            this.world.setSpawnFlags(this.world.getAllowMonsters(), this.allowAnimals);
        } else {
            this.world.setSpawnFlags(this.world.getAllowMonsters(), true);
        }
        if (this.getMonsterList().isEmpty()) {
            this.world.setSpawnFlags(this.allowMonsters, this.world.getAllowAnimals());
        } else {
            this.world.setSpawnFlags(true, this.world.getAllowAnimals());
        }
        this.plugin.getMVWorldManager().getWorldPurger().purgeWorld(null, this);
    }

    @Override
    public void setKeepSpawnInMemory(boolean value) {
        this.world.setKeepSpawnInMemory(value);
        this.keepSpawnInMemory = value;
        this.worldSection.set("keepspawninmemory", value);
        saveConfig();
    }

    // TODO: Provide better feedback
    @Override
    public boolean setVariable(String name, String value) throws PropertyDoesNotExistException {
        if (this.propertyList.containsKey(name)) {
            if (this.propertyList.get(name).parseValue(value)) {
                this.saveConfig();
                return true;
            }
            return false;
        }
        throw new PropertyDoesNotExistException(name);
    }

    public String getVariable(String name) throws PropertyDoesNotExistException {
        if (this.propertyList.containsKey(name)) {
            return this.propertyList.get(name).toString();
        }
        throw new PropertyDoesNotExistException(name);
    }

    @Override
    public Environment getEnvironment() {
        return this.environment;
    }

    @Override
    public void setEnvironment(Environment environment) {
        this.environment = environment;
    }

    @Override
    public Long getSeed() {
        return this.seed;
    }

    @Override
    public void setSeed(Long seed) {
        this.seed = seed;
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public String getAlias() {
        if (this.alias == null || this.alias.length() == 0) {
            return this.name;
        }
        return this.alias;

    }

    @Override
    public void setAlias(String alias) {
        this.alias = alias;
        this.worldSection.set("alias.name", alias);
        saveConfig();
    }

    @Override
    public boolean canAnimalsSpawn() {
        return this.allowAnimals;
    }

    @Override
    public void setAllowAnimalSpawn(boolean animals) {
        this.allowAnimals = animals;
        // If animals are a boolean, then we can turn them on or off on the server
        // If there are ANY exceptions, there will be something spawning, so turn them on
        this.worldSection.set("animals.spawn", animals);
        saveConfig();
        this.syncMobs();
    }

    @Override
    public List<String> getAnimalList() {
        return this.masterList.get("animals");
    }

    @Override
    public boolean canMonstersSpawn() {
        return this.allowMonsters;
    }

    @Override
    public void setAllowMonsterSpawn(boolean monsters) {
        this.allowMonsters = monsters;
        // If monsters are a boolean, then we can turn them on or off on the server
        // If there are ANY exceptions, there will be something spawning, so turn them on
        this.worldSection.set("monsters.spawn", monsters);
        saveConfig();
        this.syncMobs();
    }

    @Override
    public List<String> getMonsterList() {
        return this.masterList.get("monsters");
    }

    @Override
    public boolean isPVPEnabled() {
        return this.pvp;
    }

    @Override
    public void setPVPMode(boolean pvp) {
        if (this.fakePVP) {
            this.world.setPVP(true);
        } else {
            this.world.setPVP(pvp);
        }
        this.pvp = pvp;
        this.worldSection.set("pvp", pvp);
        saveConfig();
    }

    @Override
    public boolean isHidden() {
        return (Boolean) this.propertyList.get("hidden").getValue();
    }

    @Override
    public void setHidden(boolean hidden) {
        this.propertyList.get("hidden").parseValue(hidden + "");
        saveConfig();
    }

    public void setFakePVPMode(Boolean fakePVPMode) {
        this.fakePVP = fakePVPMode;
        this.worldSection.set("fakepvp", this.fakePVP);
        // Now that we've set PVP mode, make sure to go through the normal setting too!
        // This method will perform the save for us to eliminate one write.
        this.setPVPMode(this.pvp);
    }

    public List<String> getWorldBlacklist() {
        return this.masterList.get("worldblacklist");
    }

    @Override
    public double getScaling() {
        return this.scaling;
    }

    @Override
    public boolean setScaling(double scaling) {
        boolean success = true;
        if (scaling <= 0) {
            // Disallow negative or 0 scalings.
            scaling = 1.0;
            this.plugin.log(Level.WARNING, "Someone tried to set a scale <= 0, defaulting to 1.");
            success = false;
        }
        this.scaling = scaling;
        this.worldSection.set("scale", scaling);
        saveConfig();
        return success;
    }

    @Override
    public boolean setColor(String aliasColor) {
        EnglishChatColor color = EnglishChatColor.fromString(aliasColor);
        if (color == null) {
            return false;
        }
        this.aliasColor = color.getColor();
        this.worldSection.set("alias.color", color.getText());
        saveConfig();
        return true;
    }

    public boolean isValidAliasColor(String aliasColor) {
        return (EnglishChatColor.fromString(aliasColor) != null);
    }

    @Override
    public ChatColor getColor() {
        return this.aliasColor;
    }

    public boolean clearList(String property) {
        if (this.masterList.containsKey(property)) {
            this.masterList.get(property).clear();
            this.worldSection.set(property.toLowerCase(), this.masterList.get(property));
            this.syncMobs();
            saveConfig();
            return true;
        }
        return false;
    }

    public boolean getFakePVP() {
        return this.fakePVP;
    }

    @Override
    public World getRespawnToWorld() {
        if (this.respawnWorld == null) {
            return null;
        }
        return (this.plugin.getServer().getWorld(this.respawnWorld));
    }

    @Override
    public boolean setRespawnToWorld(String respawnToWorld) {
        if (this.plugin.getServer().getWorld(respawnToWorld) != null) {
            this.respawnWorld = respawnToWorld;
            this.worldSection.set("respawnworld", respawnToWorld);
            saveConfig();
            return true;
        }
        return false;
    }

    @Override
    public Permission getAccessPermission() {
        return this.permission;
    }

    @Override
    public int getCurrency() {
        return this.currency;
    }

    @Override
    public double getPrice() {
        return this.price;
    }

    @Override
    public void setCurrency(int currency) {
        this.currency = currency;
        this.worldSection.set("entryfee.currency", currency);
        saveConfig();
    }

    @Override
    public void setPrice(double price) {
        this.price = price;
        this.worldSection.set("entryfee.amount", price);
        saveConfig();
    }

    /** This method really isn't needed */
    @Deprecated
    public boolean isExempt(Player p) {
        return (this.plugin.getMVPerms().hasPermission(p, this.exempt.getName(), true));
    }

    @Override
    public Permission getExemptPermission() {
        return this.exempt;
    }

    private void saveConfig() {
        if (this.canSave) {
            try {
                this.config.save(new File(this.plugin.getDataFolder(), "worlds.yml"));
            } catch (IOException e) {
                this.plugin.log(Level.SEVERE, "Could not save worlds.yml. Please check your filesystem permissions.");
            }
        }
    }

    @Override
    public boolean setGameMode(String gameMode) {
        GameMode mode;
        try {
            mode = GameMode.valueOf(gameMode.toUpperCase());
        } catch (Exception e) {
            try {
                int modeInt = Integer.parseInt(gameMode);
                mode = GameMode.getByValue(modeInt);

            } catch (Exception e2) {
                return false;
            }
        }
        if (mode == null) {
            return false;
        }
        this.setGameMode(mode);
        this.worldSection.set("gamemode", mode.toString());
        saveConfig();
        return true;

    }

    /**
     * FernFerret messed up and now config values could be in either string or Int
     *
     * @param mode The gamemode as an object.
     *
     * @return True if the mode was set, false if not.
     */
    private boolean setGameMode(Object mode) {
        if (mode instanceof Integer) {
            return this.setGameMode(GameMode.getByValue((Integer) mode));
        }
        try {
            return this.setGameMode((String) mode);
        } catch (ClassCastException e) {
            return false;
        }
    }

    private boolean setGameMode(GameMode mode) {

        this.gameMode = mode;
        this.worldSection.set("gamemode", this.gameMode.toString());
        saveConfig();

        if (MultiverseCore.EnforceGameModes) {
            for (Player p : this.plugin.getServer().getWorld(this.getName()).getPlayers()) {
                this.plugin.log(Level.FINER, "Setting " + p.getName() + "'s GameMode to " + this.gameMode.toString());
                this.plugin.getPlayerListener().handleGameMode(p, this);
            }
        }
        return true;
    }

    public GameMode getGameMode() {
        return this.gameMode;
    }

    @Override
    public boolean isWeatherEnabled() {
        return this.allowWeather;
    }

    @Override
    public boolean isKeepingSpawnInMemory() {
        return this.keepSpawnInMemory;
    }

    @Override
    public void setHunger(boolean hunger) {
        this.hunger = hunger;
        this.worldSection.set("hunger", this.hunger);
        saveConfig();
    }

    @Override
    public boolean getHunger() {
        return this.hunger;
    }

    @Override
    public void setSpawnLocation(Location l) {
        this.getCBWorld().setSpawnLocation(l.getBlockX(), l.getBlockY(), l.getBlockZ());
        this.worldSection.set("spawn.x", l.getX());
        this.worldSection.set("spawn.y", l.getY());
        this.worldSection.set("spawn.z", l.getZ());
        this.worldSection.set("spawn.pitch", l.getPitch());
        this.worldSection.set("spawn.yaw", l.getYaw());
        this.getCBWorld().setSpawnLocation(l.getBlockX(), l.getBlockY(), l.getBlockZ());
        this.spawnLocation = l.clone();
        saveConfig();
    }

    private Location readSpawnFromConfig(World w) {
        Location spawnLocation = w.getSpawnLocation();
        double x = worldSection.getDouble("spawn.x", spawnLocation.getX());
        double y = worldSection.getDouble("spawn.y", spawnLocation.getY());
        double z = worldSection.getDouble("spawn.z", spawnLocation.getZ());
        float pitch = (float) worldSection.getDouble("spawn.pitch", spawnLocation.getPitch());
        float yaw = (float) worldSection.getDouble("spawn.yaw", spawnLocation.getYaw());
        this.plugin.log(Level.FINE, "Read spawn from config as: " + x + ", " + y + ", " + z);

        this.setSpawnLocation(new Location(w, x, y, z, yaw, pitch));
        this.plugin.log(Level.FINEST, "Spawn for '" + this.getName() + "' Located at: " + LocationManipulation.locationToString(this.getSpawnLocation()));
        SafeTTeleporter teleporter = this.plugin.getTeleporter();
        BlockSafety bs = new BlockSafety();
        if (!bs.playerCanSpawnHereSafely(this.spawnLocation)) {
            if (!this.adjustSpawn) {
                this.plugin.log(Level.WARNING, "Spawn location from world.dat file was unsafe!!");
                this.plugin.log(Level.WARNING, "NOT adjusting spawn for '" + this.getAlias() + "' because you told me not to.");
                this.plugin.log(Level.WARNING, "To turn on spawn adjustment for this world simply type:");
                this.plugin.log(Level.WARNING, "/mvm set adjustspawn true " + this.getAlias());
                return this.spawnLocation;
            }
            this.plugin.log(Level.WARNING, "Spawn location from world.dat file was unsafe. Adjusting...");
            Location newSpawn = teleporter.getSafeLocation(this.spawnLocation, 128, 128);
            // I think we could also do this, as I think this is what Notch does.
            // Not sure how it will work in the nether...
            //Location newSpawn = this.spawnLocation.getWorld().getHighestBlockAt(this.spawnLocation).getLocation();
            if (newSpawn != null) {
                this.plugin.log(Level.INFO, "New Spawn for '" + this.getName() + "' is Located at: " + LocationManipulation.locationToString(newSpawn));
                this.setSpawnLocation(newSpawn);
            } else {
                this.plugin.log(Level.SEVERE, "New safe spawn NOT found!!!");
            }
        }
        return this.spawnLocation;
    }

    @Override
    public Location getSpawnLocation() {
        return this.spawnLocation;
    }

    @Override
    public Difficulty getDifficulty() {
        return this.getCBWorld().getDifficulty();
    }

    /**
     * FernFerret messed up and now config values could be in either string or Int
     *
     * @param mode The gamemode as an object.
     *
     * @return True if the mode was set, false if not.
     */
    private boolean setDifficulty(Object mode) {
        if (mode instanceof Integer) {
            return this.setDifficulty(Difficulty.getByValue((Integer) mode));
        }
        try {
            return this.setDifficulty((String) mode);
        } catch (ClassCastException e) {
            return false;
        }
    }

    @Override
    public boolean setDifficulty(String difficulty) {
        Difficulty worlddiff;
        try {
            worlddiff = Difficulty.valueOf(difficulty.toUpperCase());
        } catch (Exception e) {
            try {
                int diff = Integer.parseInt(difficulty);
                worlddiff = Difficulty.getByValue(diff);

            } catch (Exception e2) {
                return false;
            }
        }
        if (worlddiff == null) {
            return false;
        }
        this.setDifficulty(worlddiff);
        saveConfig();
        return true;
    }

    private boolean setDifficulty(Difficulty diff) {
        this.getCBWorld().setDifficulty(diff);
        this.worldSection.set("difficulty", diff.toString());
        saveConfig();
        return true;
    }

    @Override
    public boolean getAutoHeal() {
        return this.autoheal;
    }

    @Override
    public void setAutoHeal(boolean heal) {
        this.autoheal = heal;
        this.worldSection.set("autoheal", this.autoheal);
        saveConfig();
    }

    @Override
    public void setAdjustSpawn(boolean adjust) {
        this.adjustSpawn = adjust;
        this.worldSection.set("adjustspawn", this.adjustSpawn);
        saveConfig();
    }

    @Override
    public boolean getAdjustSpawn() {
        return this.adjustSpawn;
    }
}
