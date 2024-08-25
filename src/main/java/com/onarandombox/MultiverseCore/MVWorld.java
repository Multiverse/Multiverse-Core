/******************************************************************************
 * Multiverse 2 Copyright (c) the Multiverse Team 2011.                       *
 * Multiverse 2 is licensed under the BSD License.                            *
 * For more information please check the README.md file included              *
 * with this project.                                                         *
 ******************************************************************************/

package com.onarandombox.MultiverseCore;

import com.dumptruckman.minecraft.util.Logging;
import com.onarandombox.MultiverseCore.api.BlockSafety;
import com.onarandombox.MultiverseCore.api.MultiverseWorld;
import com.onarandombox.MultiverseCore.api.SafeTTeleporter;
import com.onarandombox.MultiverseCore.configuration.SpawnLocation;
import com.onarandombox.MultiverseCore.configuration.SpawnSettings;
import com.onarandombox.MultiverseCore.configuration.WorldPropertyValidator;
import com.onarandombox.MultiverseCore.enums.AllowedPortalType;
import com.onarandombox.MultiverseCore.enums.EnglishChatColor;
import com.onarandombox.MultiverseCore.exceptions.PropertyDoesNotExistException;
import me.main__.util.SerializationConfig.ChangeDeniedException;
import me.main__.util.SerializationConfig.NoSuchPropertyException;
import me.main__.util.SerializationConfig.VirtualProperty;
import org.bukkit.ChatColor;
import org.bukkit.Difficulty;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.World.Environment;
import org.bukkit.WorldType;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.serialization.SerializableAs;
import org.bukkit.entity.Player;
import org.bukkit.permissions.Permission;
import org.bukkit.permissions.PermissionDefault;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.Nullable;
import org.json.simple.JSONObject;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.logging.Level;

/**
 * The implementation of a Multiverse handled world.
 */
public class MVWorld implements MultiverseWorld {
    private static final int SPAWN_LOCATION_SEARCH_TOLERANCE = 16;
    private static final int SPAWN_LOCATION_SEARCH_RADIUS = 16;

    private final MultiverseCore plugin; // Hold the Plugin Instance.
    private final String name; // The Worlds Name, EG its folder name.
    private final UUID worldUID;
    private final WorldProperties props;

    public MVWorld(MultiverseCore plugin, World world, WorldProperties properties) {
        this(plugin, world, properties, true);
    }

    /*
     * We have to use setCBWorld(), setPlugin() and initPerms() to prepare this object for use.
     */
    public MVWorld(MultiverseCore plugin, World world, WorldProperties properties, boolean fixSpawn) {
        this.plugin = plugin;
        this.name = world.getName();
        this.worldUID = world.getUID();
        this.props = properties;

        setupProperties();

        if (!fixSpawn) {
            props.setAdjustSpawn(false);
        }

        // Setup spawn separately so we can use the validator with the world spawn value..
        final SpawnLocationPropertyValidator spawnValidator = new SpawnLocationPropertyValidator();
        this.props.setValidator("spawn", spawnValidator);
        this.props.spawnLocation.setWorld(world);
        if (this.props.spawnLocation instanceof NullLocation) {
            final SpawnLocation newLoc = new SpawnLocation(readSpawnFromWorld(world));
            this.props.spawnLocation = newLoc;
            world.setSpawnLocation(newLoc.getBlockX(), newLoc.getBlockY(), newLoc.getBlockZ());
        }

        this.props.environment = world.getEnvironment();
        this.props.seed = world.getSeed();

        this.initPerms();

        this.props.flushChanges();

        validateProperties();
    }

