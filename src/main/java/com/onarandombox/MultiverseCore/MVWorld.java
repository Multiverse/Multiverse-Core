/******************************************************************************
 * Multiverse 2 Copyright (c) the Multiverse Team 2011.                       *
 * Multiverse 2 is licensed under the BSD License.                            *
 * For more information please check the README.md file included              *
 * with this project.                                                         *
 ******************************************************************************/

package com.onarandombox.MultiverseCore;

import com.onarandombox.MultiverseCore.api.BlockSafety;
import com.onarandombox.MultiverseCore.api.MultiverseWorld;
import com.onarandombox.MultiverseCore.configuration.EntryFee;
import com.onarandombox.MultiverseCore.configuration.SpawnLocation;
import com.onarandombox.MultiverseCore.configuration.SpawnSettings;
import com.onarandombox.MultiverseCore.configuration.WorldPropertyValidator;
import com.onarandombox.MultiverseCore.enums.AllowedPortalType;
import com.onarandombox.MultiverseCore.enums.EnglishChatColor;
import com.onarandombox.MultiverseCore.exceptions.PropertyDoesNotExistException;
import com.onarandombox.MultiverseCore.api.SafeTTeleporter;

import me.main__.util.SerializationConfig.ChangeDeniedException;
import me.main__.util.SerializationConfig.IllegalPropertyValueException;
import me.main__.util.SerializationConfig.NoSuchPropertyException;
import me.main__.util.SerializationConfig.Property;
import me.main__.util.SerializationConfig.SerializationConfig;
import me.main__.util.SerializationConfig.Serializor;
import me.main__.util.SerializationConfig.ValidateAllWith;
import me.main__.util.SerializationConfig.VirtualProperty;

import org.bukkit.ChatColor;
import org.bukkit.Difficulty;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.World.Environment;
import org.bukkit.WorldType;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.serialization.SerializableAs;
import org.bukkit.entity.Player;
import org.bukkit.permissions.Permission;
import org.bukkit.permissions.PermissionDefault;

import java.lang.ref.Reference;
import java.lang.ref.WeakReference;
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
@SerializableAs("MVWorld")
@ValidateAllWith(WorldPropertyValidator.class)
public class MVWorld extends SerializationConfig implements MultiverseWorld {
    private static final int SPAWN_LOCATION_SEARCH_TOLERANCE = 16;
    private static final int SPAWN_LOCATION_SEARCH_RADIUS = 16;

    /*
     * We have to use setCBWorld(), setPlugin() and initPerms() to prepare this object for use.
     */
    public MVWorld(Map<String, Object> values) {
        super(values);
    }

    private MultiverseCore plugin; // Hold the Plugin Instance.

    private Reference<World> world; // A reference to the World Instance.
    private String name; // The Worlds Name, EG its folder name.

    /**
     * Validates the scale-property.
     */
    private final class ScalePropertyValidator extends WorldPropertyValidator<Double> {
        @Override
        public Double validateChange(String property, Double newValue, Double oldValue,
                MVWorld object) throws ChangeDeniedException {
            if (newValue <= 0) {
                plugin.log(Level.FINE, "Someone tried to set a scale <= 0, aborting!");
                throw new ChangeDeniedException();
            }
            return super.validateChange(property, newValue, oldValue, object);
        }
    }

    /**
     * Validates the respawnWorld-property.
     */
    private final class RespawnWorldPropertyValidator extends WorldPropertyValidator<String> {
        @Override
        public String validateChange(String property, String newValue, String oldValue,
                MVWorld object) throws ChangeDeniedException {
            if (!plugin.getMVWorldManager().isMVWorld(newValue))
                throw new ChangeDeniedException();
            return super.validateChange(property, newValue, oldValue, object);
        }
    }

