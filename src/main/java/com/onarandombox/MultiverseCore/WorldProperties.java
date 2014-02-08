package com.onarandombox.MultiverseCore;

import com.onarandombox.MultiverseCore.MVWorld.NullLocation;
import com.onarandombox.MultiverseCore.configuration.EntryFee;
import com.onarandombox.MultiverseCore.configuration.SpawnLocation;
import com.onarandombox.MultiverseCore.configuration.SpawnSettings;
import com.onarandombox.MultiverseCore.configuration.WorldPropertyValidator;
import com.onarandombox.MultiverseCore.enums.AllowedPortalType;
import com.onarandombox.MultiverseCore.enums.EnglishChatColor;
import com.onarandombox.MultiverseCore.enums.EnglishChatStyle;
import me.main__.util.SerializationConfig.IllegalPropertyValueException;
import me.main__.util.SerializationConfig.Property;
import me.main__.util.SerializationConfig.SerializationConfig;
import me.main__.util.SerializationConfig.Serializor;
import me.main__.util.SerializationConfig.Validator;
import me.main__.util.SerializationConfig.VirtualProperty;
import org.bukkit.ChatColor;
import org.bukkit.Difficulty;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.World.Environment;
import org.bukkit.configuration.serialization.SerializableAs;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/*
 * This is a property class, I think we don't need that much javadoc.
 * BEGIN CHECKSTYLE-SUPPRESSION: Javadoc
 */

@SerializableAs("MVWorld")
public class WorldProperties extends SerializationConfig {

    private static final Map<String, String> PROPERTY_ALIASES;

    static {
        PROPERTY_ALIASES = new HashMap<String, String>();
        PROPERTY_ALIASES.put("curr", "entryfee.currency");
        PROPERTY_ALIASES.put("currency", "entryfee.currency");
        PROPERTY_ALIASES.put("price", "entryfee.amount");
        PROPERTY_ALIASES.put("scaling", "scale");
        PROPERTY_ALIASES.put("aliascolor", "color");
        PROPERTY_ALIASES.put("heal", "autoHeal");
        PROPERTY_ALIASES.put("storm", "allowWeather");
        PROPERTY_ALIASES.put("weather", "allowWeather");
        PROPERTY_ALIASES.put("spawnmemory", "keepSpawnInMemory");
        PROPERTY_ALIASES.put("memory", "keepSpawnInMemory");
        PROPERTY_ALIASES.put("mode", "gameMode");
        PROPERTY_ALIASES.put("diff", "difficulty");
        PROPERTY_ALIASES.put("spawnlocation", "spawn");
        PROPERTY_ALIASES.put("limit", "playerLimit");
        PROPERTY_ALIASES.put("animals", "spawning.animals.spawn");
        PROPERTY_ALIASES.put("monsters", "spawning.monsters.spawn");
        PROPERTY_ALIASES.put("animalsrate", "spawning.animals.spawnrate");
        PROPERTY_ALIASES.put("monstersrate", "spawning.monsters.spawnrate");
        PROPERTY_ALIASES.put("flight", "allowFlight");
        PROPERTY_ALIASES.put("fly", "allowFlight");
        PROPERTY_ALIASES.put("allowfly", "allowFlight");
    }

    private final boolean keepSpawnFallback;

    public WorldProperties(Map<String, Object> values) {
        super(values);
        Object keepSpawnObject = values.get("keepSpawnInMemory");
        keepSpawnFallback = keepSpawnObject == null || Boolean.parseBoolean(keepSpawnObject.toString());
    }

    public WorldProperties() {
        super();
        keepSpawnFallback = true;
    }

    public WorldProperties(final boolean fixSpawn, final Environment environment) {
        super();
        if (!fixSpawn) {
            this.adjustSpawn = false;
        }
        setScaling(getDefaultScale(environment));
        keepSpawnFallback = true;
    }

