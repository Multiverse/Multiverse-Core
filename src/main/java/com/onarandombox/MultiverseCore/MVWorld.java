/******************************************************************************
 * Multiverse 2 Copyright (c) the Multiverse Team 2011.                       *
 * Multiverse 2 is licensed under the BSD License.                            *
 * For more information please check the README.md file included              *
 * with this project.                                                         *
 ******************************************************************************/

package com.onarandombox.MultiverseCore;

import com.onarandombox.MultiverseCore.api.BlockSafety;
import com.onarandombox.MultiverseCore.api.MultiverseWorld;
import com.onarandombox.MultiverseCore.configuration.ConfigPropertyFactory;
import com.onarandombox.MultiverseCore.configuration.MVActiveConfigProperty;
import com.onarandombox.MultiverseCore.configuration.MVConfigProperty;
import com.onarandombox.MultiverseCore.enums.AllowedPortalType;
import com.onarandombox.MultiverseCore.enums.EnglishChatColor;
import com.onarandombox.MultiverseCore.event.MVWorldPropertyChangeEvent;
import com.onarandombox.MultiverseCore.exceptions.PropertyDoesNotExistException;
import com.onarandombox.MultiverseCore.api.SafeTTeleporter;
import org.bukkit.ChatColor;
import org.bukkit.Difficulty;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.World.Environment;
import org.bukkit.WorldType;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.permissions.Permission;
import org.bukkit.permissions.PermissionDefault;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * The implementation of a Multiverse handled world.
 */
public class MVWorld implements MultiverseWorld {

    private MultiverseCore plugin; // Hold the Plugin Instance.
    private FileConfiguration config; // Hold the Configuration File.
    private ConfigurationSection worldSection; // Holds the section of the config file for this world.

    private World world; // The World Instance.
    private Environment environment; // Hold the Environment type EG Environment.NETHER / Environment.NORMAL
    private Long seed; // The world seed
    private String name; // The Worlds Name, EG its folder name.

    private Map<String, List<String>> masterList;
    private Map<String, MVConfigProperty<?>> propertyList;

    private Permission permission;
    private Permission exempt;

    private boolean canSave = false; // Prevents all the setters from constantly saving to the config when being called from the constructor.
    private Map<String, String> propertyAliases;
    private Permission ignoreperm;

    private static final Map<String, String> TIME_ALIASES;
    private WorldType type;

    static {
        Map<String, String> staticTimes = new HashMap<String, String>();
        staticTimes.put("morning", "8:00");
        staticTimes.put("day", "12:00");
        staticTimes.put("noon", "12:00");
        staticTimes.put("midnight", "0:00");
        staticTimes.put("night", "20:00");

        // now set TIME_ALIASES to a "frozen" map
        TIME_ALIASES = Collections.unmodifiableMap(staticTimes);
    }