    /**
     * Serializor for the time-property.
     */
    private static final class TimePropertySerializor implements Serializor<Long, String> {
        // BEGIN CHECKSTYLE-SUPPRESSION: MagicNumberCheck
        private static final String TIME_REGEX = "(\\d\\d?):?(\\d\\d)(a|p)?m?";
        private static final  Map<String, String> TIME_ALIASES;
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

        @Override
        public String serialize(Long from) {
            // I'm tired, so they get time in 24 hour for now.
            // Someone else can add 12 hr format if they want :P

            int hours = (int) ((from / 1000 + 8) % 24);
            int minutes = (int) (60 * (from % 1000) / 1000);

            return String.format("%d:%02d", hours, minutes);
        }

        @Override
        public Long deserialize(String serialized, Class<Long> wanted) throws IllegalPropertyValueException {
            if (TIME_ALIASES.containsKey(serialized.toLowerCase())) {
                serialized = TIME_ALIASES.get(serialized.toLowerCase());
            }
            // Regex that extracts a time in the following formats:
            // 11:11pm, 11:11, 23:11, 1111, 1111p, and the aliases at the top of this file.
            Pattern pattern = Pattern.compile(TIME_REGEX, Pattern.CASE_INSENSITIVE);
            Matcher matcher = pattern.matcher(serialized);
            matcher.find();
            int hour = 0;
            double minute = 0;
            int count = matcher.groupCount();
            if (count >= 2) {
                hour = Integer.parseInt(matcher.group(1));
                minute = Integer.parseInt(matcher.group(2));
            }
            // If there were 4 matches (all, hour, min, am/pm)
            if (count == 4) {
                // We want 24 hour time for calcs, but if they
                // added a p[m], turn it into a 24 hr one.
                if (matcher.group(3).equals("p")) {
                    hour += 12;
                }
            }
            // Translate 24th hour to 0th hour.
            if (hour == 24) {
                hour = 0;
            }
            // Clamp the hour
            if (hour > 23 || hour < 0) {
                throw new IllegalPropertyValueException("Illegal hour!");
            }
            // Clamp the minute
            if (minute > 59 || minute < 0) {
                throw new IllegalPropertyValueException("Illegal minute!");
            }
            // 60 seconds in a minute, time needs to be in hrs * 1000, per
            // the bukkit docs.
            double totaltime = (hour + (minute / 60.0)) * 1000;
            // Somehow there's an 8 hour offset...
            totaltime -= 8000;
            if (totaltime < 0) {
                totaltime = 24000 + totaltime;
            }

            return (long) totaltime;
        }
        // END CHECKSTYLE-SUPPRESSION: MagicNumberCheck
    }

    /**
     * Used to apply the allowWeather-property.
     */
    private final class AllowWeatherPropertyValidator extends WorldPropertyValidator<Boolean> {
        @Override
        public Boolean validateChange(String property, Boolean newValue, Boolean oldValue,
                MVWorld object) throws ChangeDeniedException {
            if (!newValue) {
                world.get().setStorm(false);
                world.get().setThundering(false);
            }
            return super.validateChange(property, newValue, oldValue, object);
        }
    }

    /**
     * Used to apply the spawning-property.
     */
    private final class SpawningPropertyValidator extends WorldPropertyValidator<Boolean> {
        @Override
        public Boolean validateChange(String property, Boolean newValue, Boolean oldValue,
                MVWorld object) throws ChangeDeniedException {
            boolean allowMonsters, allowAnimals;
            if (getAnimalList().isEmpty()) {
                allowAnimals = canAnimalsSpawn();
            } else {
                allowAnimals = true;
            }
            if (getMonsterList().isEmpty()) {
                allowMonsters = canMonstersSpawn();
            } else {
                allowMonsters = true;
            }
            world.get().setSpawnFlags(allowMonsters, allowAnimals);
            plugin.getMVWorldManager().getTheWorldPurger().purgeWorld(MVWorld.this);
            return super.validateChange(property, newValue, oldValue, object);
        }
    }