    private void setupProperties() {
        this.props.setMVWorld(this);
        this.props.pvp = new VirtualProperty<Boolean>() {
            @Override
            public void set(Boolean newValue) {
                final World world = getCBWorld();
                if (world != null) {
                    world.setPVP(newValue);
                }
            }

            @Override
            public Boolean get() {
                final World world = getCBWorld();
                return world != null ? world.getPVP() : null;
            }
        };

        this.props.difficulty = new VirtualProperty<Difficulty>() {
            @Override
            public void set(Difficulty newValue) {
                final World world = getCBWorld();
                if (world != null) {
                    world.setDifficulty(newValue);
                }
            }

            @Override
            public Difficulty get() {
                final World world = getCBWorld();
                return world != null ? world.getDifficulty() : null;
            }
        };

        this.props.keepSpawnInMemory = new VirtualProperty<Boolean>() {
            @Override
            public void set(Boolean newValue) {
                final World world = getCBWorld();
                if (world != null) {
                    world.setKeepSpawnInMemory(newValue);
                }
            }

            @Override
            public Boolean get() {
                final World world = getCBWorld();
                return world != null ? world.getKeepSpawnInMemory() : null;
            }
        };

        this.props.spawn = new VirtualProperty<Location>() {
            @Override
            public void set(Location newValue) {
                if (getCBWorld() != null)
                    getCBWorld().setSpawnLocation(newValue.getBlockX(), newValue.getBlockY(), newValue.getBlockZ());

                props.spawnLocation = new SpawnLocation(newValue);
            }

            @Override
            public Location get() {
                props.spawnLocation.setWorld(getCBWorld());
                // basically, everybody should accept our "SpawnLocation", right?
                // so just returning it should be fine
                return props.spawnLocation;
            }
        };

        this.props.time = new VirtualProperty<Long>() {
            @Override
            public void set(Long newValue) {
                final World world = getCBWorld();
                if (world != null) {
                    world.setTime(newValue);
                }
            }

            @Override
            public Long get() {
                final World world = getCBWorld();
                return world != null ? world.getTime() : null;
            }
        };

        this.props.setValidator("scale", new ScalePropertyValidator());
        this.props.setValidator("respawnWorld", new RespawnWorldPropertyValidator());
        this.props.setValidator("allowWeather", new AllowWeatherPropertyValidator());
        this.props.setValidator("spawning", new SpawningPropertyValidator());
        this.props.setValidator("gameMode", new GameModePropertyValidator());

        //this.props.validate();
    }

    /**
     * This method is here to provide a stopgap until the add/remove/clear methods are implemented with
     * SerializationConfig.
     */
    public void validateEntitySpawns() {
        setAllowAnimalSpawn(canAnimalsSpawn());
        setAllowMonsterSpawn(canMonstersSpawn());
    }

    private void validateProperties() {
        setPVPMode(isPVPEnabled());
        setDifficulty(getDifficulty());
        setKeepSpawnInMemory(isKeepingSpawnInMemory());
        setScaling(getScaling());
        setRespawnToWorld(this.props.getRespawnToWorld());
        validateEntitySpawns();
        setGameMode(getGameMode());
    }

    /**
     * Validates the scale-property.
     */
    private final class ScalePropertyValidator extends WorldPropertyValidator<Double> {
        @Override
        public Double validateChange(String property, Double newValue, Double oldValue,
                MVWorld object) throws ChangeDeniedException {
            if (newValue <= 0) {
                Logging.fine("Someone tried to set a scale <= 0, aborting!");
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
            if (!newValue.isEmpty() && !plugin.getMVWorldManager().isMVWorld(newValue))
                throw new ChangeDeniedException();
            return super.validateChange(property, newValue, oldValue, object);
        }
    }



    /**
     * Used to apply the allowWeather-property.
     */
    private final class AllowWeatherPropertyValidator extends WorldPropertyValidator<Boolean> {
        @Override
        public Boolean validateChange(String property, Boolean newValue, Boolean oldValue,
                MVWorld object) throws ChangeDeniedException {
            if (!newValue) {
                final World world = getCBWorld();
                if (world != null) {
                    world.setStorm(false);
                    world.setThundering(false);
                }
            }
            return super.validateChange(property, newValue, oldValue, object);
        }
    }

    /**
     * Used to apply the spawning-property.
     */
    private final class SpawningPropertyValidator extends WorldPropertyValidator<SpawnSettings> {
        @Override
        public SpawnSettings validateChange(String property, SpawnSettings newValue, SpawnSettings oldValue,
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
            final World world = getCBWorld();
            if (world != null) {
                if (MVWorld.this.props.getAnimalSpawnRate() != -1) {
                    world.setTicksPerAnimalSpawns(MVWorld.this.props.getAnimalSpawnRate());
                }
                if (MVWorld.this.props.getMonsterSpawnRate() != -1) {
                    world.setTicksPerMonsterSpawns(MVWorld.this.props.getMonsterSpawnRate());
                }
                world.setSpawnFlags(allowMonsters, allowAnimals);
            }
            if (MultiverseCoreConfiguration.getInstance().isAutoPurgeEnabled()) {
                plugin.getMVWorldManager().getTheWorldPurger().purgeWorld(MVWorld.this);
            }
            return super.validateChange(property, newValue, oldValue, object);
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
                Logging.finer(String.format("Setting %s's GameMode to %s",
                        p.getName(), newValue.toString()));
                plugin.getPlayerListener().handleGameModeAndFlight(p, MVWorld.this);
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
            if (props.getAdjustSpawn()) {
                BlockSafety bs = plugin.getBlockSafety();
                // verify that the location is safe
                if (!bs.playerCanSpawnHereSafely(newValue)) {
                    // it's not ==> find a better one!
                    Logging.warning(String.format("Somebody tried to set the spawn location for '%s' to an unsafe value! Adjusting...", getAlias()));
                    Logging.warning("Old Location: " + plugin.getLocationManipulation().strCoordsRaw(oldValue));
                    Logging.warning("New (unsafe) Location: " + plugin.getLocationManipulation().strCoordsRaw(newValue));
                    SafeTTeleporter teleporter = plugin.getSafeTTeleporter();
                    newValue = teleporter.getSafeLocation(newValue, SPAWN_LOCATION_SEARCH_TOLERANCE, SPAWN_LOCATION_SEARCH_RADIUS);
                    if (newValue == null) {
                        Logging.warning("Couldn't fix the location. I have to abort the spawn location-change :/");
                        throw new ChangeDeniedException();
                    }
                    Logging.warning("New (safe) Location: " + plugin.getLocationManipulation().strCoordsRaw(newValue));
                }
            }
            return super.validateChange(property, newValue, oldValue, object);
        }
    }

