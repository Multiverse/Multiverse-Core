package org.mvplugins.multiverse.core.world;

import java.util.List;

import com.google.common.base.Strings;
import io.vavr.control.Try;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Difficulty;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import org.mvplugins.multiverse.core.config.CoreConfig;
import org.mvplugins.multiverse.core.config.handle.StringPropertyHandle;
import org.mvplugins.multiverse.core.world.location.SpawnLocation;
import org.mvplugins.multiverse.core.world.entity.EntitySpawnConfig;

/**
 * Represents a world handled by Multiverse which has all the custom properties provided by Multiverse.
 */
public sealed class MultiverseWorld permits LoadedMultiverseWorld {
    /**
     * This world's name.
     */
    protected final String worldName;

    /**
     * This world's configuration.
     */
    WorldConfig worldConfig;

    private final CoreConfig config;
    private String colourlessAlias = "";

    MultiverseWorld(String worldName, WorldConfig worldConfig, CoreConfig config) {
        this.worldName = worldName;
        this.worldConfig = worldConfig;
        this.config = config;
        this.worldConfig.setMVWorld(this);
        updateColourlessAlias();
    }

    /**
     * Gets the name of this world. The name cannot be changed.
     * <br/>
     * Note for plugin developers: Usually {@link #getAliasOrName()}
     * is what you want to use instead of this method.
     *
     * @return The name of the world as a String.
     */
    public String getName() {
        return worldName;
    }

    /**
     * Gets the tab complete name of this world. Use alias if `resolve-alias-name` config is true, else use world name.
     *
     * @return The tab complete name of the world as a String
     */
    public String getTabCompleteName() {
        return config.getResolveAliasName() ? getColourlessAlias() : getName();
    }

    /**
     * Gets whether this world is loaded.
     *
     * @return True if the world is loaded, else false.
     */
    public boolean isLoaded() {
        return worldConfig.isLoadedWorld();
    }

    /**
     * Gets the properties handler of this world.
     *
     * @return The properties handler of this world.
     */
    public StringPropertyHandle getStringPropertyHandle() {
        return worldConfig.getStringPropertyHandle();
    }

    /**
     * Gets whether or not Multiverse should auto-adjust the spawn for this world.
     *
     * @return True if Multiverse should adjust the spawn, false if not.
     */
    public boolean getAdjustSpawn() {
        return worldConfig.getAdjustSpawn();
    }

    /**
     * Sets whether or not Multiverse should auto-adjust the spawn for this world.
     *
     * @param adjustSpawn True if multiverse should adjust the spawn, false if not.
     * @return Result of setting property.
     */
    public Try<Void> setAdjustSpawn(boolean adjustSpawn) {
        return worldConfig.setAdjustSpawn(adjustSpawn);
    }

    public String getAlias() {
        return worldConfig.getAlias();
    }

    /**
     * Sets the alias of the world.
     *
     * @param alias A string that is the new alias.
     * @return Result of setting property.
     */
    public Try<Void> setAlias(String alias) {
        return worldConfig.setAlias(alias);
    }

    /**
     * Gets the alias of this world or defaults to world name if no alias is set.
     * <br/>
     * This alias allows users to have a world named "world" but show up in the list as "FernIsland"
     *
     * @return The alias of the world as a String.
     */
    public String getAliasOrName() {
        return Strings.isNullOrEmpty(worldConfig.getAlias()) ? worldName : worldConfig.getAlias();
    }

    /**
     * Gets the alias without any colour codes
     *
     * @return The colourless alias
     */
    public String getColourlessAlias() {
        return colourlessAlias;
    }

    void updateColourlessAlias() {
        colourlessAlias = ChatColor.stripColor(ChatColor.translateAlternateColorCodes('&', getAliasOrName()));
    }

    /**
     * Whether or not players are allowed to fly in this world.
     *
     * @return True if players allowed to fly in this world.
     */
    public boolean isAllowFlight() {
        return worldConfig.isAllowFlight();
    }

    /**
     * Sets whether or not players are allowed to fly in this world.
     *
     * @param allowFlight True to allow flight in this world.
     * @return Result of setting property.
     */
    public Try<Void> setAllowFlight(boolean allowFlight) {
        return worldConfig.setAllowFlight(allowFlight);
    }

    /**
     * Gets whether weather is enabled in this world.
     *
     * @return True if weather events will occur, false if not.
     */
    public boolean isAllowWeather() {
        return worldConfig.isAllowWeather();
    }

    /**
     * Sets whether or not there will be weather events in a given world.
     * If set to false, Multiverse will disable the weather in the world immediately.
     *
     * @param allowWeather True if weather events should occur in a world, false if not.
     * @return Result of setting property.
     */
    public Try<Void> setAllowWeather(boolean allowWeather) {
        return worldConfig.setAllowWeather(allowWeather);
    }