    public MVWorld(World world, FileConfiguration config, MultiverseCore instance, Long seed, String generatorString, boolean fixSpawn) {
        this.config = config;
        this.plugin = instance;

        // Set local values that CANNOT be changed by user
        this.world = world;
        this.name = world.getName();
        this.seed = seed;
        this.environment = world.getEnvironment();
        this.type = world.getWorldType();

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

        worldSection.set("type", this.type.toString());

        // Start NEW config awesomeness.
        ConfigPropertyFactory fac = new ConfigPropertyFactory(this.worldSection);
        this.propertyList = new HashMap<String, MVConfigProperty<?>>();
        // The format of these are either:
        // getNewProperty(name, defaultValue, helpText)
        // or
        // getNewProperty(name, defaultValue, yamlConfigNode, helpText)
        //
        // If the first type is used, name is used as the yamlConfigNode
        this.propertyList.put("hidden", fac.getNewProperty("hidden", false,
                "Sorry, 'hidden' must either be:" + ChatColor.GREEN + " true " + ChatColor.WHITE + "or" + ChatColor.RED + " false" + ChatColor.WHITE + "."));
        this.propertyList.put("alias", fac.getNewProperty("alias", "", "alias.name",
                "Alias must be a valid string."));
        this.propertyList.put("color", fac.getNewProperty("color", EnglishChatColor.WHITE, "alias.color",
                "Sorry, 'color' must either one of: " + EnglishChatColor.getAllColors()));
        this.propertyList.put("pvp", fac.getNewProperty("pvp", true, "pvp",
                "Sorry, 'pvp' must either be:" + ChatColor.GREEN + " true " + ChatColor.WHITE
                + "or" + ChatColor.RED + " false" + ChatColor.WHITE + ".", "setActualPVP"));
        this.propertyList.put("scale", fac.getNewProperty("scale", this.getDefaultScale(this.environment), "scale",
                "Scale must be a positive double value. ex: " + ChatColor.GOLD + "2.3", "verifyScaleSetProperly"));
        this.propertyList.put("respawn", fac.getNewProperty("respawn", "", "respawnworld",
                "You must set this to the " + ChatColor.GOLD + " NAME" + ChatColor.RED + " not alias of a world."));
        this.propertyList.put("weather", fac.getNewProperty("weather", true, "allowweather",
                "Sorry, 'weather' must either be:" + ChatColor.GREEN + " true " + ChatColor.WHITE
                + "or" + ChatColor.RED + " false" + ChatColor.WHITE + ".", "setActualWeather"));
        this.propertyList.put("difficulty", fac.getNewProperty("difficulty", Difficulty.EASY,
                "Difficulty must be set as one of the following: " + ChatColor.GREEN + "peaceful "
                        + ChatColor.AQUA + "easy " + ChatColor.GOLD + "normal " + ChatColor.RED + "hard"));
        this.propertyList.put("animals", fac.getNewProperty("animals", true, "animals.spawn",
                "Sorry, 'animals' must either be:" + ChatColor.GREEN + " true " + ChatColor.WHITE
                + "or" + ChatColor.RED + " false" + ChatColor.WHITE + ".", "syncMobs"));
        this.propertyList.put("monsters", fac.getNewProperty("monsters", true, "monsters.spawn",
                "Sorry, 'monsters' must either be:" + ChatColor.GREEN + " true " + ChatColor.WHITE
                    + "or" + ChatColor.RED + " false" + ChatColor.WHITE + ".", "syncMobs"));
        this.propertyList.put("currency", fac.getNewProperty("currency", -1, "entryfee.currency",
                "Currency must be an integer between -1 and the highest Minecraft item ID."));
        this.propertyList.put("price", fac.getNewProperty("price", 0.0, "entryfee.price",
                "Price must be a double value. ex: " + ChatColor.GOLD + "1.2" + ChatColor.WHITE
                        + ". Set to a negative value to give players money for entering this world."));
        this.propertyList.put("hunger", fac.getNewProperty("hunger", true,
                "Sorry, 'hunger' must either be:" + ChatColor.GREEN + " true " + ChatColor.WHITE + "or" + ChatColor.RED + " false" + ChatColor.WHITE + "."));
        this.propertyList.put("autoheal", fac.getNewProperty("autoheal", true,
                "Sorry, 'autoheal' must either be:" + ChatColor.GREEN + " true " + ChatColor.WHITE + "or" + ChatColor.RED + " false" + ChatColor.WHITE + "."));
        this.propertyList.put("adjustspawn", fac.getNewProperty("adjustspawn", true,
                "Sorry, 'adjustspawn' must either be:" + ChatColor.GREEN + " true " + ChatColor.WHITE
                        + "or" + ChatColor.RED + " false" + ChatColor.WHITE + "."));
        this.propertyList.put("portalform", fac.getNewProperty("portalform", AllowedPortalType.ALL,
                        "Allow portal forming must be NONE, ALL, NETHER or END."));
        if (!fixSpawn) {
            this.setAdjustSpawn(false);
        }
        this.propertyList.put("gamemode", fac.getNewProperty("gamemode", GameMode.SURVIVAL,
                "GameMode must be set as one of the following: " + ChatColor.RED + "survival " + ChatColor.GREEN + "creative "));
        this.propertyList.put("memory", fac.getNewProperty("keepspawninmemory", true, "keepspawninmemory",
                "Sorry, 'memory' must either be:" + ChatColor.GREEN + " true "
                + ChatColor.WHITE + "or" + ChatColor.RED + " false" + ChatColor.WHITE + ".", "setActualKeepSpawnInMemory"));
        this.propertyList.put("spawn", fac.getNewProperty("spawn", this.world.getSpawnLocation(), "spawn",
                "There is no help available for this variable. Go bug Rigby90 about it.", "setActualKeepSpawnInMemory"));
        this.propertyList.put("autoload", fac.getNewProperty("autoload", true,
                "Set this to false ONLY if you don't want this world to load itself on server restart."));
        this.propertyList.put("bedrespawn", fac.getNewProperty("bedrespawn", true, "If a player dies in this world, shoudld they go to their bed?"));
        this.propertyList.put("time", fac.getNewProperty("time", "", "Set the time to whatever you want! (Will NOT freeze time)", "setActualTime", true));
        this.getKnownProperty("spawn", Location.class).setValue(this.readSpawnFromConfig(this.getCBWorld()));


        // Set aliases
        this.propertyAliases = new HashMap<String, String>();
        this.propertyAliases.put("curr", "currency");
        this.propertyAliases.put("scaling", "scale");
        this.propertyAliases.put("aliascolor", "color");
        this.propertyAliases.put("heal", "autoheal");
        this.propertyAliases.put("storm", "weather");
        this.propertyAliases.put("spawnmemory", "memory");
        this.propertyAliases.put("mode", "gamemode");
        this.propertyAliases.put("diff", "difficulty");

        // Things I haven't converted yet.
        this.getMobExceptions();
        this.getWorldBlacklist().addAll(worldSection.getList("worldblacklist", new ArrayList<String>()));

        // Enable and do the save.
        this.canSave = true;
        this.saveConfig();

        this.permission = new Permission("multiverse.access." + this.getName(), "Allows access to " + this.getName(), PermissionDefault.OP);
        // This guy is special. He shouldn't be added to any parent perms.
        this.ignoreperm = new Permission("mv.bypass.gamemode." + this.getName(),
                "Allows players with this permission to ignore gamemode changes.", PermissionDefault.FALSE);

        this.exempt = new Permission("multiverse.exempt." + this.getName(),
                "A player who has this does not pay to enter this world, or use any MV portals in it " + this.getName(), PermissionDefault.OP);
        try {
            this.plugin.getServer().getPluginManager().addPermission(this.permission);
            this.plugin.getServer().getPluginManager().addPermission(this.exempt);
            this.plugin.getServer().getPluginManager().addPermission(this.ignoreperm);
            // Add the permission and exempt to parents.
            this.addToUpperLists(this.permission);

            // Add ignore to it's parent:
            this.ignoreperm.addParent("mv.bypass.gamemode.*", true);
        } catch (IllegalArgumentException e) {
            this.plugin.log(Level.FINER, "Permissions nodes were already added for " + this.name);
        }

        // Sync all active settings.
        this.setActualPVP();
        this.verifyScaleSetProperly();
        this.setActualKeepSpawnInMemory();
        this.setActualDifficulty();
        this.setActualGameMode();
        this.setActualSpawn();
        this.syncMobs();
    }