    private Permission permission;
    private Permission exempt;
    private Permission ignoregamemodeperm;
    private Permission ignoreflyperm;
    private Permission limitbypassperm;

    /**
     * Null-location.
     */
    @SerializableAs("MVNullLocation (It's a bug if you see this in your config file)")
    public static final class NullLocation extends SpawnLocation {
        public NullLocation() {
            super(0, -1, 0);
        }

        @Override
        public Location clone() {
            throw new UnsupportedOperationException();
        };

        @Override
        public Map<String, Object> serialize() {
            return Collections.emptyMap();
        }

        /**
         * Let Bukkit be able to deserialize this.
         * @param args The map.
         * @return The deserialized object.
         */
        public static NullLocation deserialize(Map<String, Object> args) {
            return new NullLocation();
        }

        @Override
        public Vector toVector() {
            throw new UnsupportedOperationException();
        }

        @Override
        public int hashCode() {
            return -1;
        };

        @Override
        public String toString() {
            return "NULL LOCATION";
        };
    }

    /**
     * Initializes permissions.
     */
    private void initPerms() {
        this.permission = new Permission("multiverse.access." + this.getName(), "Allows access to " + this.getName(), PermissionDefault.OP);
        // This guy is special. He shouldn't be added to any parent perms.
        this.ignoregamemodeperm = new Permission("mv.bypass.gamemode." + this.getName(),
                "Allows players with this permission to ignore gamemode changes.", PermissionDefault.FALSE);
        this.ignoreflyperm = new Permission("mv.bypass.fly." + this.getName(),
                "Allows players with this permission to ignore fly changes.", PermissionDefault.FALSE);

        this.exempt = new Permission("multiverse.exempt." + this.getName(),
                "A player who has this does not pay to enter this world, or use any MV portals in it " + this.getName(), PermissionDefault.OP);

        this.limitbypassperm = new Permission("mv.bypass.playerlimit." + this.getName(),
                "A player who can enter this world regardless of wether its full", PermissionDefault.OP);
        try {
            this.plugin.getServer().getPluginManager().addPermission(this.permission);
            this.plugin.getServer().getPluginManager().addPermission(this.exempt);
            this.plugin.getServer().getPluginManager().addPermission(this.ignoregamemodeperm);
            this.plugin.getServer().getPluginManager().addPermission(this.ignoreflyperm);
            this.plugin.getServer().getPluginManager().addPermission(this.limitbypassperm);
            // Add the permission and exempt to parents.
            this.addToUpperLists(this.permission);

            // Add ignore to it's parent:
            this.ignoregamemodeperm.addParent("mv.bypass.gamemode.*", true);
            this.ignoreflyperm.addParent("mv.bypass.fly.*", true);
            // Add limit bypass to it's parent
            this.limitbypassperm.addParent("mv.bypass.playerlimit.*", true);
        } catch (IllegalArgumentException e) {
            Logging.finer("Permissions nodes were already added for " + this.name);
        }
    }

