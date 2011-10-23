/******************************************************************************
 * Multiverse 2 Copyright (c) the Multiverse Team 2011.                       *
 * Multiverse 2 is licensed under the BSD License.                            *
 * For more information please check the README.md file included              *
 * with this project.                                                         *
 ******************************************************************************/

package com.onarandombox.MultiverseCore;

import com.onarandombox.MultiverseCore.api.MultiverseWorld;
import com.onarandombox.MultiverseCore.configuration.*;
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
    private Long seed; // The world seed
    private String name; // The Worlds Name, EG its folder name.

    private Map<String, List<String>> masterList;
    private Map<String, MVConfigProperty> propertyList;

    private Permission permission;
    private Permission exempt;

    private boolean canSave = false; // Prevents all the setters from constantly saving to the config when being called from the constructor.

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
        // The format of these are either:
        // getNewProperty(name, defaultValue, helpText)
        // or
        // getNewProperty(name, defaultValue, yamlConfigNode, helpText)
        //
        // If the first type is used, name is used as the yamlConfigNode
        this.propertyList.put("hidden", fac.getNewProperty("hidden", false, "Sorry, 'hidden' must either be:" + ChatColor.GREEN + " true " + ChatColor.WHITE + "or" + ChatColor.RED + " false" + ChatColor.WHITE + "."));
        this.propertyList.put("alias", fac.getNewProperty("alias", "", "alias.name"));
        this.propertyList.put("color", fac.getNewProperty("color", EnglishChatColor.WHITE, "alias.color", "Sorry, 'color' must either one of: " + EnglishChatColor.getAllColors()));
        this.propertyList.put("pvp", fac.getNewProperty("pvp", true, "Sorry, 'hidden' must either be:" + ChatColor.GREEN + " true " + ChatColor.WHITE + "or" + ChatColor.RED + " false" + ChatColor.WHITE + "."));
        this.propertyList.put("scale", fac.getNewProperty("scale", this.getDefaultScale(this.environment), "There is no help available for this variable. Go bug Rigby90 about it."));
        this.propertyList.put("respawn", fac.getNewProperty("respawn", "", "respawnworld", "You must set this to the " + ChatColor.GOLD + " NAME" + ChatColor.RED + " not alias of a world."));
        this.propertyList.put("weather", fac.getNewProperty("weather", true, "allowweather", "Sorry, 'hidden' must either be:" + ChatColor.GREEN + " true " + ChatColor.WHITE + "or" + ChatColor.RED + " false" + ChatColor.WHITE + "."));
        this.propertyList.put("difficulty", fac.getNewProperty("difficulty", Difficulty.EASY, "Difficulty must be set as one of the following: " + ChatColor.GOLD));
        this.propertyList.put("animals", fac.getNewProperty("animals", true, "animals.spawn", "Sorry, 'animals' must either be:" + ChatColor.GREEN + " true " + ChatColor.WHITE + "or" + ChatColor.RED + " false" + ChatColor.WHITE + ". (see "));
        this.propertyList.put("monsters", fac.getNewProperty("monsters", true, "monsters.spawn", "Sorry, 'monsters' must either be:" + ChatColor.GREEN + " true " + ChatColor.WHITE + "or" + ChatColor.RED + " false" + ChatColor.WHITE + "."));
        this.propertyList.put("currency", fac.getNewProperty("currency", -1, "entryfee.currency", "Currency must be an integer between -1 and the highest Minecraft item ID."));
        this.propertyList.put("price", fac.getNewProperty("price", 0.0, "entryfee.price", "Price must be a double formatted number like: 1.3"));
        this.propertyList.put("hunger", fac.getNewProperty("hunger", true, "Sorry, 'hidden' must either be:" + ChatColor.GREEN + " true " + ChatColor.WHITE + "or" + ChatColor.RED + " false" + ChatColor.WHITE + "."));
        this.propertyList.put("autoheal", fac.getNewProperty("autoheal", true, "Sorry, 'hidden' must either be:" + ChatColor.GREEN + " true " + ChatColor.WHITE + "or" + ChatColor.RED + " false" + ChatColor.WHITE + "."));
        this.propertyList.put("adjustspawn", fac.getNewProperty("adjustspawn", true, "Sorry, 'hidden' must either be:" + ChatColor.GREEN + " true " + ChatColor.WHITE + "or" + ChatColor.RED + " false" + ChatColor.WHITE + "."));
        this.propertyList.put("gamemode", fac.getNewProperty("gamemode", GameMode.SURVIVAL, "There is no help available for this variable. Go bug Rigby90 about it."));
        this.propertyList.put("memory", fac.getNewProperty("keepspawninmemory", true, "keepspawninmemory", "Sorry, 'hidden' must either be:" + ChatColor.GREEN + " true " + ChatColor.WHITE + "or" + ChatColor.RED + " false" + ChatColor.WHITE + "."));
        this.propertyList.put("spawn", fac.getNewProperty("spawn", this.world.getSpawnLocation(), "There is no help available for this variable. Go bug Rigby90 about it."));
        ((LocationConfigProperty) this.propertyList.get("spawn")).setValue(this.readSpawnFromConfig(this.getCBWorld()));

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

    public void changeActiveEffects() {
        // Disable any current weather
        if (!(Boolean) this.propertyList.get("weather").getValue()) {
            this.getCBWorld().setStorm(false);
            this.getCBWorld().setThundering(false);
        }

        // Set the spawn location
        Location spawnLocation = ((LocationConfigProperty) this.propertyList.get("spawn")).getValue();
        this.getCBWorld().setSpawnLocation(spawnLocation.getBlockX(), spawnLocation.getBlockY(), spawnLocation.getBlockZ());

        // Syncronize all Mob settings
        this.syncMobs();

        // Ensure the memory setting is correct
        this.world.setKeepSpawnInMemory(((BooleanConfigProperty) this.propertyList.get("memory")).getValue());

        // Set the PVP mode
        this.world.setPVP(((BooleanConfigProperty) this.propertyList.get("pvp")).getValue());

        // Ensure the scale is above 0
        if (((DoubleConfigProperty) this.propertyList.get("scale")).getValue() <= 0) {
            // Disallow negative or 0 scalings.
            ((DoubleConfigProperty) this.propertyList.get("scale")).setValue(1.0);
            this.plugin.log(Level.WARNING, "Someone tried to set a scale <= 0, defaulting to 1.");
        }

        // Set the gamemode
        // TODO: Move this to a per world gamemode
        if (MultiverseCore.EnforceGameModes) {
            for (Player p : this.plugin.getServer().getWorld(this.getName()).getPlayers()) {
                this.plugin.log(Level.FINER, "Setting " + p.getName() + "'s GameMode to " + this.propertyList.get("mode").getValue().toString());
                this.plugin.getPlayerListener().handleGameMode(p, this);
            }
        }

        // Set the difficulty
        this.getCBWorld().setDifficulty(((DifficultyConfigProperty) this.propertyList.get("diff")).getValue());
    }

    private double getDefaultScale(Environment environment) {
        if (environment == Environment.NETHER) {
            return 8.0;
        }
        return 1.0;
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

    public String getColoredWorldString() {
        EnglishChatColor worldColor = ((ColorConfigProperty) this.propertyList.get("color")).getValue();
        String alias = ((StringConfigProperty) this.propertyList.get("alias")).getValue();
        if (worldColor.getColor() == null) {
            return alias + ChatColor.WHITE;
        }
        return worldColor + alias + ChatColor.WHITE;
    }

    // TODO: Migrate this method.
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

    private void syncMobs() {

        if (this.getAnimalList().isEmpty()) {
            this.world.setSpawnFlags(this.world.getAllowMonsters(), ((BooleanConfigProperty) this.propertyList.get("animals")).getValue());
        } else {
            this.world.setSpawnFlags(this.world.getAllowMonsters(), true);
        }
        if (this.getMonsterList().isEmpty()) {
            this.world.setSpawnFlags(((BooleanConfigProperty) this.propertyList.get("monsters")).getValue(), this.world.getAllowAnimals());
        } else {
            this.world.setSpawnFlags(true, this.world.getAllowAnimals());
        }
        this.plugin.getMVWorldManager().getWorldPurger().purgeWorld(null, this);
    }

    @Override
    public void setKeepSpawnInMemory(boolean value) {
        ((BooleanConfigProperty) this.propertyList.get("memory")).setValue(value);
        saveConfig();
    }

    // TODO: Provide better feedback
    @Override
    public boolean setProperty(String name, String value) throws PropertyDoesNotExistException {
        if (this.propertyList.containsKey(name)) {
            if (this.propertyList.get(name).parseValue(value)) {
                this.saveConfig();
                return true;
            }
            return false;
        }
        throw new PropertyDoesNotExistException(name);
    }

    @Override
    public String getPropertyValue(String name) throws PropertyDoesNotExistException {
        if (this.propertyList.containsKey(name)) {
            return this.propertyList.get(name).toString();
        }
        throw new PropertyDoesNotExistException(name);
    }

    @Override
    public MVConfigProperty getProperty(String name) throws PropertyDoesNotExistException {
        if (this.propertyList.containsKey(name)) {
            return this.propertyList.get(name);
        }
        throw new PropertyDoesNotExistException(name);
    }

    @Override
    public Environment getEnvironment() {
        // This variable is not settable in-game, therefore does not get a property.
        return this.environment;
    }

    @Override
    public void setEnvironment(Environment environment) {
        // This variable is not settable in-game, therefore does not get a property.
        this.environment = environment;
    }

    @Override
    public Long getSeed() {
        // This variable is not settable in-game, therefore does not get a property.
        return this.seed;
    }

    @Override
    public void setSeed(Long seed) {
        // This variable is not settable in-game, therefore does not get a property.
        this.seed = seed;
    }

    @Override
    public String getName() {
        // This variable is not settable in-game, therefore does not get a property.
        return this.name;
    }

    @Override
    public String getAlias() {
        String alias = ((StringConfigProperty) this.propertyList.get("alias")).getValue();
        if (alias == null || alias.length() == 0) {
            return this.name;
        }
        return alias;

    }

    @Override
    public void setAlias(String alias) {
        ((StringConfigProperty) this.propertyList.get("alias")).setValue(alias);
        this.saveConfig();
    }

    @Override
    public boolean canAnimalsSpawn() {
        return ((BooleanConfigProperty) this.propertyList.get("animals")).getValue();
    }

    @Override
    public void setAllowAnimalSpawn(boolean animals) {
        ((BooleanConfigProperty) this.propertyList.get("animals")).setValue(animals);
        this.saveConfig();
    }

    @Override
    public List<String> getAnimalList() {
        return this.masterList.get("animals");
    }

    @Override
    public boolean canMonstersSpawn() {
        return ((BooleanConfigProperty) this.propertyList.get("monsters")).getValue();
    }

    @Override
    public void setAllowMonsterSpawn(boolean monsters) {
        ((BooleanConfigProperty) this.propertyList.get("monsters")).setValue(monsters);
        this.saveConfig();
    }

    @Override
    public List<String> getMonsterList() {
        return this.masterList.get("monsters");
    }

    @Override
    public boolean isPVPEnabled() {
        return ((BooleanConfigProperty) this.propertyList.get("pvp")).getValue();
    }

    @Override
    public void setPVPMode(boolean pvp) {
        ((BooleanConfigProperty) this.propertyList.get("pvp")).setValue(pvp);
        this.saveConfig();
    }

    @Override
    public boolean isHidden() {
        return ((BooleanConfigProperty) this.propertyList.get("hidden")).getValue();
    }

    @Override
    public void setHidden(boolean hidden) {
        ((BooleanConfigProperty) this.propertyList.get("hidden")).setValue(hidden);
        this.saveConfig();
    }

    public List<String> getWorldBlacklist() {
        return this.masterList.get("worldblacklist");
    }

    @Override
    public double getScaling() {
        return ((DoubleConfigProperty) this.propertyList.get("scale")).getValue();
    }

    @Override
    public boolean setScaling(double scaling) {
        ((DoubleConfigProperty) this.propertyList.get("scale")).setValue(scaling);
        saveConfig();
        return true;
    }

    @Override
    public boolean setColor(String aliasColor) {
        boolean success = this.propertyList.get("color").parseValue(aliasColor);
        if (success) {
            saveConfig();
        }
        return success;
    }

    public boolean isValidAliasColor(String aliasColor) {
        return (EnglishChatColor.fromString(aliasColor) != null);
    }

    @Override
    public ChatColor getColor() {
        return ((ColorConfigProperty) this.propertyList.get("color")).getValue().getColor();
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

    @Deprecated
    public boolean getFakePVP() {
        return false;
    }

    @Override
    public World getRespawnToWorld() {
        return (this.plugin.getServer().getWorld(((StringConfigProperty) this.propertyList.get("respawn")).getValue()));
    }

    @Override
    public boolean setRespawnToWorld(String respawnToWorld) {
        return ((StringConfigProperty) this.propertyList.get("respawn")).setValue(respawnToWorld);
    }

    @Override
    public Permission getAccessPermission() {
        return this.permission;
    }

    @Override
    public int getCurrency() {
        return ((IntegerConfigProperty) this.propertyList.get("curr")).getValue();
    }

    @Override
    public void setCurrency(int currency) {
        ((IntegerConfigProperty) this.propertyList.get("curr")).setValue(currency);
        this.saveConfig();
    }

    @Override
    public double getPrice() {
        return ((DoubleConfigProperty) this.propertyList.get("price")).getValue();
    }

    @Override
    public void setPrice(double price) {
        ((DoubleConfigProperty) this.propertyList.get("price")).setValue(price);
        this.saveConfig();
    }

    @Override
    public Permission getExemptPermission() {
        return this.exempt;
    }

    private void saveConfig() {
        if (this.canSave) {
            try {
                this.changeActiveEffects();
                this.config.save(new File(this.plugin.getDataFolder(), "worlds.yml"));
            } catch (IOException e) {
                this.plugin.log(Level.SEVERE, "Could not save worlds.yml. Please check your filesystem permissions.");
            }
        }
    }

    @Override
    public boolean setGameMode(String gameMode) {
        if (this.propertyList.get("mode").parseValue(gameMode)) {
            saveConfig();
            return true;
        }
        return false;
    }

    @Override
    public GameMode getGameMode() {
        return ((GameModeConfigProperty) this.propertyList.get("mode")).getValue();
    }

    @Override
    public void setEnableWeather(boolean weather) {
        ((BooleanConfigProperty) this.propertyList.get("weather")).setValue(weather);
        this.saveConfig();
    }

    @Override
    public boolean isWeatherEnabled() {
        return ((BooleanConfigProperty) this.propertyList.get("weather")).getValue();
    }

    @Override
    public boolean isKeepingSpawnInMemory() {
        return ((BooleanConfigProperty) this.propertyList.get("memory")).getValue();
    }

    @Override
    public void setHunger(boolean hunger) {
        ((BooleanConfigProperty) this.propertyList.get("weather")).setValue(hunger);
        this.saveConfig();
    }

    @Override
    public boolean getHunger() {
        return ((BooleanConfigProperty) this.propertyList.get("hunger")).getValue();
    }

    @Override
    public void setSpawnLocation(Location l) {
        ((LocationConfigProperty) this.propertyList.get("spawn")).setValue(l);
        this.saveConfig();
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
        if (!bs.playerCanSpawnHereSafely(spawnLocation)) {
            if (!((BooleanConfigProperty) this.propertyList.get("adjustspawn")).getValue()) {
                this.plugin.log(Level.WARNING, "Spawn location from world.dat file was unsafe!!");
                this.plugin.log(Level.WARNING, "NOT adjusting spawn for '" + this.getAlias() + "' because you told me not to.");
                this.plugin.log(Level.WARNING, "To turn on spawn adjustment for this world simply type:");
                this.plugin.log(Level.WARNING, "/mvm set adjustspawn true " + this.getAlias());
                return spawnLocation;
            }
            this.plugin.log(Level.WARNING, "Spawn location from world.dat file was unsafe. Adjusting...");
            Location newSpawn = teleporter.getSafeLocation(spawnLocation, 128, 128);
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
        return spawnLocation;
    }

    @Override
    public Location getSpawnLocation() {
        return ((LocationConfigProperty) this.propertyList.get("spawn")).getValue();
    }

    @Override
    public Difficulty getDifficulty() {
        return this.getCBWorld().getDifficulty();
    }

    @Override
    public boolean setDifficulty(String difficulty) {
        if (this.propertyList.get("diff").parseValue(difficulty)) {
            saveConfig();
            return true;
        }
        return false;
    }

    @Override
    public boolean getAutoHeal() {
        return ((BooleanConfigProperty) this.propertyList.get("autoheal")).getValue();
    }

    @Override
    public void setAutoHeal(boolean heal) {
        ((BooleanConfigProperty) this.propertyList.get("autoheal")).setValue(heal);
        saveConfig();
    }

    @Override
    public void setAdjustSpawn(boolean adjust) {
        ((BooleanConfigProperty) this.propertyList.get("adjustspawn")).setValue(adjust);
        saveConfig();
    }

    @Override
    public boolean getAdjustSpawn() {
        return ((BooleanConfigProperty) this.propertyList.get("adjustspawn")).getValue();
    }
}