    /**
     * Used by the active PVP-property to set the "actual" PVP-property.
     * @return True if the property was successfully set.
     */
    public boolean setActualPVP() {
        // Set the PVP mode
        this.world.setPVP(this.getKnownProperty("pvp", Boolean.class).getValue());
        return true;
    }

    /**
     * Used by the active scale-property to set the "actual" scale-property.
     * @return True if the property was successfully set.
     */
    public boolean verifyScaleSetProperly() {
        // Ensure the scale is above 0
        if (this.getKnownProperty("scale", Double.class).getValue() <= 0) {
            // Disallow negative or 0 scalings.
            this.getKnownProperty("scale", Double.class).setValue(1.0);
            this.plugin.log(Level.WARNING, "Someone tried to set a scale <= 0, defaulting to 1.");
        }
        return true;
    }

    /**
     * Used by the active keepSpawnInMemory-property to set the "actual" property.
     * @return True if the property was successfully set.
     */
    public boolean setActualKeepSpawnInMemory() {
        // Ensure the memory setting is correct
        this.getCBWorld().setKeepSpawnInMemory(this.getKnownProperty("memory", Boolean.class).getValue());
        return true;
    }

    /**
     * Used by the active difficulty-property to set the "actual" property.
     * @return True if the property was successfully set.
     */
    public boolean setActualDifficulty() {
        this.getCBWorld().setDifficulty(this.getKnownProperty("difficulty", Difficulty.class).getValue());
        return true;
    }