    /**
     * Serializor for the difficulty-property.
     */
    private static final class DifficultyPropertySerializor implements Serializor<Difficulty, String> {
        @Override
        public String serialize(Difficulty from) {
            return from.toString();
        }

        @Override
        public Difficulty deserialize(String serialized, Class<Difficulty> wanted) throws IllegalPropertyValueException {
            try {
                return Difficulty.getByValue(Integer.parseInt(serialized));
            } catch (Exception e) {
            }
            try {
                return Difficulty.valueOf(serialized.toUpperCase());
            } catch (Exception e) {
            }
            throw new IllegalPropertyValueException();
        }
    }

    /**
     * Serializor for the gameMode-property.
     */
    private static final class GameModePropertySerializor implements Serializor<GameMode, String> {
        @Override
        public String serialize(GameMode from) {
            return from.toString();
        }

        @Override
        public GameMode deserialize(String serialized, Class<GameMode> wanted) throws IllegalPropertyValueException {
            try {
                return GameMode.getByValue(Integer.parseInt(serialized));
            } catch (NumberFormatException nfe) {
            }
            try {
                return GameMode.valueOf(serialized.toUpperCase());
            } catch (Exception e) {
            }
            throw new IllegalPropertyValueException();
        }
    }


    /**
     * Used to apply the gameMode-property.
     */
    private final class GameModePropertyValidator extends WorldPropertyValidator<GameMode> {
        @Override
        public GameMode validateChange(String property, GameMode newValue, GameMode oldValue,
                MVWorld object) throws ChangeDeniedException {
            for (Player p : plugin.getServer().getWorld(getName()).getPlayers()) {
                plugin.log(Level.FINER, String.format("Setting %s's GameMode to %s",
                        p.getName(), newValue.toString()));
                plugin.getPlayerListener().handleGameMode(p, MVWorld.this);
            }
            return super.validateChange(property, newValue, oldValue, object);
        }
    }

    /**
     * Validator for the spawnLocation-property.
     */
    private final class SpawnLocationPropertyValidator extends WorldPropertyValidator<Location> {
        @Override
        public Location validateChange(String property, Location newValue, Location oldValue,
                MVWorld object) throws ChangeDeniedException {
            if (newValue == null)
                throw new ChangeDeniedException();
            if (adjustSpawn) {
                BlockSafety bs = plugin.getBlockSafety();
                // verify that the location is safe
                if (!bs.playerCanSpawnHereSafely(newValue)) {
                    // it's not ==> find a better one!
                    plugin.log(Level.WARNING, String.format("Somebody tried to set the spawn location for '%s' to an unsafe value! Adjusting...", getAlias()));
                    plugin.log(Level.WARNING, "Old Location: " + plugin.getLocationManipulation().strCoordsRaw(oldValue));
                    plugin.log(Level.WARNING, "New (unsafe) Location: " + plugin.getLocationManipulation().strCoordsRaw(newValue));
                    SafeTTeleporter teleporter = plugin.getSafeTTeleporter();
                    newValue = teleporter.getSafeLocation(newValue, SPAWN_LOCATION_SEARCH_TOLERANCE, SPAWN_LOCATION_SEARCH_RADIUS);
                    if (newValue == null) {
                        plugin.log(Level.WARNING, "Couldn't fix the location. I have to abort the spawn location-change :/");
                        throw new ChangeDeniedException();
                    }
                    plugin.log(Level.WARNING, "New (safe) Location: " + plugin.getLocationManipulation().strCoordsRaw(newValue));
                }
            }
            return super.validateChange(property, newValue, oldValue, object);
        }
    }

    /**
     * Serializor for the color-property.
     */
    private static final class EnumPropertySerializor<T extends Enum<T>> implements Serializor<T, String> {
        @Override
        public String serialize(T from) {
            return from.toString();
        }