    /**
     * Gets whether or not a player who dies in this world will respawn in their
     * anchor or follow the normal respawn pattern.
     *
     * @return True if players dying in this world should respawn at their anchor.
     */
    public boolean getAnchorRespawn() {
        return worldConfig.getAnchorRespawn();
    }

    /**
     * Sets whether or not a player who dies in this world will respawn in their
     * anchor or follow the normal respawn pattern.
     * <br/>
     * True is default.
     *
     * @param anchorSpawn True if players dying in this world respawn at their anchor.
     * @return Result of setting property.
     */
    public Try<Void> setAnchorSpawn(boolean anchorSpawn) {
        return worldConfig.setAnchorSpawn(anchorSpawn);
    }

    /**
     * Gets whether or not a world will auto-heal players if the difficulty is on peaceful.
     *
     * @return True if the world should heal (default), false if not.
     */
    public boolean getAutoHeal() {
        return worldConfig.getAutoHeal();
    }

    /**
     * Sets whether or not a world will auto-heal players if the difficulty is on peaceful.
     *
     * @param autoHeal True if the world will heal.
     * @return Result of setting property.
     */
    public Try<Void> setAutoHeal(boolean autoHeal) {
        return worldConfig.setAutoHeal(autoHeal);
    }

    /**
     * Gets whether or not Multiverse should auto-load this world on server start.
     *
     * @return True if Multiverse should auto-load this world on server start.
     */
    public boolean isAutoLoad() {
        return worldConfig.isAutoLoad();
    }

    /**
     * Sets whether or not Multiverse should auto-load this world.
     * <br/>
     * True is default.
     *
     * @param autoLoad True if multiverse should autoload this world the spawn, false if not.
     * @return Result of setting property.
     */
    public Try<Void> setAutoLoad(boolean autoLoad) {
        return worldConfig.setAutoLoad(autoLoad);
    }

    /**
     * Gets whether or not a player who dies in this world will respawn in their
     * bed or follow the normal respawn pattern.
     *
     * @return True if players dying in this world should respawn at their bed.
     */
    public boolean getBedRespawn() {
        return worldConfig.getBedRespawn();
    }

    /**
     * Sets whether or not a player who dies in this world will respawn in their
     * bed or follow the normal respawn pattern.
     * <br/>
     * True is default.
     *
     * @param bedRespawn True if players dying in this world respawn at their bed.
     * @return Result of setting property.
     */
    public Try<Void> setBedRespawn(boolean bedRespawn) {
        return worldConfig.setBedRespawn(bedRespawn);
    }

    /**
     * Gets the single biome used for this world. This may be null, in which case the biome from the generator will be used.
     * If no generator is specified, the "natural" biome behaviour for this environment will be used.
     *
     * @return The biome used for this world
     */
    public @NotNull String getBiome() {
        return worldConfig.getBiome();
    }

    /**
     * Gets the type of currency that will be used when users enter this world. A value of null indicates a non-item
     * based currency is used.
     *
     * @return The type of currency that will be used when users enter this world.
     */
    public Material getCurrency() {
        return worldConfig.getEntryFeeCurrency();
    }

    /**
     * Sets the type of item that will be required given the price is not 0.
     * Use a value of null to specify a non-item based currency.
     *
     * @param currency The Type of currency that will be used when users enter this world.
     * @return Result of setting property.
     */
    public Try<Void> setCurrency(Material currency) {
        return worldConfig.setEntryFeeCurrency(currency);
    }

    /**
     * Gets the difficulty of this world.
     *
     * @return The difficulty of this world.
     */
    public Difficulty getDifficulty() {
        return worldConfig.getDifficulty();
    }

    /**
     * Sets the difficulty of this world and returns {@code true} on success.
     * Valid string values are either an integer of difficulty(0-3) or
     * the name that resides in the Bukkit enum, ex. PEACEFUL
     *
     * @param difficulty The new difficulty.
     * @return Result of setting property.
     */
    public Try<Void> setDifficulty(Difficulty difficulty) {
        return worldConfig.setDifficulty(difficulty);
    }

    /**
     * Gets the environment of this world. You cannot change this after world creation.
     *
     * @return A {@link World.Environment}.
     */
    public World.Environment getEnvironment() {
        return worldConfig.getEnvironment();
    }

    /**
     * Gets the GameMode of this world.
     *
     * @return The GameMode of this world.
     */
    public GameMode getGameMode() {
        return worldConfig.getGameMode();
    }

    /**
     * Sets the game mode of this world.
     *
     * @param gameMode The new {@link GameMode}.
     * @return Result of setting property.
     */
    public Try<Void> setGameMode(GameMode gameMode) {
        return worldConfig.setGameMode(gameMode);
    }

