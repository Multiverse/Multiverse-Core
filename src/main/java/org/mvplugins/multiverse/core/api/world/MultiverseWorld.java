package org.mvplugins.multiverse.core.api.world;

import io.vavr.control.Try;
import org.bukkit.Difficulty;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Biome;
import org.jetbrains.annotations.Nullable;
import org.mvplugins.multiverse.core.api.configuration.StringPropertyHandle;
import org.mvplugins.multiverse.core.world.config.AllowedPortalType;
import org.mvplugins.multiverse.core.world.config.SpawnLocation;

import java.util.List;

/**
 * Represents a world handled by Multiverse which has all the custom properties provided by Multiverse.
 *
 * @since 5.0
 */
public interface MultiverseWorld {
    /**
     * Gets the name of this world. The name cannot be changed.
     * <br/>
     * Note for plugin developers: Usually {@link #getAlias()}
     * is what you want to use instead of this method.
     *
     * @return The name of the world as a String.
     * @since 5.0
     */
    String getName();

    /**
     * Gets whether this world is loaded.
     *
     * @return True if the world is loaded, else false.
     * @since 5.0
     */
    boolean isLoaded();

    /**
     * Gets the properties handler of this world.
     *
     * @return The properties handler of this world.
     * @since 5.0
     */
    StringPropertyHandle getStringPropertyHandle();

    /**
     * Gets whether or not Multiverse should auto-adjust the spawn for this world.
     *
     * @return True if Multiverse should adjust the spawn, false if not.
     * @since 5.0
     */
    boolean getAdjustSpawn();

    /**
     * Sets whether or not Multiverse should auto-adjust the spawn for this world.
     *
     * @param adjustSpawn True if multiverse should adjust the spawn, false if not.
     * @return Result of setting property.
     * @since 5.0
     */
    Try<Void> setAdjustSpawn(boolean adjustSpawn);

    /**
     * Gets the alias of this world.
     * <br/>
     * This alias allows users to have a world named "world" but show up in the list as "FernIsland"
     *
     * @return The alias of the world as a String.
     * @since 5.0
     */
    String getAlias();

    /**
     * Sets the alias of the world.
     *
     * @param alias A string that is the new alias.
     * @return Result of setting property.
     * @since 5.0
     */
    Try<Void> setAlias(String alias);

    /**
     * Whether or not players are allowed to fly in this world.
     *
     * @return True if players allowed to fly in this world.
     * @since 5.0
     */
    boolean getAllowFlight();

    /**
     * Sets whether or not players are allowed to fly in this world.
     *
     * @param allowFlight True to allow flight in this world.
     * @return Result of setting property.
     * @since 5.0
     */
    Try<Void> setAllowFlight(boolean allowFlight);

    /**
     * Gets whether weather is enabled in this world.
     *
     * @return True if weather events will occur, false if not.
     * @since 5.0
     */
    boolean getAllowWeather();

    /**
     * Sets whether or not there will be weather events in a given world.
     * If set to false, Multiverse will disable the weather in the world immediately.
     *
     * @param allowWeather True if weather events should occur in a world, false if not.
     * @return Result of setting property.
     * @since 5.0
     */
    Try<Void> setAllowWeather(boolean allowWeather);

    /**
     * Gets whether or not a player who dies in this world will respawn in their
     * anchor or follow the normal respawn pattern.
     *
     * @return True if players dying in this world should respawn at their anchor.
     * @since 5.0
     */
    boolean getAnchorRespawn();

    /**
     * Sets whether or not a player who dies in this world will respawn in their
     * anchor or follow the normal respawn pattern.
     * <br/>
     * True is default.
     *
     * @param anchorSpawn True if players dying in this world respawn at their anchor.
     * @return Result of setting property.
     * @since 5.0
     */
    Try<Void> setAnchorSpawn(boolean anchorSpawn);

    /**
     * Gets whether or not a world will auto-heal players if the difficulty is on peaceful.
     *
     * @return True if the world should heal (default), false if not.
     * @since 5.0
     */
    boolean getAutoHeal();

    /**
     * Sets whether or not a world will auto-heal players if the difficulty is on peaceful.
     *
     * @param autoHeal True if the world will heal.
     * @return Result of setting property.
     * @since 5.0
     */
    Try<Void> setAutoHeal(boolean autoHeal);

    /**
     * Gets whether or not Multiverse should auto-load this world.
     *
     * @return True if Multiverse should auto-load this world.
     * @since 5.0
     */
    boolean getAutoLoad();