    /**
     * Used by the active spawn-property to set the "actual" property.
     * @return True if the property was successfully set.
     */
    public boolean setActualSpawn() {
        // Set the spawn location
        Location spawnLocation = this.getKnownProperty("spawn", Location.class).getValue();
        this.getCBWorld().setSpawnLocation(spawnLocation.getBlockX(), spawnLocation.getBlockY(), spawnLocation.getBlockZ());
        return true;
    }

    private double getDefaultScale(Environment environment) {
        if (environment == Environment.NETHER) {
            return 8.0; // SUPPRESS CHECKSTYLE: MagicNumberCheck
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

    /**
     * {@inheritDoc}
     */
    @Override
    public String getColoredWorldString() {
        EnglishChatColor worldColor = this.getKnownProperty("color", EnglishChatColor.class).getValue();
        String alias = this.getKnownProperty("alias", String.class).getValue();
        if (worldColor == null) {
            this.setKnownProperty("color", "WHITE", null);
            return alias + ChatColor.WHITE;
        } else if (worldColor.getColor() == null) {
            return alias + ChatColor.WHITE;
        }
        if (alias.length() == 0) {
            alias = this.getName();
        }
        return worldColor.getColor() + alias + ChatColor.WHITE;
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

    /**
     * {@inheritDoc}
     */
    @Override
    public World getCBWorld() {
        return this.world;
    }

    private void initLists() {
        this.masterList = new HashMap<String, List<String>>();
        this.masterList.put("worldblacklist", new ArrayList<String>());
        this.masterList.put("animals", new ArrayList<String>());
        this.masterList.put("monsters", new ArrayList<String>());
    }

    /**
     * {@inheritDoc}
     */
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

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean addToVariable(String property, String value) {
        property = property.toLowerCase();
        if (this.masterList.keySet().contains(property)) {

            if (property.equals("animals") || property.equals("monsters")) {
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

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean removeFromVariable(String property, String value) {
        property = property.toLowerCase();
        if (this.masterList.keySet().contains(property)) {

            if (property.equals("animals") || property.equals("monsters")) {
                this.masterList.get(property).remove(value.toUpperCase());
                this.worldSection.set(property + ".exceptions", this.masterList.get(property));
                this.syncMobs();
            } else {
                this.masterList.get(property).remove(value);
                this.worldSection.set(property, this.masterList.get(property));
            }
            saveConfig();
            return true;
        }
        return false;
    }

    /**
     * Ensure that the value of the animals and monsters config
     * properties are set in accordance with the current animals
     * and monsters in the world, respectively.
     */
    public void syncMobs() {

        if (this.getAnimalList().isEmpty()) {
            this.world.setSpawnFlags(this.world.getAllowMonsters(), this.getKnownProperty("animals", Boolean.class).getValue());
        } else {
            this.world.setSpawnFlags(this.world.getAllowMonsters(), true);
        }
        if (this.getMonsterList().isEmpty()) {
            this.world.setSpawnFlags(this.getKnownProperty("monsters", Boolean.class).getValue(), this.world.getAllowAnimals());
        } else {
            this.world.setSpawnFlags(true, this.world.getAllowAnimals());
        }
        this.plugin.getMVWorldManager().getTheWorldPurger().purgeWorld(this);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setKeepSpawnInMemory(boolean value) {
        this.getKnownProperty("memory", Boolean.class).setValue(value);
        saveConfig();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    // TODO: Provide better feedback
    public boolean setProperty(String name, String value, CommandSender sender) throws PropertyDoesNotExistException {
        if (!this.isValidPropertyName(name)) {
            throw new PropertyDoesNotExistException(name);
        }
        return this.setKnownProperty(name, value, sender) || this.setKnownProperty(this.propertyAliases.get(name), value, sender);

    }

    private boolean isValidPropertyName(String name) {
        return this.propertyList.containsKey(name) || this.propertyAliases.containsKey(name);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getPropertyValue(String name) throws PropertyDoesNotExistException {
        if (this.propertyList.containsKey(name)) {
            return this.getKnownProperty(name, Object.class).toString();
        }
        throw new PropertyDoesNotExistException(name);
    }

    /**
     * {@inheritDoc}
     *
     * @deprecated Use {@link #getProperty(String, Class)} instead
     */
    @Override
    @Deprecated
    public MVConfigProperty<?> getProperty(String property) throws PropertyDoesNotExistException {
        return getProperty(property, Object.class);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public <T> MVConfigProperty<T> getProperty(String name, Class<T> expected) throws PropertyDoesNotExistException {
        MVConfigProperty<T> p = this.getKnownProperty(name, expected);
        if (p == null) {
            throw new PropertyDoesNotExistException(name);
        }
        return p;
    }

    /**
     * This method should only be used from inside this class when it is KNOWN that the property exists.
     *
     * @param name     The known name of a property
     * @param expected The Type of the expected value
     * @return The property object.
     */
    @SuppressWarnings("unchecked")
    private <T> MVConfigProperty<T> getKnownProperty(String name, Class<T> expected) {
        try {
            if (this.propertyList.containsKey(name)) {
                return (MVConfigProperty<T>) this.propertyList.get(name);
            } else if (this.propertyAliases.containsKey(name)) {
                // If the property was defined in the alias table, make sure to grab the actual name
                return (MVConfigProperty<T>) this.propertyList.get(this.propertyAliases.get(name));
            }
        } catch (ClassCastException e) {
            return null;
        }
        return null;
    }

    /**
     * This method should only be used from inside this class when it is KNOWN that the property exists.
     *
     * @param name   The known name of a property.
     * @param value  The value that is trying to be set.
     * @param sender The person sending the command, MAY BE NULL.
     * @return True if the property was saved, false if not.
     */
    private boolean setKnownProperty(String name, String value, CommandSender sender) {
        MVConfigProperty<?> property;
        if (this.propertyList.containsKey(name)) {
            property = this.getKnownProperty(name, Object.class);
        } else if (this.propertyAliases.containsKey(name)) {
            return this.setKnownProperty(this.propertyAliases.get(name), value, sender);
        } else {
            return false;
        }
        // Only allow people to cancel events when they're not the initializations.
        if (this.canSave) {
            MVWorldPropertyChangeEvent propertyChangeEvent = new MVWorldPropertyChangeEvent(this, sender, name, value);
            this.plugin.getServer().getPluginManager().callEvent(propertyChangeEvent);
            if (propertyChangeEvent.isCancelled()) {
                this.plugin.log(Level.FINE, "Someone else cancelled the WorldPropertyChanged Event!!!");
                return false;
            }
            value = propertyChangeEvent.getNewValue();
        }
        if (property.parseValue(value)) {
            if (property instanceof MVActiveConfigProperty) {
                return this.setActiveProperty((MVActiveConfigProperty<?>) property);
            }
            this.saveConfig();
            return true;
        }
        return false;
    }

    private boolean setActiveProperty(MVActiveConfigProperty<?> property) {
        try {
            if (property.getMethod() == null) {
                // This property did not have a method.
                this.saveConfig();
                return true;
            }
            Method method = this.getClass().getMethod(property.getMethod());
            Object returnVal = method.invoke(this);
            if (returnVal instanceof Boolean) {
                if ((Boolean) returnVal) {
                    this.saveConfig();
                }
                return (Boolean) returnVal;
            } else {
                this.saveConfig();
                return true;
            }
        } catch (Exception e) {
            // TODO: I don't care about 3 catches,
            // TODO: I hate pokemon errors :/ - FernFerret
            e.printStackTrace();
            return false;
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Environment getEnvironment() {
        // This variable is not settable in-game, therefore does not get a property.
        return this.environment;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setEnvironment(Environment environment) {
        // This variable is not settable in-game, therefore does not get a property.
        this.environment = environment;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Long getSeed() {
        // This variable is not settable in-game, therefore does not get a property.
        return this.seed;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setSeed(Long seed) {
        // This variable is not settable in-game, therefore does not get a property.
        this.seed = seed;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getName() {
        // This variable is not settable in-game, therefore does not get a property.
        return this.name;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getPermissibleName() {
        return this.name.toLowerCase();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getAlias() {
        String alias = this.getKnownProperty("alias", String.class).getValue();
        if (alias == null || alias.length() == 0) {
            return this.name;
        }
        return alias;

    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setAlias(String alias) {
        this.setKnownProperty("alias", alias, null);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean canAnimalsSpawn() {
        return this.getKnownProperty("animals", Boolean.class).getValue();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setAllowAnimalSpawn(boolean animals) {
        this.setKnownProperty("animals", animals + "", null);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<String> getAnimalList() {
        return this.masterList.get("animals");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean canMonstersSpawn() {
        return this.getKnownProperty("monsters", Boolean.class).getValue();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setAllowMonsterSpawn(boolean monsters) {
        this.setKnownProperty("monsters", monsters + "", null);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<String> getMonsterList() {
        return this.masterList.get("monsters");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isPVPEnabled() {
        return this.getKnownProperty("pvp", Boolean.class).getValue();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setPVPMode(boolean pvp) {
        this.setKnownProperty("pvp", pvp + "", null);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isHidden() {
        return this.getKnownProperty("hidden", Boolean.class).getValue();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setHidden(boolean hidden) {
        this.setKnownProperty("hidden", hidden + "", null);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<String> getWorldBlacklist() {
        return this.masterList.get("worldblacklist");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public double getScaling() {
        return this.getKnownProperty("scale", Double.class).getValue();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean setScaling(double scaling) {
        return this.setKnownProperty("scale", scaling + "", null);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean setColor(String aliasColor) {
        return this.setKnownProperty("color", aliasColor, null);
    }

    /**
     * {@inheritDoc}
     */
    @Override // TODO This method should be static. (Maybe EnglishChatColor would be a good place?)
    public boolean isValidAliasColor(String aliasColor) {
        return (EnglishChatColor.fromString(aliasColor) != null);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ChatColor getColor() {
        return this.getKnownProperty("color", EnglishChatColor.class).getValue().getColor();
    }

    /**
     * {@inheritDoc}
     */
    @Override
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

    /**
     * {@inheritDoc}
     *
     * @deprecated This is deprecated.
     */
    @Override
    @Deprecated
    public boolean getFakePVP() {
        return false;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public World getRespawnToWorld() {
        return (this.plugin.getServer().getWorld(this.getKnownProperty("respawn", String.class).getValue()));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean setRespawnToWorld(String respawnToWorld) {
        if (!this.plugin.getMVWorldManager().isMVWorld(respawnToWorld)) return false;
        return this.setKnownProperty("respawn", respawnToWorld, null);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Permission getAccessPermission() {
        return this.permission;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getCurrency() {
        return this.getKnownProperty("curr", Integer.class).getValue();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setCurrency(int currency) {
        this.setKnownProperty("curr", currency + "", null);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public double getPrice() {
        return this.getKnownProperty("price", Double.class).getValue();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setPrice(double price) {
        this.setKnownProperty("price", price + "", null);
    }

    /**
     * {@inheritDoc}
     */
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

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean setGameMode(String gameMode) {
        return this.setKnownProperty("mode", gameMode, null);
    }

    /**
     * Sets the actual gamemode by iterating through players.
     *
     * gameMode is not used, but it's in the reflection template.
     *
     * Needs a bit o' refactoring.
     *
     * @return True if the gamemodes of players were set successfully. (always)
     */
    public boolean setActualGameMode() {
        for (Player p : this.plugin.getServer().getWorld(this.getName()).getPlayers()) {
            this.plugin.log(Level.FINER, String.format("Setting %s's GameMode to %s",
                    p.getName(), this.getKnownProperty("mode", GameMode.class).getValue().toString()));
            this.plugin.getPlayerListener().handleGameMode(p, this);
        }
        return true;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public GameMode getGameMode() {
        return this.getKnownProperty("mode", GameMode.class).getValue();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setEnableWeather(boolean weather) {
        this.setKnownProperty("weather", weather + "", null);
    }

    /**
     * Used by the active weather-property to set the "actual" property.
     * @return True if the property was successfully set.
     */
    public boolean setActualWeather() {
        // Disable any current weather
        if (!this.getKnownProperty("weather", Boolean.class).getValue()) {
            this.getCBWorld().setStorm(false);
            this.getCBWorld().setThundering(false);
        }
        return true;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isWeatherEnabled() {
        return this.getKnownProperty("weather", Boolean.class).getValue();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isKeepingSpawnInMemory() {
        return this.getKnownProperty("memory", Boolean.class).getValue();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setHunger(boolean hunger) {
        this.setKnownProperty("hunger", hunger + "", null);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean getHunger() {
        return this.getKnownProperty("hunger", Boolean.class).getValue();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setSpawnLocation(Location l) {
        this.getCBWorld().setSpawnLocation(l.getBlockX(), l.getBlockY(), l.getBlockZ());
        this.getKnownProperty("spawn", Location.class).setValue(l);
        this.saveConfig();
    }

    private static final int SPAWN_LOCATION_SEARCH_TOLERANCE = 16;
    private static final int SPAWN_LOCATION_SEARCH_RADIUS = 16;

    private Location readSpawnFromConfig(World w) {
        Location spawnLocation = w.getSpawnLocation();
        Location configLocation = this.getSpawnLocation();

        // Set the worldspawn to our configspawn
        w.setSpawnLocation(configLocation.getBlockX(), configLocation.getBlockY(), configLocation.getBlockZ());
        SafeTTeleporter teleporter = this.plugin.getSafeTTeleporter();
        BlockSafety bs = this.plugin.getBlockSafety();
        // Verify that location was safe
        if (!bs.playerCanSpawnHereSafely(configLocation)) {
            if (!this.getAdjustSpawn()) {
                this.plugin.log(Level.FINE, "Spawn location from world.dat file was unsafe!!");
                this.plugin.log(Level.FINE, "NOT adjusting spawn for '" + this.getAlias() + "' because you told me not to.");
                this.plugin.log(Level.FINE, "To turn on spawn adjustment for this world simply type:");
                this.plugin.log(Level.FINE, "/mvm set adjustspawn true " + this.getAlias());
                return configLocation;
            }
            // If it's not, find a better one.
            this.plugin.log(Level.WARNING, "Spawn location from world.dat file was unsafe. Adjusting...");
            this.plugin.log(Level.WARNING, "Original Location: " + plugin.getLocationManipulation().strCoordsRaw(spawnLocation));
            Location newSpawn = teleporter.getSafeLocation(spawnLocation,
                    SPAWN_LOCATION_SEARCH_TOLERANCE, SPAWN_LOCATION_SEARCH_RADIUS);
            // I think we could also do this, as I think this is what Notch does.
            // Not sure how it will work in the nether...
            //Location newSpawn = this.spawnLocation.getWorld().getHighestBlockAt(this.spawnLocation).getLocation();
            if (newSpawn != null) {
                this.setSpawnLocation(newSpawn);
                configLocation = this.getSpawnLocation();
                this.plugin.log(Level.INFO, "New Spawn for '" + this.getName()
                        + "' is Located at: " + plugin.getLocationManipulation().locationToString(configLocation));
            } else {
                // If it's a standard end world, let's check in a better place:
                Location newerSpawn;
                newerSpawn = bs.getTopBlock(new Location(w, 0, 0, 0));
                if (newerSpawn != null) {
                    this.setSpawnLocation(newerSpawn);
                    configLocation = this.getSpawnLocation();
                    this.plugin.log(Level.INFO, "New Spawn for '" + this.getName()
                            + "' is Located at: " + plugin.getLocationManipulation().locationToString(configLocation));
                } else {
                    this.plugin.log(Level.SEVERE, "New safe spawn NOT found!!!");
                }


            }
        }
        return configLocation;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Location getSpawnLocation() {
        return this.getKnownProperty("spawn", Location.class).getValue();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Difficulty getDifficulty() {
        return this.getCBWorld().getDifficulty();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean setDifficulty(String difficulty) {
        if (this.setKnownProperty("diff", difficulty, null)) {
            // Set the difficulty
            this.getCBWorld().setDifficulty(this.getKnownProperty("diff", Difficulty.class).getValue());
            return true;
        }
        return false;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean getAutoHeal() {
        return this.getKnownProperty("autoheal", Boolean.class).getValue();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setAutoHeal(boolean heal) {
        this.setKnownProperty("autoheal", heal + "", null);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setAdjustSpawn(boolean adjust) {
        this.setKnownProperty("adjustspawn", adjust + "", null);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean getAdjustSpawn() {
        return this.getKnownProperty("adjustspawn", Boolean.class).getValue();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setAutoLoad(boolean autoLoad) {
        this.setKnownProperty("autoload", autoLoad + "", null);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean getAutoLoad() {
        return this.getKnownProperty("autoload", Boolean.class).getValue();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setBedRespawn(boolean respawn) {
        this.setKnownProperty("bedrespawn", respawn + "", null);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean getBedRespawn() {
        return this.getKnownProperty("bedrespawn", Boolean.class).getValue();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getAllPropertyNames() {
        ChatColor color = ChatColor.AQUA;
        String result = "";
        for (String propertyNames : this.propertyList.keySet()) {
            result += color + propertyNames + " ";
            color = (color == ChatColor.AQUA) ? ChatColor.GOLD : ChatColor.AQUA;
        }
        return result;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getTime() {
        long time = this.getCBWorld().getTime();
        // I'm tired, so they get time in 24 hour for now.
        // Someone else can add 12 hr format if they want :P

        // BEGIN CHECKSTYLE-SUPPRESSION: MagicNumberCheck
        int hours = (int) ((time / 1000 + 8) % 24);
        int minutes = (int) (60 * (time % 1000) / 1000);
        // END CHECKSTYLE-SUPPRESSION: MagicNumberCheck

        return String.format("%d:%02d", hours, minutes);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public WorldType getWorldType() {
        return this.type;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void allowPortalMaking(AllowedPortalType type) {
        this.setKnownProperty("portalform", type.toString(), null);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public AllowedPortalType getAllowedPortals() {
        return this.getKnownProperty("portalform", AllowedPortalType.class).getValue();
    }

    /**
     * Used by the active time-property to set the "actual" property.
     * @return True if the property was successfully set.
     */
    public boolean setActualTime() {
        return this.setTime(this.getKnownProperty("time", String.class).toString());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    // BEGIN CHECKSTYLE-SUPPRESSION: MagicNumberCheck
    public boolean setTime(String timeAsString) {
        if (TIME_ALIASES.containsKey(timeAsString.toLowerCase())) {
            return this.setTime(TIME_ALIASES.get(timeAsString.toLowerCase()));
        }
        // Regex that extracts a time in the following formats:
        // 11:11pm, 11:11, 23:11, 1111, 1111p, and the aliases at the top of this file.
        String timeRegex = "(\\d\\d?):?(\\d\\d)(a|p)?m?";
        Pattern pattern = Pattern.compile(timeRegex, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(timeAsString);
        matcher.find();
        int hour = 0;
        double minute = 0;
        int count = matcher.groupCount();
        if (count >= 2) {
            hour = Integer.parseInt(matcher.group(1));
            minute = Integer.parseInt(matcher.group(2));
        }
        // If there were 4 matches (all, hour, min, ampm)
        if (count == 4) {
            // We want 24 hour time for calcs, but if they
            // added a p[m], turn it into a 24 hr one.
            if (matcher.group(3).equals("p")) {
                hour += 12;
            }
        }
        // Translate 24th hour to 0th hour.
        if (hour == 24) { // SUPPRESS CHECKSTYLE MagicNumberCheck
            hour = 0;
        }
        // Clamp the hour
        if (hour > 23 || hour < 0) {
            return false;
        }
        // Clamp the minute
        if (minute > 59 || minute < 0) {
            return false;
        }
        // 60 seconds in a minute, time needs to be in hrs * 1000, per
        // the bukkit docs.
        double totaltime = (hour + (minute / 60.0)) * 1000;
        // Somehow there's an 8 hour offset...
        totaltime -= 8000;
        if (totaltime < 0) {
            totaltime = 24000 + totaltime;
        }

        this.getCBWorld().setTime((long) totaltime);
        return true;
    }
    // END CHECKSTYLE-SUPPRESSION: MagicNumberCheck

    @Override
    public String toString() {
        StringBuilder toStringBuilder = new StringBuilder();
        toStringBuilder.append(this.getClass().getSimpleName());
        toStringBuilder.append('@');
        toStringBuilder.append(this.hashCode());
        toStringBuilder.append(" (Name: '").append(this.getName()).append("')");
        return toStringBuilder.toString();
    }
}