    /**
     * Gets the generator string of this world. You cannot change this after world creation.
     *
     * @return The name of the generator.
     */
    public String getGenerator() {
        return worldConfig.getGenerator();
    }

    /**
     * Gets whether or not this world will display in chat, mvw and mvl regardless if a user has the
     * access permissions to go to this world.
     *
     * @return True if the world will be hidden, false if not.
     */
    public boolean isHidden() {
        return worldConfig.isHidden();
    }

    /**
     * Sets whether or not this world will display in chat, mvw and mvl regardless if a user has the
     * access permissions to go to this world.
     *
     * @param hidden True if the world should be hidden, false if not.
     * @return Result of setting property.
     */
    public Try<Void> setHidden(boolean hidden) {
        return worldConfig.setHidden(hidden);
    }

    /**
     * Gets whether or not the hunger level of players will go down in a world.
     *
     * @return True if it will go down, false if it will remain steady.
     */
    public boolean isHunger() {
        return worldConfig.isHunger();
    }

    /**
     * Sets whether or not the hunger level of players will go down in a world.
     *
     * @param hunger True if hunger will go down, false to keep it at the level they entered a world with.
     * @return Result of setting property.
     */
    public Try<Void> setHunger(boolean hunger) {
        return worldConfig.setHunger(hunger);
    }

    /**
     * Gets whether or not CraftBukkit is keeping the chunks for this world in memory.
     *
     * @return True if CraftBukkit is keeping spawn chunks in memory.
     */
    public boolean isKeepSpawnInMemory() {
        return worldConfig.isKeepSpawnInMemory();
    }

    /**
     * If true, tells Craftbukkit to keep a worlds spawn chunks loaded in memory (default: true)
     * If not, CraftBukkit will attempt to free memory when players have not used that world.
     * This will not happen immediately.
     *
     * @param keepSpawnInMemory If true, CraftBukkit will keep the spawn chunks loaded in memory.
     * @return Result of setting property.
     */
    public Try<Void> setKeepSpawnInMemory(boolean keepSpawnInMemory) {
        return worldConfig.setKeepSpawnInMemory(keepSpawnInMemory);
    }

    /**
     * Gets the player limit for this world after which players without an override
     * permission node will not be allowed in. A value of -1 or less signifies no limit
     *
     * @return The player limit
     */
    public int getPlayerLimit() {
        return worldConfig.getPlayerLimit();
    }

    /**
     * Sets the player limit for this world after which players without an override
     * permission node will not be allowed in. A value of -1 or less signifies no limit
     *
     * @param playerLimit The new limit
     * @return Result of setting property.
     */
    public Try<Void> setPlayerLimit(int playerLimit) {
        return worldConfig.setPlayerLimit(playerLimit);
    }

    /**
     * Gets which type(s) of portals are allowed to be constructed in this world.
     *
     * @return The type of portals that are allowed.
     */
    public AllowedPortalType getPortalForm() {
        return worldConfig.getPortalForm();
    }

    /**
     * Sets The types of portals that are allowed in this world.
     *
     * @param portalForm The type of portals allowed in this world.
     * @return Result of setting property.
     */
    public Try<Void> setPortalForm(AllowedPortalType portalForm) {
        return worldConfig.setPortalForm(portalForm);
    }

    /**
     * Gets if entry fee is needed when entering this world
     *
     * @return True if entry fee is needed
     */
    public boolean isEntryFeeEnabled() {
        return worldConfig.isEntryFeeEnabled();
    }

    /**
     * Sets if entry fee is needed when entering this world
     *
     * @param entryFeeEnabled True to enable use of entry fee
     * @return Result of setting property.
     */
    public Try<Void> setEntryFeeEnabled(boolean entryFeeEnabled) {
        return worldConfig.setEntryFeeEnabled(entryFeeEnabled);
    }

    /**
     * Gets the amount of currency it requires to enter this world.
     *
     * @return The amount it costs to enter this world.
     */
    public double getPrice() {
        return worldConfig.getEntryFeeAmount();
    }

    /**
         * Sets the price for entry to this world.
     * You can think of this like an amount.
     * The type can be set with {@link #setCurrency(Material)}
     *
     * @param price The Amount of money/item to enter the world.
     * @return Result of setting property.
     */
    public Try<Void> setPrice(double price) {
        return worldConfig.setEntryFeeAmount(price);
    }

    /**
     * Gets whether or not PVP is enabled in this world in some form (fake or not).
     *
     * @return True if players can take damage from other players.
     */
    public boolean getPvp() {
        return worldConfig.getPvp();
    }