    /**
     * Sets whether or not Multiverse should auto-load this world.
     * <br/>
     * True is default.
     *
     * @param autoLoad True if multiverse should autoload this world the spawn, false if not.
     * @return Result of setting property.
     * @since 5.0
     */
    Try<Void> setAutoLoad(boolean autoLoad);

    /**
     * Gets whether or not a player who dies in this world will respawn in their
     * bed or follow the normal respawn pattern.
     *
     * @return True if players dying in this world should respawn at their bed.
     * @since 5.0
     */
    boolean getBedRespawn();

    /**
     * Sets whether or not a player who dies in this world will respawn in their
     * bed or follow the normal respawn pattern.
     * <br/>
     * True is default.
     *
     * @param bedRespawn True if players dying in this world respawn at their bed.
     * @return Result of setting property.
     * @since 5.0
     */
    Try<Void> setBedRespawn(boolean bedRespawn);

    /**
     * Gets the single biome used for this world. This may be null, in which case the biome from the generator will be used.
     * If no generator is specified, the "natural" biome behaviour for this environment will be used.
     *
     * @return The biome used for this world
     * @since 5.0
     */
    @Nullable Biome getBiome();

    /**
     * Gets the type of currency that will be used when users enter this world. A value of null indicates a non-item
     * based currency is used.
     *
     * @return The type of currency that will be used when users enter this world.
     * @since 5.0
     */
    Material getCurrency();

    /**
     * Sets the type of item that will be required given the price is not 0.
     * Use a value of null to specify a non-item based currency.
     *
     * @param currency The Type of currency that will be used when users enter this world.
     * @return Result of setting property.
     * @since 5.0
     */
    Try<Void> setCurrency(Material currency);

    /**
     * Gets the difficulty of this world.
     *
     * @return The difficulty of this world.
     * @since 5.0
     */
    Difficulty getDifficulty();

    /**
     * Sets the difficulty of this world and returns {@code true} on success.
     * Valid string values are either an integer of difficulty(0-3) or
     * the name that resides in the Bukkit enum, ex. PEACEFUL
     *
     * @param difficulty The new difficulty.
     * @return Result of setting property.
     * @since 5.0
     */
    Try<Void> setDifficulty(Difficulty difficulty);

    /**
     * Gets the environment of this world. You cannot change this after world creation.
     *
     * @return A {@link World.Environment}.
     * @since 5.0
     */
    World.Environment getEnvironment();

    /**
     * Gets the GameMode of this world.
     *
     * @return The GameMode of this world.
     * @since 5.0
     */
    GameMode getGameMode();

    /**
     * Sets the game mode of this world.
     *
     * @param gameMode The new {@link GameMode}.
     * @return Result of setting property.
     * @since 5.0
     */
    Try<Void> setGameMode(GameMode gameMode);

    /**
     * Gets the generator string of this world. You cannot change this after world creation.
     *
     * @return The name of the generator.
     * @since 5.0
     */
    String getGenerator();

    /**
     * Gets whether or not this world will display in chat, mvw and mvl regardless if a user has the
     * access permissions to go to this world.
     *
     * @return True if the world will be hidden, false if not.
     * @since 5.0
     */
    boolean isHidden();

    /**
     * Sets whether or not this world will display in chat, mvw and mvl regardless if a user has the
     * access permissions to go to this world.
     *
     * @param hidden True if the world should be hidden, false if not.
     * @return Result of setting property.
     * @since 5.0
     */
    Try<Void> setHidden(boolean hidden);

    /**
     * Gets whether or not the hunger level of players will go down in a world.
     *
     * @return True if it will go down, false if it will remain steady.
     * @since 5.0
     */
    boolean getHunger();

    /**
     * Sets whether or not the hunger level of players will go down in a world.
     *
     * @param hunger True if hunger will go down, false to keep it at the level they entered a world with.
     * @return Result of setting property.
     * @since 5.0
     */
    Try<Void> setHunger(boolean hunger);

    /**
     * Gets whether or not CraftBukkit is keeping the chunks for this world in memory.
     *
     * @return True if CraftBukkit is keeping spawn chunks in memory.
     * @since 5.0
     */
    boolean getKeepSpawnInMemory();

    /**
     * If true, tells Craftbukkit to keep a worlds spawn chunks loaded in memory (default: true)
     * If not, CraftBukkit will attempt to free memory when players have not used that world.
     * This will not happen immediately.
     *
     * @param keepSpawnInMemory If true, CraftBukkit will keep the spawn chunks loaded in memory.
     * @return Result of setting property.
     * @since 5.0
     */
    Try<Void> setKeepSpawnInMemory(boolean keepSpawnInMemory);