        @Override
        public T deserialize(String serialized, Class<T> wanted) throws IllegalPropertyValueException {
            try {
                return Enum.valueOf(wanted, serialized.toUpperCase());
            } catch (IllegalArgumentException e) {
                throw new IllegalPropertyValueException(e);
            }
        }
    }

    // --------------------------------------------------------------
    // Begin properties
    @Property(description = "Sorry, 'hidden' must either be: true or false.")
    private boolean hidden;
    @Property(description = "Alias must be a valid string.")
    private String alias;
    @Property(serializor = EnumPropertySerializor.class, description = "Sorry, 'color' must be a valid color-name.")
    private EnglishChatColor color;
    @Property(description = "Sorry, 'pvp' must either be: true or false.")
    private VirtualProperty<Boolean> pvp = new VirtualProperty<Boolean>() {
        @Override
        public void set(Boolean newValue) {
            world.get().setPVP(newValue);
        }

        @Override
        public Boolean get() {
            return world.get().getPVP();
        }
    };
    @Property(validator = ScalePropertyValidator.class, description = "Scale must be a positive double value. ex: 2.3")
    private double scale;
    @Property(validator = RespawnWorldPropertyValidator.class, description = "You must set this to the NAME not alias of a world.")
    private String respawnWorld;
    @Property(validator = AllowWeatherPropertyValidator.class, description = "Sorry, this must either be: true or false.")
    private boolean allowWeather;
    @Property(serializor = DifficultyPropertySerializor.class, description = "Difficulty must be set as one of the following: peaceful easy normal hard")
    private VirtualProperty<Difficulty> difficulty = new VirtualProperty<Difficulty>() {
        @Override
        public void set(Difficulty newValue) {
            world.get().setDifficulty(newValue);
        }

        @Override
        public Difficulty get() {
            return world.get().getDifficulty();
        }
    };
    @Property(validator = SpawningPropertyValidator.class, description = "Sorry, 'animals' must either be: true or false.")
    private SpawnSettings spawning;
    @Property
    private EntryFee entryfee;
    @Property(description = "Sorry, 'hunger' must either be: true or false.")
    private boolean hunger;
    @Property(description = "Sorry, 'autoheal' must either be: true or false.")
    private boolean autoHeal;
    @Property(description = "Sorry, 'adjustspawn' must either be: true or false.")
    private boolean adjustSpawn;
    @Property(serializor = EnumPropertySerializor.class, description = "Allow portal forming must be NONE, ALL, NETHER or END.")
    private AllowedPortalType portalForm;
    @Property(serializor = GameModePropertySerializor.class, validator = GameModePropertyValidator.class,
            description = "GameMode must be set as one of the following: survival creative")
    private GameMode gameMode;
    @Property(description = "Sorry, this must either be: true or false.")
    private VirtualProperty<Boolean> keepSpawnInMemory = new VirtualProperty<Boolean>() {
        @Override
        public void set(Boolean newValue) {
            world.get().setKeepSpawnInMemory(newValue);
        }

        @Override
        public Boolean get() {
            return world.get().getKeepSpawnInMemory();
        }
    };
    @Property
    private SpawnLocation spawnLocation;
    @Property(validator = SpawnLocationPropertyValidator.class,
            description = "There is no help available for this variable. Go bug Rigby90 about it.")
    private VirtualProperty<Location> spawn = new VirtualProperty<Location>() {
        @Override
        public void set(Location newValue) {
            world.get().setSpawnLocation(newValue.getBlockX(), newValue.getBlockY(), newValue.getBlockZ());
            spawnLocation = new SpawnLocation(newValue);
        }

        @Override
        public Location get() {
            spawnLocation.setWorld(getCBWorld());
            // basically, everybody should accept our "SpawnLocation", right?
            // so just returning it should be fine
            return spawnLocation;
        }
    };
    @Property(description = "Set this to false ONLY if you don't want this world to load itself on server restart.")
    private boolean autoLoad;
    @Property(description = "If a player dies in this world, shoudld they go to their bed?")
    private boolean bedRespawn;
    @Property
    private List<String> worldBlacklist;
    @SuppressWarnings("unused") // it IS used!
    @Property(serializor = TimePropertySerializor.class, description = "Set the time to whatever you want! (Will NOT freeze time)")
    private VirtualProperty<Long> time = new VirtualProperty<Long>() {
        @Override
        public void set(Long newValue) {
            world.get().setTime(newValue);
        }

        @Override
        public Long get() {
            return world.get().getTime();
        }
    };
    @Property
    private Environment environment;
    @Property
    private long seed;
    @Property
    private String generator;
    // End of properties
    // --------------------------------------------------------------