    private Location readSpawnFromWorld(World w) {
        Location location = w.getSpawnLocation();
        // Set the worldspawn to our configspawn
        BlockSafety bs = this.plugin.getBlockSafety();
        // Verify that location was safe
        if (!bs.playerCanSpawnHereSafely(location)) {
            if (!this.getAdjustSpawn()) {
                Logging.fine("Spawn location from world.dat file was unsafe!!");
                Logging.fine("NOT adjusting spawn for '" + this.getAlias() + "' because you told me not to.");
                Logging.fine("To turn on spawn adjustment for this world simply type:");
                Logging.fine("/mvm set adjustspawn true " + this.getAlias());
                return location;
            }
            // If it's not, find a better one.
            SafeTTeleporter teleporter = this.plugin.getSafeTTeleporter();
            Logging.warning("Spawn location from world.dat file was unsafe. Adjusting...");
            Logging.warning("Original Location: " + plugin.getLocationManipulation().strCoordsRaw(location));
            Location newSpawn = teleporter.getSafeLocation(location,
                    SPAWN_LOCATION_SEARCH_TOLERANCE, SPAWN_LOCATION_SEARCH_RADIUS);
            // I think we could also do this, as I think this is what Notch does.
            // Not sure how it will work in the nether...
            //Location newSpawn = this.spawnLocation.getWorld().getHighestBlockAt(this.spawnLocation).getLocation();
            if (newSpawn != null) {
                Logging.info("New Spawn for '%s' is located at: %s",
                        this.getName(), plugin.getLocationManipulation().locationToString(newSpawn));
                return newSpawn;
            } else {
                // If it's a standard end world, let's check in a better place:
                Location newerSpawn;
                newerSpawn = bs.getTopBlock(new Location(w, 0, 0, 0));
                if (newerSpawn != null) {
                    Logging.info("New Spawn for '%s' is located at: %s",
                            this.getName(), plugin.getLocationManipulation().locationToString(newerSpawn));
                    return newerSpawn;
                } else {
                    Logging.severe("Safe spawn NOT found!!!");
                }
            }
        }
        return location;
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
     * Copies all properties from another {@link MVWorld} object.
     * @param other The other world object.
     */
    public void copyValues(MVWorld other) {
        props.copyValues(other.props);
    }

    /**
     * Copies all properties from a {@link WorldProperties} object.
     * @param other The world properties object.
     */
    public void copyValues(WorldProperties other) {
        props.copyValues(other);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public World getCBWorld() {
        final World world = plugin.getServer().getWorld(worldUID);
        if (world == null) {
            throw new IllegalStateException("Lost reference to bukkit world '" + name + "'");
        }
        return world;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getColoredWorldString() {
        if (props.getAlias().length() == 0) {
            props.setAlias(this.getName());
        }

        if ((props.getColor() == null) || (props.getColor().getColor() == null)) {
            this.props.setColor(EnglishChatColor.WHITE);
        }

        StringBuilder nameBuilder = new StringBuilder().append(props.getColor().getColor());
        if (props.getStyle().getColor() != null)
            nameBuilder.append(props.getStyle().getColor());
        nameBuilder.append(props.getAlias()).append(ChatColor.WHITE).toString();

        return nameBuilder.toString();
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
        validateEntitySpawns();
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
        validateEntitySpawns();
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
        validateEntitySpawns();
        return true;
    }

    /**
     * @deprecated This is deprecated.
     */
    @Deprecated
    private List<String> getOldAndEvilList(String property) {
        if (property.equalsIgnoreCase("worldblacklist"))
            return this.props.getWorldBlacklist();
        else if (property.equalsIgnoreCase("animals"))
            return this.props.getAnimalList();
        else if (property.equalsIgnoreCase("monsters"))
            return this.props.getMonsterList();
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getPropertyValue(String property) throws PropertyDoesNotExistException {
        try {
            return this.props.getProperty(property, true);
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
            return this.props.setProperty(property, value, true);
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
            return this.props.getPropertyDescription(property, true);
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
        final World world = getCBWorld();
        return world != null ? world.getWorldType() : null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Environment getEnvironment() {
        return this.props.getEnvironment();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setEnvironment(Environment environment) {
        this.props.setEnvironment(environment);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public long getSeed() {
        return this.props.getSeed();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setSeed(long seed) {
        this.props.setSeed(seed);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getGenerator() {
        return this.props.getGenerator();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setGenerator(String generator) {
        this.props.setGenerator(generator);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getPlayerLimit() {
        return this.props.getPlayerLimit();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setPlayerLimit(int limit) {
        this.props.setPlayerLimit(limit);
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
        if (this.props.getAlias() == null || this.props.getAlias().length() == 0) {
            return this.name;
        }
        return this.props.getAlias();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setAlias(String alias) {
        this.props.setAlias(alias);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean canAnimalsSpawn() {
        return this.props.canAnimalsSpawn();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setAllowAnimalSpawn(boolean animals) {
        this.props.setAllowAnimalSpawn(animals);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<String> getAnimalList() {
        // These don't fire events at the moment. Should they?
        return this.props.getAnimalList();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean canMonstersSpawn() {
        return this.props.canMonstersSpawn();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setAllowMonsterSpawn(boolean monsters) {
        this.props.setAllowMonsterSpawn(monsters);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<String> getMonsterList() {
        // These don't fire events at the moment. Should they?
        return this.props.getMonsterList();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isPVPEnabled() {
        return this.props.isPVPEnabled();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setPVPMode(boolean pvp) {
        this.props.setPVPMode(pvp);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isHidden() {
        return this.props.isHidden();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setHidden(boolean hidden) {
        this.props.setHidden(hidden);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<String> getWorldBlacklist() {
        return this.props.getWorldBlacklist();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public double getScaling() {
        return this.props.getScaling();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean setScaling(double scaling) {
        return this.props.setScaling(scaling);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean setColor(String aliasColor) {
        return props.setColor(aliasColor);
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
        return this.props.getColor().getColor();
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
        return this.plugin.getServer().getWorld(props.getRespawnToWorld());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean setRespawnToWorld(String respawnToWorld) {
        if (!this.plugin.getMVWorldManager().isMVWorld(respawnToWorld)) return false;
        return this.props.setRespawnToWorld(respawnToWorld);
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
    public Material getCurrency() {
        return this.props.getCurrency();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setCurrency(@Nullable Material currency) {
        this.props.setCurrency(currency);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public double getPrice() {
        return this.props.getPrice();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setPrice(double price) {
        this.props.setPrice(price);
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
        return this.props.setGameMode(mode);
    }

    @Override
    public boolean setGameMode(GameMode mode) {
        return this.props.setGameMode(mode);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public GameMode getGameMode() {
        return this.props.getGameMode();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setEnableWeather(boolean weather) {
        this.props.setEnableWeather(weather);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isWeatherEnabled() {
        return this.props.isWeatherEnabled();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isKeepingSpawnInMemory() {
        return this.props.isKeepingSpawnInMemory();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setKeepSpawnInMemory(boolean value) {
        this.props.setKeepSpawnInMemory(value);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean getHunger() {
        return this.props.getHunger();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setHunger(boolean hunger) {
        this.props.setHunger(hunger);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Location getSpawnLocation() {
        return this.props.getSpawnLocation();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setSpawnLocation(Location l) {
        this.props.setSpawnLocation(l);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Difficulty getDifficulty() {
        return this.props.getDifficulty();
    }

    /**
     * {@inheritDoc}
     *
     * @deprecated This is deprecated.
     */
    @Override
    @Deprecated
    public boolean setDifficulty(String difficulty) {
        return this.props.setDifficulty(difficulty);
    }

    @Override
    public boolean setDifficulty(Difficulty difficulty) {
        return this.props.setDifficulty(difficulty);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean getAutoHeal() {
        return this.props.getAutoHeal();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setAutoHeal(boolean heal) {
        this.props.setAutoHeal(heal);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setAdjustSpawn(boolean adjust) {
        this.props.setAdjustSpawn(adjust);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean getAdjustSpawn() {
        return this.props.getAdjustSpawn();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setAutoLoad(boolean load) {
        this.props.setAutoLoad(load);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean getAutoLoad() {
        return this.props.getAutoLoad();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setBedRespawn(boolean respawn) {
        this.props.setBedRespawn(respawn);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean getBedRespawn() {
        return this.props.getBedRespawn();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getAllPropertyNames() {
        return this.props.getAllPropertyNames();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getTime() {
        return this.props.getTime();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean setTime(String timeAsString) {
        return this.props.setTime(timeAsString);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public AllowedPortalType getAllowedPortals() {
        return props.getAllowedPortals();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void allowPortalMaking(AllowedPortalType portalType) {
        this.props.allowPortalMaking(portalType);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ChatColor getStyle() {
        return this.props.getStyle().getColor();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean setStyle(String style) {
        return this.props.setStyle(style);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean getAllowFlight() {
        return this.props.getAllowFlight();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setAllowFlight(final boolean allowFlight) {
        this.props.setAllowFlight(allowFlight);
    }

    @Override
    public String toString() {
        final JSONObject jsonData = new JSONObject();
        jsonData.put("Name", getName());
        jsonData.put("Env", getEnvironment().toString());
        jsonData.put("Type", getWorldType().toString());
        jsonData.put("Gen", getGenerator());
        final JSONObject topLevel = new JSONObject();
        topLevel.put(getClass().getSimpleName() + "@" + hashCode(), jsonData);
        return topLevel.toString();
    }
}