    void setMVWorld(MVWorld world) {
        registerObjectUsing(world);
        registerGlobalValidator(new WorldPropertyValidator());
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

    // --------------------------------------------------------------
    // Begin properties
    @Property(description = "Sorry, 'hidden' must either be: true or false.")
    private volatile boolean hidden;
    @Property(description = "Alias must be a valid string.")
    private volatile String alias;
    @Property(serializor = EnumPropertySerializor.class, description = "Sorry, 'color' must be a valid color-name.")
    private volatile EnglishChatColor color;
    @Property(serializor = EnumPropertySerializor.class, description = "Sorry, 'style' must be a valid style-name.")
    private volatile EnglishChatStyle style;
    @Property(description = "Sorry, 'pvp' must either be: true or false.", virtualType = Boolean.class, persistVirtual = true)
    volatile VirtualProperty<Boolean> pvp; // SUPPRESS CHECKSTYLE: VisibilityModifier
    @Property(description = "Scale must be a positive double value. ex: 2.3")
    private volatile double scale;
    @Property(description = "You must set this to the NAME not alias of a world.")
    private volatile String respawnWorld;
    @Property(description = "Sorry, this must either be: true or false.")
    private volatile boolean allowWeather;
    @Property(serializor = DifficultyPropertySerializor.class, virtualType = Difficulty.class, persistVirtual = true,
            description = "Difficulty must be set as one of the following: peaceful easy normal hard")
    volatile VirtualProperty<Difficulty> difficulty; // SUPPRESS CHECKSTYLE: VisibilityModifier
    @Property(description = "Sorry, 'animals' must either be: true or false.")
    private volatile SpawnSettings spawning;
    @Property
    private volatile EntryFee entryfee;
    @Property(description = "Sorry, 'hunger' must either be: true or false.")
    private volatile boolean hunger;
    @Property(description = "Sorry, 'autoheal' must either be: true or false.")
    private volatile boolean autoHeal;
    @Property(description = "Sorry, 'adjustspawn' must either be: true or false.")
    private volatile boolean adjustSpawn;
    @Property(serializor = EnumPropertySerializor.class, description = "Allow portal forming must be NONE, ALL, NETHER or END.")
    private volatile AllowedPortalType portalForm;
    @Property(serializor = GameModePropertySerializor.class, description = "GameMode must be set as one of the following: survival creative")
    private volatile GameMode gameMode;
    @Property(description = "Sorry, this must either be: true or false.", virtualType = Boolean.class, persistVirtual = true)
    volatile VirtualProperty<Boolean> keepSpawnInMemory; // SUPPRESS CHECKSTYLE: VisibilityModifier
    @Property
    volatile SpawnLocation spawnLocation; // SUPPRESS CHECKSTYLE: VisibilityModifier
    @Property(virtualType = Location.class,
            description = "There is no help available for this variable. Go bug Rigby90 about it.")
    volatile VirtualProperty<Location> spawn; // SUPPRESS CHECKSTYLE: VisibilityModifier
    @Property(description = "Set this to false ONLY if you don't want this world to load itself on server restart.")
    private volatile boolean autoLoad;
    @Property(description = "If a player dies in this world, shoudld they go to their bed?")
    private volatile boolean bedRespawn;
    @Property
    private volatile List<String> worldBlacklist;
    @Property(serializor = TimePropertySerializor.class, virtualType = Long.class,
            description = "Set the time to whatever you want! (Will NOT freeze time)")
    volatile VirtualProperty<Long> time; // SUPPRESS CHECKSTYLE: VisibilityModifier
    @Property
    volatile Environment environment; // SUPPRESS CHECKSTYLE: VisibilityModifier
    @Property
    volatile long seed; // SUPPRESS CHECKSTYLE: VisibilityModifier
    @Property
    private volatile String generator;
    @Property
    private volatile int playerLimit;
    @Property
    private volatile boolean allowFlight;
    // End of properties
    // --------------------------------------------------------------

    void setValidator(String fieldName, Validator validator) {
        registerValidator(fieldName, validator);    //To change body of overridden methods use File | Settings | File Templates.
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void copyValues(SerializationConfig other) {
        super.copyValues(other);
    }

    /**
     * This prepares the MVWorld for unloading.
     */
    public void cacheVirtualProperties() {
        try {
            this.buildVPropChanges();
        } catch (IllegalStateException e) {
            // do nothing
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void setDefaults() {
        this.hidden = false;
        this.alias = new String();
        this.color = EnglishChatColor.WHITE;
        this.style = EnglishChatStyle.NORMAL;
        this.scale = 1D;
        this.respawnWorld = new String();
        this.allowWeather = true;
        this.spawning = new SpawnSettings();
        this.entryfee = new EntryFee();
        this.hunger = true;
        this.autoHeal = true;
        this.adjustSpawn = true;
        this.portalForm = AllowedPortalType.ALL;
        this.gameMode = GameMode.SURVIVAL;
        this.spawnLocation = new NullLocation();
        this.autoLoad = true;
        this.bedRespawn = true;
        this.worldBlacklist = new ArrayList<String>();
        this.generator = null;
        this.playerLimit = -1;
        this.allowFlight = true;
    }

    private static double getDefaultScale(Environment environment) {
        if (environment == Environment.NETHER) {
            return 8.0; // SUPPRESS CHECKSTYLE: MagicNumberCheck
        } else if (environment == Environment.THE_END) {
            return 16.0; // SUPPRESS CHECKSTYLE: MagicNumberCheck
        }
        return 1.0;
    }

    /**
     * getAliases().
     * @return The alias-map.
     * @see SerializationConfig
     */
    protected static Map<String, String> getAliases() {
        return PROPERTY_ALIASES;
    }

    void flushChanges() {
        this.flushPendingVPropChanges();
    }

    String getAlias() {
        return this.alias;
    }

    public void setAlias(String alias) {
        this.setPropertyValueUnchecked("alias", alias);
    }

    public Environment getEnvironment() {
        return this.environment;
    }

    public void setEnvironment(Environment environment) {
        this.setPropertyValueUnchecked("environment", environment);
    }

    public long getSeed() {
        return this.seed;
    }

    public void setSeed(long seed) {
        this.setPropertyValueUnchecked("seed", seed);
    }

    public String getGenerator() {
        return this.generator;
    }

    public void setGenerator(String generator) {
        this.setPropertyValueUnchecked("generator", generator);
    }

    public int getPlayerLimit() {
        return this.playerLimit;
    }

    public void setPlayerLimit(int limit) {
        this.setPropertyValueUnchecked("playerLimit", limit);
    }

    public boolean canAnimalsSpawn() {
        return this.spawning.getAnimalSettings().doSpawn();
    }

    public void setAllowAnimalSpawn(boolean animals) {
        this.setPropertyValueUnchecked("spawning.animals.spawn", animals);
    }

    public List<String> getAnimalList() {
        // These don't fire events at the moment. Should they?
        return this.spawning.getAnimalSettings().getExceptions();
    }

    public boolean canMonstersSpawn() {
        return this.spawning.getMonsterSettings().doSpawn();
    }

    public void setAllowMonsterSpawn(boolean monsters) {
        this.setPropertyValueUnchecked("spawning.monsters.spawn", monsters);
    }

    public int getAnimalSpawnRate() {
        return this.spawning.getAnimalSettings().getSpawnRate();
    }

    public int getMonsterSpawnRate() {
        return this.spawning.getMonsterSettings().getSpawnRate();
    }

    public List<String> getMonsterList() {
        // These don't fire events at the moment. Should they?
        return this.spawning.getMonsterSettings().getExceptions();
    }

    public boolean isPVPEnabled() {
        return this.pvp.get();
    }

    public void setPVPMode(boolean pvp) {
        this.setPropertyValueUnchecked("pvp", pvp);
    }

    public boolean isHidden() {
        return this.hidden;
    }

    public void setHidden(boolean hidden) {
        this.setPropertyValueUnchecked("hidden", hidden);
    }

    public List<String> getWorldBlacklist() {
        return this.worldBlacklist;
    }

    public double getScaling() {
        return this.scale;
    }

    public boolean setScaling(double scaling) {
        return this.setPropertyValueUnchecked("scale", scaling);
    }

    public boolean setColor(String aliasColor) {
        return this.setPropertyUnchecked("color", aliasColor);
    }

    public boolean setColor(EnglishChatColor color) {
        return this.setPropertyValueUnchecked("color", color);
    }

    public EnglishChatColor getColor() {
        return this.color;
    }

    public String getRespawnToWorld() {
        return this.respawnWorld;
    }

    public boolean setRespawnToWorld(String respawnToWorld) {
        return this.setPropertyValueUnchecked("respawnWorld", respawnToWorld);
    }

    public int getCurrency() {
        return this.entryfee.getCurrency();
    }

    public void setCurrency(int currency) {
        this.setPropertyValueUnchecked("entryfee.currency", currency);
    }

    public double getPrice() {
        return this.entryfee.getAmount();
    }

    public void setPrice(double price) {
        this.setPropertyValueUnchecked("entryfee.amount", price);
    }

    public boolean setGameMode(String mode) {
        return this.setPropertyUnchecked("gameMode", mode);
    }

    public boolean setGameMode(GameMode mode) {
        return this.setPropertyValueUnchecked("gameMode", mode);
    }

    public GameMode getGameMode() {
        return this.gameMode;
    }

    public void setEnableWeather(boolean weather) {
        this.setPropertyValueUnchecked("allowWeather", weather);
    }

    public boolean isWeatherEnabled() {
        return this.allowWeather;
    }

    public boolean isKeepingSpawnInMemory() {
        if (keepSpawnInMemory == null) {
            return keepSpawnFallback;
        }
        return this.keepSpawnInMemory.get();
    }

    public void setKeepSpawnInMemory(boolean value) {
        this.setPropertyValueUnchecked("keepSpawnInMemory", value);
    }

    public boolean getHunger() {
        return this.hunger;
    }

    public void setHunger(boolean hunger) {
        this.setPropertyValueUnchecked("hunger", hunger);
    }

    public Location getSpawnLocation() {
        return this.spawn.get();
    }

    public void setSpawnLocation(Location l) {
        this.setPropertyValueUnchecked("spawn", l);
    }

    public Difficulty getDifficulty() {
        return this.difficulty.get();
    }

    @Deprecated // SUPPRESS CHECKSTYLE: Deprecated
    public boolean setDifficulty(String difficulty) {
        return this.setPropertyUnchecked("difficulty", difficulty);
    }

    public boolean setDifficulty(Difficulty difficulty) {
        return this.setPropertyValueUnchecked("difficulty", difficulty);
    }

    public boolean getAutoHeal() {
        return this.autoHeal;
    }

    public void setAutoHeal(boolean heal) {
        this.setPropertyValueUnchecked("autoHeal", heal);
    }

    public void setAdjustSpawn(boolean adjust) {
        this.setPropertyValueUnchecked("adjustSpawn", adjust);
    }

    public boolean getAdjustSpawn() {
        return this.adjustSpawn;
    }

    public void setAutoLoad(boolean load) {
        this.setPropertyValueUnchecked("autoLoad", load);
    }

    public boolean getAutoLoad() {
        return this.autoLoad;
    }

    public void setBedRespawn(boolean respawn) {
        this.setPropertyValueUnchecked("bedRespawn", respawn);
    }

    public boolean getBedRespawn() {
        return this.bedRespawn;
    }

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

    public String getTime() {
        return this.getPropertyUnchecked("time");
    }

    public boolean setTime(String timeAsString) {
        return this.setPropertyUnchecked("time", timeAsString);
    }

    public AllowedPortalType getAllowedPortals() {
        return portalForm;
    }

    public void allowPortalMaking(AllowedPortalType portalType) {
        this.setPropertyValueUnchecked("portalForm", portalType);
    }

    public EnglishChatStyle getStyle() {
        return style;
    }

    public boolean setStyle(String style) {
        return this.setPropertyUnchecked("style", style);
    }

    public boolean getAllowFlight() {
        return this.allowFlight;
    }

    public void setAllowFlight(final boolean allowFlight) {
        this.setPropertyValueUnchecked("allowFlight", allowFlight);
    }
}