    /**
         * Turn pvp on or off. This setting is used to set the world's PVP mode.
         *
         * @param pvp True to enable PVP damage, false to disable it.
         * @return Result of setting property.
             */
    public Try<Void> setPvp(boolean pvp) {
        return worldConfig.setPvp(pvp);
    }

    /**
     * Gets the world name players will respawn in if they die in this one.
     *
     * @return A world name that exists on the server.
     */
    public String getRespawnWorldName() {
        return worldConfig.getRespawnWorld();
    }

    /**
     * Gets the world players will respawn in if they die in this one.
     *
     * @return A world that exists on the server.
     */
    public @Nullable World getRespawnWorld() {
        return Bukkit.getWorld(worldConfig.getRespawnWorld());
    }

    /**
     * Sets the world players will respawn in if they die in this one.
     * Returns true upon success, false upon failure.
     *
     * @param respawnWorld The name of a world that exists on the server.
     * @return Result of setting property.
     */
    public Try<Void> setRespawnWorld(World respawnWorld) {
        return worldConfig.setRespawnWorld(respawnWorld.getName());
    }

    /**
     * Sets the world players will respawn in if they die in this one.
     * Returns true upon success, false upon failure.
     *
     * @param respawnWorld The name of a world that exists on the server.
     * @return Result of setting property.
     */
    public Try<Void> setRespawnWorld(MultiverseWorld respawnWorld) {
        return worldConfig.setRespawnWorld(respawnWorld.getName());
    }

    /**
     * Sets the world players will respawn in if they die in this one.
     * Returns true upon success, false upon failure.
     *
     * @param respawnWorld The name of a world that exists on the server.
     * @return Result of setting property.
     */
    public Try<Void> setRespawnWorld(String respawnWorld) {
        return worldConfig.setRespawnWorld(respawnWorld);
    }

    /**
     * Gets the scaling value of this world.Really only has an effect if you use
     * Multiverse-NetherPortals.
     *
     * @return This world's non-negative, non-zero scale.
     */
    public double getScale() {
    return worldConfig.getScale();
    }

    /**
     * Sets the scale of this world. Really only has an effect if you use
     * Multiverse-NetherPortals.
     *
     * @param scale A scaling value, cannot be negative or 0.
     * @return Result of setting property.
     */
    public Try<Void> setScale(double scale) {
        return worldConfig.setScale(scale);
    }

    /**
     * Gets the world seed of this world. This cannot be changed after world creation.
     *
     * @return The Long version of the seed.
     */
    public long getSeed() {
        return worldConfig.getSeed();
    }

    /**
     * Gets a copy of the spawn location of this world. {@link Location#getWorld()} will be null when
     * the world is unloaded. I.e. {@link MultiverseWorld#isLoaded()} is false.
     *
     * @return The spawn location of this world.
     */
    public Location getSpawnLocation() {
        return worldConfig.getSpawnLocation().toBukkitLocation();
    }

    /**
     * Sets the spawn location for a world.
     *
     * @param spawnLocation The spawn location for a world.
     * @return Result of setting property.
     */
    public Try<Void> setSpawnLocation(Location spawnLocation) {
        return worldConfig.setSpawnLocation(spawnLocation instanceof SpawnLocation
                ? (SpawnLocation) spawnLocation.clone()
                : new SpawnLocation(spawnLocation));
    }

    public EntitySpawnConfig getEntitySpawnConfig() {
        return worldConfig.getEntitySpawnConfig();
    }

    public Try<Void> setEntitySpawnConfig(EntitySpawnConfig entitySpawnConfig) {
        return worldConfig.setEntitySpawnConfig(entitySpawnConfig);
    }

    /**
     * Gets a list of all the worlds that players CANNOT travel to from this world,regardless of their access
     * permissions.
     *
     * @return A List of world names.
     */
    public List<String> getWorldBlacklist() {
        return worldConfig.getWorldBlacklist();
    }

    /**
     * Sets the list of worlds that players CANNOT travel to from this world, regardless of their access permissions.
     *
     * @param worldBlacklist A List of world names.
     * @return Result of setting property.
     */
    public Try<Void> setWorldBlacklist(List<String> worldBlacklist) {
        return worldConfig.setWorldBlacklist(worldBlacklist);
    }

    /**
     * Gets the world config. Only for internal use.
     *
     * @return The world config.
     */
    WorldConfig getWorldConfig() {
        return worldConfig;
    }

    /**
     * Sets the world config. Only for internal use.
     *
     * @param worldConfig   The world config.
     */
    void setWorldConfig(WorldConfig worldConfig) {
        this.worldConfig = worldConfig;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        return "MultiverseWorld{"
                + "name='" + worldName + "', "
                + "env='" + getEnvironment() + "', "
                + "gen='" + getGenerator() + "'"
                + '}';
    }
}