    /**
     * Gets the player limit for this world after which players without an override
     * permission node will not be allowed in. A value of -1 or less signifies no limit
     *
     * @return The player limit
     * @since 5.0
     */
    int getPlayerLimit();

    /**
     * Sets the player limit for this world after which players without an override
     * permission node will not be allowed in. A value of -1 or less signifies no limit
     *
     * @param playerLimit The new limit
     * @return Result of setting property.
     * @since 5.0
     */
    Try<Void> setPlayerLimit(int playerLimit);

    /**
     * Gets which type(s) of portals are allowed to be constructed in this world.
     *
     * @return The type of portals that are allowed.
     * @since 5.0
     */
    AllowedPortalType getPortalForm();

    /**
     * Sets The types of portals that are allowed in this world.
     *
     * @param portalForm The type of portals allowed in this world.
     * @return Result of setting property.
     * @since 5.0
     */
    Try<Void> setPortalForm(AllowedPortalType portalForm);

    /**
     * Gets if entry fee is needed when entering this world
     *
     * @return True if entry fee is needed
     * @since 5.0
     */
    boolean isEntryFeeEnabled();

    /**
     * Sets if entry fee is needed when entering this world
     *
     * @param entryFeeEnabled True to enable use of entry fee
     * @return Result of setting property.
     * @since 5.0
     */
    Try<Void> setEntryFeeEnabled(boolean entryFeeEnabled);

    /**
     * Gets the amount of currency it requires to enter this world.
     *
     * @return The amount it costs to enter this world.
     * @since 5.0
     */
    double getPrice();

    /**
     * Sets the price for entry to this world.
     * You can think of this like an amount.
     * The type can be set with {@link #setCurrency(Material)}
     *
     * @param price The Amount of money/item to enter the world.
     * @return Result of setting property.
     * @since 5.0
     */
    Try<Void> setPrice(double price);

    /**
     * Gets whether or not PVP is enabled in this world in some form (fake or not).
     *
     * @return True if players can take damage from other players.
     * @since 5.0
     */
    boolean getPvp();

    /**
     * Turn pvp on or off. This setting is used to set the world's PVP mode.
     *
     * @param pvp True to enable PVP damage, false to disable it.
     * @return Result of setting property.
     * @since 5.0
     */
    Try<Void> setPvp(boolean pvp);

    /**
     * Gets the world name players will respawn in if they die in this one.
     *
     * @return A world name that exists on the server.
     * @since 5.0
     */
    String getRespawnWorldName();

    /**
     * Gets the world players will respawn in if they die in this one.
     *
     * @return A world that exists on the server.
     * @since 5.0
     */
    @Nullable World getRespawnWorld();

    /**
     * Sets the world players will respawn in if they die in this one.
     * Returns true upon success, false upon failure.
     *
     * @param respawnWorld The name of a world that exists on the server.
     * @return Result of setting property.
     * @since 5.0
     */
    Try<Void> setRespawnWorld(World respawnWorld);

    /**
     * Sets the world players will respawn in if they die in this one.
     * Returns true upon success, false upon failure.
     *
     * @param respawnWorld The name of a world that exists on the server.
     * @return Result of setting property.
     * @since 5.0
     */
    Try<Void> setRespawnWorld(MultiverseWorld respawnWorld);

    /**
     * Sets the world players will respawn in if they die in this one.
     * Returns true upon success, false upon failure.
     *
     * @param respawnWorld The name of a world that exists on the server.
     * @return Result of setting property.
     * @since 5.0
     */
    Try<Void> setRespawnWorld(String respawnWorld);

    /**
     * Gets the scaling value of this world.Really only has an effect if you use
     * Multiverse-NetherPortals.
     *
     * @return This world's non-negative, non-zero scale.
     * @since 5.0
     */
    double getScale();

    /**
     * Sets the scale of this world. Really only has an effect if you use
     * Multiverse-NetherPortals.
     *
     * @param scale A scaling value, cannot be negative or 0.
     * @return Result of setting property.
     * @since 5.0
     */
    Try<Void> setScale(double scale);

    /**
     * Gets the world seed of this world. This cannot be changed after world creation.
     *
     * @return The Long version of the seed.
     * @since 5.0
     */
    long getSeed();

    /**
     * Gets the spawn location of this world.
     *
     * @return The spawn location of this world.
     * @since 5.0
     */
    SpawnLocation getSpawnLocation();