    private Permission permission;
    private Permission exempt;
    private Permission ignoreperm;

    public MVWorld(boolean fixSpawn) {
        super();
        if (!fixSpawn) {
            this.adjustSpawn = false;
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void copyValues(SerializationConfig other) {
        super.copyValues(other);
    }

    /**
     * Sets the CB-World.
     * <p>
     * This is used to set some values after deserialization.
     * @param cbWorld The new world.
     * @param thePlugin The reference to the plugin.
     */
    public void init(World cbWorld, MultiverseCore thePlugin) {
        this.plugin = thePlugin;

        // Weak reference so the CB-World can be unloaded even if this object still exists!
        this.world = new WeakReference<World>(cbWorld);
        this.environment = cbWorld.getEnvironment();
        this.seed = cbWorld.getSeed();
        this.name = cbWorld.getName();
        if (this.spawnLocation == null)
            this.spawnLocation = new SpawnLocation(readSpawnFromWorld(cbWorld));

        this.initPerms();
    }

    /**
     * Initializes permissions.
     */
    private void initPerms() {
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
    }

    private Location readSpawnFromWorld(World w) {
        Location location = w.getSpawnLocation();
        // Set the worldspawn to our configspawn
        BlockSafety bs = this.plugin.getBlockSafety();
        // Verify that location was safe
        if (!bs.playerCanSpawnHereSafely(location)) {
            if (!this.getAdjustSpawn()) {
                this.plugin.log(Level.FINE, "Spawn location from world.dat file was unsafe!!");
                this.plugin.log(Level.FINE, "NOT adjusting spawn for '" + this.getAlias() + "' because you told me not to.");
                this.plugin.log(Level.FINE, "To turn on spawn adjustment for this world simply type:");
                this.plugin.log(Level.FINE, "/mvm set adjustspawn true " + this.getAlias());
                return location;
            }
            // If it's not, find a better one.
            SafeTTeleporter teleporter = this.plugin.getSafeTTeleporter();
            this.plugin.log(Level.WARNING, "Spawn location from world.dat file was unsafe. Adjusting...");
            this.plugin.log(Level.WARNING, "Original Location: " + plugin.getLocationManipulation().strCoordsRaw(location));
            Location newSpawn = teleporter.getSafeLocation(location,
                    SPAWN_LOCATION_SEARCH_TOLERANCE, SPAWN_LOCATION_SEARCH_RADIUS);
            // I think we could also do this, as I think this is what Notch does.
            // Not sure how it will work in the nether...
            //Location newSpawn = this.spawnLocation.getWorld().getHighestBlockAt(this.spawnLocation).getLocation();
            if (newSpawn != null) {
                this.plugin.log(Level.INFO, String.format("New Spawn for '%s' is located at: %s",
                        this.getName(), plugin.getLocationManipulation().locationToString(newSpawn)));
                return newSpawn;
            } else {
                // If it's a standard end world, let's check in a better place:
                Location newerSpawn;
                newerSpawn = bs.getTopBlock(new Location(w, 0, 0, 0));
                if (newerSpawn != null) {
                    this.plugin.log(Level.INFO, String.format("New Spawn for '%s' is located at: %s",
                            this.getName(), plugin.getLocationManipulation().locationToString(newerSpawn)));
                    return newerSpawn;
                } else {
                    this.plugin.log(Level.SEVERE, "Safe spawn NOT found!!!");
                }
            }
        }
        return location;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void setDefaults() {
        this.hidden = false;
        this.alias = new String();
        this.color = EnglishChatColor.WHITE;
        this.scale = getDefaultScale(environment);
        this.respawnWorld = new String();
        this.allowWeather = true;
        this.spawning = new SpawnSettings();
        this.entryfee = new EntryFee();
        this.hunger = true;
        this.autoHeal = true;
        this.adjustSpawn = true;
        this.portalForm = AllowedPortalType.ALL;
        this.gameMode = GameMode.SURVIVAL;
        this.spawnLocation = (world != null) ? new SpawnLocation(world.get().getSpawnLocation()) : null;
        this.autoLoad = true;
        this.bedRespawn = true;
        this.worldBlacklist = new ArrayList<String>();
        this.generator = null;
    }

    /**
     * getAliases().
     * @return The alias-map.
     * @see SerializationConfig
     */
    protected static Map<String, String> getAliases() {
        Map<String, String> aliases = new HashMap<String, String>();
        aliases.put("curr", "currency");
        aliases.put("scaling", "scale");
        aliases.put("aliascolor", "color");
        aliases.put("heal", "autoHeal");
        aliases.put("storm", "allowWeather");
        aliases.put("weather", "allowWeather");
        aliases.put("spawnmemory", "keepSpawnInMemory");
        aliases.put("memory", "keepSpawnInMemory");
        aliases.put("mode", "gameMode");
        aliases.put("diff", "difficulty");
        aliases.put("spawnlocation", "spawn");
        return aliases;
    }

    private static double getDefaultScale(Environment environment) {
        if (environment == Environment.NETHER) {
            return 8.0; // SUPPRESS CHECKSTYLE: MagicNumberCheck
        }
        return 1.0;
    }

    private void addToUpperLists(Permission perm) {
        Permission all = this.plugin.getServer().getPluginManager().getPermission("multiverse.*");
        Permission allWorlds = this.plugin.getServer().getPluginManager().getPermission("multiverse.access.*");
        Permission allExemption = this.plugin.getServer().getPluginManager().getPermission("multiverse.exempt.*");

        if (allWorlds == null) {
            allWorlds = new Permission("multiverse.access.*");
            this.plugin.getServer().getPluginManager().addPermission(allWorlds);
        }
        allWorlds.getChildren().put(perm.getName(), true);
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
    public World getCBWorld() {
        return this.world.get();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getColoredWorldString() {
        if (alias.length() == 0) {
            alias = this.getName();
        }
        if ((color == null) || (color.getColor() == null)) {
            this.setPropertyValueUnchecked("color", EnglishChatColor.WHITE);
        }
        return color.getColor() + alias + ChatColor.WHITE;
    }

    /**
     * {@inheritDoc}
     *
     * @deprecated This is deprecated.
     */
    @Override
    @Deprecated
    public boolean clearList(String property) {
        return clearVariable(property);
    }

    /**
     * {@inheritDoc}
     *
     * @deprecated This is deprecated.
     */
    @Override
    @Deprecated
    public boolean clearVariable(String property) {
        List<String> list = getOldAndEvilList(property);
        if (list == null)
            return false;
        list.clear();
        return true;
    }

    /**
     * {@inheritDoc}
     *
     * @deprecated This is deprecated.
     */
    @Override
    @Deprecated
    public boolean addToVariable(String property, String value) {
        List<String> list = getOldAndEvilList(property);
        if (list == null)
            return false;
        list.add(value);
        return true;
    }

    /**
     * {@inheritDoc}
     *
     * @deprecated This is deprecated.
     */
    @Override
    @Deprecated
    public boolean removeFromVariable(String property, String value) {
        List<String> list = getOldAndEvilList(property);
        if (list == null)
            return false;
        list.remove(value);
        return true;
    }

    /**
     * @deprecated This is deprecated.
     */
    @Deprecated
    private List<String> getOldAndEvilList(String property) {
        if (property.equalsIgnoreCase("worldblacklist"))
            return this.worldBlacklist;
        else if (property.equalsIgnoreCase("animals"))
            return this.spawning.getAnimalSettings().getExceptions();
        else if (property.equalsIgnoreCase("monsters"))
            return this.spawning.getMonsterSettings().getExceptions();
        return null;
    }

    /**
     * {@inheritDoc}
     *
     * @deprecated This is deprecated.
     */
    @Override
    @Deprecated
    public <T> com.onarandombox.MultiverseCore.configuration.MVConfigProperty<T> getProperty(String property,
            Class<T> expected) throws PropertyDoesNotExistException {
        throw new UnsupportedOperationException("'MVConfigProperty<T> getProperty(String,Class<T>)' is no longer supported!");
    }

    /**
     * {@inheritDoc}
     *
     * @deprecated This is deprecated.
     */
    @Override
    @Deprecated
    public boolean setProperty(String name, String value, CommandSender sender) throws PropertyDoesNotExistException {
        return this.setPropertyValue(name, value);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getPropertyValue(String property) throws PropertyDoesNotExistException {
        try {
            return this.getProperty(property, true);
        } catch (NoSuchPropertyException e) {
            throw new PropertyDoesNotExistException(property, e);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean setPropertyValue(String property, String value) throws PropertyDoesNotExistException {
        try {
            return this.setProperty(property, value, true);
        } catch (NoSuchPropertyException e) {
            throw new PropertyDoesNotExistException(property, e);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getPropertyHelp(String property) throws PropertyDoesNotExistException {
        try {
            return this.getPropertyDescription(property, true);
        } catch (NoSuchPropertyException e) {
            throw new PropertyDoesNotExistException(property, e);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public WorldType getWorldType() {
        // This variable is not settable in-game, therefore does not get a property.
        return world.get().getWorldType();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Environment getEnvironment() {
        return this.environment;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setEnvironment(Environment environment) {
        this.setPropertyValueUnchecked("environment", environment);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public long getSeed() {
        return this.seed;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setSeed(long seed) {
        this.setPropertyValueUnchecked("seed", seed);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getGenerator() {
        return this.generator;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setGenerator(String generator) {
        this.setPropertyValueUnchecked("generator", generator);
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
        if (this.alias == null || this.alias.length() == 0) {
            return this.name;
        }
        return this.alias;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setAlias(String alias) {
        this.setPropertyValueUnchecked("alias", alias);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean canAnimalsSpawn() {
        return this.spawning.getAnimalSettings().doSpawn();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setAllowAnimalSpawn(boolean animals) {
        this.setPropertyValueUnchecked("spawning.animals.spawn", animals);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<String> getAnimalList() {
        // These don't fire events at the moment. Should they?
        return this.spawning.getAnimalSettings().getExceptions();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean canMonstersSpawn() {
        return this.spawning.getMonsterSettings().doSpawn();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setAllowMonsterSpawn(boolean monsters) {
        this.setPropertyValueUnchecked("spawning.monsters.spawn", monsters);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<String> getMonsterList() {
        // These don't fire events at the moment. Should they?
        return this.spawning.getMonsterSettings().getExceptions();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isPVPEnabled() {
        return this.pvp.get();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setPVPMode(boolean pvp) {
        this.setPropertyValueUnchecked("pvp", pvp);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isHidden() {
        return this.hidden;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setHidden(boolean hidden) {
        this.setPropertyValueUnchecked("hidden", hidden);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<String> getWorldBlacklist() {
        return this.worldBlacklist;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public double getScaling() {
        return this.scale;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean setScaling(double scaling) {
        return this.setPropertyValueUnchecked("scale", scaling);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean setColor(String aliasColor) {
        return this.setPropertyUnchecked("color", aliasColor);
    }

    /**
     * {@inheritDoc}
     *
     * @deprecated This is deprecated.
     */
    @Override
    @Deprecated
    public boolean isValidAliasColor(String aliasColor) {
        return (EnglishChatColor.fromString(aliasColor) != null);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ChatColor getColor() {
        return this.color.getColor();
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
        return this.plugin.getServer().getWorld(respawnWorld);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean setRespawnToWorld(String respawnToWorld) {
        if (!this.plugin.getMVWorldManager().isMVWorld(respawnToWorld)) return false;
        return this.setPropertyValueUnchecked("respawnWorld", respawnToWorld);
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
        return this.entryfee.getCurrency();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setCurrency(int currency) {
        this.setPropertyValueUnchecked("entryfee.currency", currency);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public double getPrice() {
        return this.entryfee.getAmount();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setPrice(double price) {
        this.setPropertyValueUnchecked("entryfee.amount", price);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Permission getExemptPermission() {
        return this.exempt;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean setGameMode(String mode) {
        return this.setPropertyUnchecked("gameMode", mode);
    }

    @Override
    public boolean setGameMode(GameMode mode) {
        return this.setPropertyValueUnchecked("gameMode", mode);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public GameMode getGameMode() {
        return this.gameMode;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setEnableWeather(boolean weather) {
        this.setPropertyValueUnchecked("allowWeather", weather);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isWeatherEnabled() {
        return this.allowWeather;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isKeepingSpawnInMemory() {
        return this.keepSpawnInMemory.get();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setKeepSpawnInMemory(boolean value) {
        this.setPropertyValueUnchecked("keepSpawnInMemory", value);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean getHunger() {
        return this.hunger;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setHunger(boolean hunger) {
        this.setPropertyValueUnchecked("hunger", hunger);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Location getSpawnLocation() {
        return this.spawn.get();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setSpawnLocation(Location l) {
        this.setPropertyValueUnchecked("spawn", l);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Difficulty getDifficulty() {
        return this.difficulty.get();
    }

    /**
     * {@inheritDoc}
     *
     * @deprecated This is deprecated.
     */
    @Override
    @Deprecated
    public boolean setDifficulty(String difficulty) {
        return this.setPropertyUnchecked("difficulty", difficulty);
    }

    @Override
    public boolean setDifficulty(Difficulty difficulty) {
        return this.setPropertyValueUnchecked("difficulty", difficulty);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean getAutoHeal() {
        return this.autoHeal;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setAutoHeal(boolean heal) {
        this.setPropertyValueUnchecked("autoHeal", heal);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setAdjustSpawn(boolean adjust) {
        this.setPropertyValueUnchecked("adjustSpawn", adjust);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean getAdjustSpawn() {
        return this.adjustSpawn;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setAutoLoad(boolean load) {
        this.setPropertyValueUnchecked("autoLoad", load);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean getAutoLoad() {
        return this.autoLoad;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setBedRespawn(boolean respawn) {
        this.setPropertyValueUnchecked("bedRespawn", respawn);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean getBedRespawn() {
        return this.bedRespawn;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getAllPropertyNames() {
        ChatColor myColor = ChatColor.AQUA;
        StringBuilder result = new StringBuilder();
        Map<String, Object> serialized = this.serialize();
        for (String key : serialized.keySet()) {
            result.append(myColor).append(key).append(' ');
            myColor = (myColor == ChatColor.AQUA) ? ChatColor.GOLD : ChatColor.AQUA;
        }
        return result.toString();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getTime() {
        return this.getPropertyUnchecked("time");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean setTime(String timeAsString) {
        return this.setPropertyUnchecked("time", timeAsString);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public AllowedPortalType getAllowedPortals() {
        return portalForm;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void allowPortalMaking(AllowedPortalType portalType) {
        this.setPropertyValueUnchecked("portalForm", portalType);
    }

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