    /**
     * Sets the spawn location for a world.
     *
     * @param spawnLocation The spawn location for a world.
     * @return Result of setting property.
     * @since 5.0
     */
    Try<Void> setSpawnLocation(Location spawnLocation);

    /**
     * Sets the spawn location for a world.
     *
     * @param spawnLocation The spawn location for a world.
     * @return Result of setting property.
     * @since 5.0
     */
    Try<Void> setSpawnLocation(SpawnLocation spawnLocation);

    /**
     * Gets whether or not animals are allowed to spawn in this world.
     *
     * @return True if ANY animal can, false if no animals can spawn.
     * @since 5.0
     */
    boolean getSpawningAnimals();

    /**
     * Sets whether or not animals can spawn.
     * <br/>
     * If there are values in {@link #getSpawningAnimalsExceptions()} and this is false,
     * those animals become the exceptions, and will spawn
     *
     * @param spawningAnimals True to allow spawning of monsters, false to prevent.
     * @return Result of setting property.
     * @since 5.0
     */
    Try<Void> setSpawningAnimals(boolean spawningAnimals);

    /**
     * Gets the amount of ticks between animal spawns.
     *
     * @return The amount of ticks between animal spawns.
     * @since 5.0
     */
    int getSpawningAnimalsTicks();

    /**
     * Sets the amount of ticks between animal spawns. Set to -1 to use bukkit default.
     *
     * @param spawningAnimalsAmount The amount of ticks between animal spawns.
     * @return Result of setting property.
     * @since 5.0
     */
    Try<Void> setSpawningAnimalsTicks(int spawningAnimalsAmount);

    /**
     * Returns a list of animals. This list always negates the {@link #getSpawningAnimals()} result.
     *
     * @return A list of animals that will spawn if {@link #getSpawningAnimals()} is false.
     * @since 5.0
     */
    List<String> getSpawningAnimalsExceptions();

    /**
     * Sets the list of animals that will spawn if {@link #getSpawningAnimals()} is false.
     *
     * @param spawningAnimalsExceptions The list of animals that will spawn if {@link #getSpawningAnimals()} is false.
     * @return Result of setting property.
     * @since 5.0
     */
    Try<Void> setSpawningAnimalsExceptions(List<String> spawningAnimalsExceptions);

    /**
     * Gets whether or not monsters are allowed to spawn in this world.
     *
     * @return True if ANY monster can, false if no monsters can spawn.
     * @since 5.0
     */
    boolean getSpawningMonsters();

    /**
     * Sets whether or not monsters can spawn.
     * If there are values in {@link #getSpawningMonstersExceptions()} and this is false,
     * those monsters become the exceptions, and will spawn
     *
     * @param spawningMonsters True to allow spawning of monsters, false to prevent.
     * @return Result of setting property.
     * @since 5.0
     */
    Try<Void> setSpawningMonsters(boolean spawningMonsters);

    /**
     * Gets the amount of ticks between monster spawns.
     *
     * @return The amount of ticks between monster spawns.
     * @since 5.0
     */
    int getSpawningMonstersTicks();

    /**
     * Sets the amount of ticks between monster spawns. Set to -1 to use bukkit default.
     *
     * @param spawningMonstersAmount The amount of ticks between monster spawns.
     * @return Result of setting property.
     * @since 5.0
     */
    Try<Void> setSpawningMonstersTicks(int spawningMonstersAmount);

    /**
     * Returns a list of monsters. This list always negates the {@link #getSpawningMonsters()} result.
     *
     * @return A list of monsters that will spawn if {@link #getSpawningMonsters()} is false.
     * @since 5.0
     */
    List<String> getSpawningMonstersExceptions();

    /**
     * Sets the list of monsters that will spawn if {@link #getSpawningMonsters()} is false.
     *
     * @param spawningMonstersExceptions The list of monsters that will spawn if {@link #getSpawningMonsters()}
     *                                   is false.
     * @return Result of setting property.
     * @since 5.0
     */
    Try<Void> setSpawningMonstersExceptions(List<String> spawningMonstersExceptions);

    /**
     * Gets a list of all the worlds that players CANNOT travel to from this world,regardless of their access
     * permissions.
     *
     * @return A List of world names.
     * @since 5.0
     */
    List<String> getWorldBlacklist();

    /**
     * Sets the list of worlds that players CANNOT travel to from this world, regardless of their access permissions.
     *
     * @param worldBlacklist A List of world names.
     * @return Result of setting property.
     * @since 5.0
     */
    Try<Void> setWorldBlacklist(List<String> worldBlacklist);
}
