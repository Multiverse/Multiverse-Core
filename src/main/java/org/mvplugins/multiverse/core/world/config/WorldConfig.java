package org.mvplugins.multiverse.core.world.config;

import java.util.Collection;
import java.util.List;

import com.dumptruckman.minecraft.util.Logging;
import io.vavr.control.Try;
import org.bukkit.Difficulty;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import org.mvplugins.multiverse.core.MultiverseCore;
import org.mvplugins.multiverse.core.configuration.handle.ConfigurationSectionHandle;
import org.mvplugins.multiverse.core.configuration.handle.StringPropertyHandle;
import org.mvplugins.multiverse.core.configuration.migration.BooleanMigratorAction;
import org.mvplugins.multiverse.core.configuration.migration.ConfigMigrator;
import org.mvplugins.multiverse.core.configuration.migration.IntegerMigratorAction;
import org.mvplugins.multiverse.core.configuration.migration.LongMigratorAction;
import org.mvplugins.multiverse.core.configuration.migration.MoveMigratorAction;
import org.mvplugins.multiverse.core.configuration.migration.NullStringMigratorAction;
import org.mvplugins.multiverse.core.configuration.migration.VersionMigrator;
import org.mvplugins.multiverse.core.world.LoadedMultiverseWorld;

/**
 * Represents a world configuration.
 */
public final class WorldConfig {

    private final String worldName;
    private final WorldConfigNodes configNodes;
    private final ConfigurationSectionHandle configHandle;
    private final StringPropertyHandle stringPropertyHandle;

    WorldConfig(
            @NotNull String worldName,
            @NotNull ConfigurationSection configSection,
            @NotNull MultiverseCore multiverseCore) {
        this.worldName = worldName;
        this.configNodes = new WorldConfigNodes(multiverseCore);
        this.configHandle = ConfigurationSectionHandle.builder(configSection, configNodes.getNodes())
                .logger(Logging.getLogger())
                .migrator(ConfigMigrator.builder(configNodes.VERSION)
                        .addVersionMigrator(initialVersionMigrator())
                        .build())
                .build();
        this.stringPropertyHandle = new StringPropertyHandle(configHandle);
        load();
    }

    private VersionMigrator initialVersionMigrator() {
        return VersionMigrator.builder(1.0)
                .addAction(MoveMigratorAction.of("adjustSpawn", "adjust-spawn"))
                .addAction(BooleanMigratorAction.of("adjust-spawn"))
                .addAction(MoveMigratorAction.of("allowFlight", "allow-flight"))
                .addAction(BooleanMigratorAction.of("allow-flight"))
                .addAction(MoveMigratorAction.of("allowWeather", "allow-weather"))
                .addAction(BooleanMigratorAction.of("allow-weather"))
                .addAction(MoveMigratorAction.of("autoHeal", "auto-heal"))
                .addAction(BooleanMigratorAction.of("auto-heal"))
                .addAction(MoveMigratorAction.of("autoLoad", "auto-load"))
                .addAction(BooleanMigratorAction.of("auto-load"))
                .addAction(MoveMigratorAction.of("bedRespawn", "bed-respawn"))
                .addAction(BooleanMigratorAction.of("bed-respawn"))
                //.addAction(MoveMigratorAction.of("difficulty", "difficulty"))
                .addAction(MoveMigratorAction.of("entryfee.amount", "entry-fee.amount"))
                .addAction(MoveMigratorAction.of("entryfee.currency", "entry-fee.currency"))
                //.addAction(MoveMigratorAction.of("environment", "environment"))
                .addAction(MoveMigratorAction.of("gameMode", "gamemode"))
                //.addAction(MoveMigratorAction.of("generator", "generator"))
                .addAction(NullStringMigratorAction.of("generator"))
                //.addAction(MoveMigratorAction.of("hidden", "hidden"))
                .addAction(BooleanMigratorAction.of("hidden"))
                //.addAction(MoveMigratorAction.of("hunger", "hunger"))
                .addAction(BooleanMigratorAction.of("hunger"))
                .addAction(MoveMigratorAction.of("keepSpawnInMemory", "keep-spawn-in-memory"))
                .addAction(BooleanMigratorAction.of("keep-spawn-in-memory"))
                .addAction(MoveMigratorAction.of("playerLimit", "player-limit"))
                .addAction(IntegerMigratorAction.of("player-limit"))
                .addAction(MoveMigratorAction.of("portalForm", "portal-form"))
                //.addAction(MoveMigratorAction.of("pvp", "pvp"))
                .addAction(BooleanMigratorAction.of("pvp"))
                .addAction(MoveMigratorAction.of("respawnWorld", "respawn-world"))
                //.addAction(MoveMigratorAction.of("scale", "scale"))
                //.addAction(MoveMigratorAction.of("seed", "seed"))
                .addAction(LongMigratorAction.of("seed"))
                .addAction(MoveMigratorAction.of("spawnLocation", "spawn-location"))
                //.addAction(MoveMigratorAction.of("spawning.animals.spawn", "spawning.animals.spawn"))
                .addAction(BooleanMigratorAction.of("spawning.animals.spawn"))
                .addAction(MoveMigratorAction.of("spawning.animals.amount", "spawning.animals.tick-rate"))
                .addAction(IntegerMigratorAction.of("spawning.animals.tick-rate"))
                //.addAction(MoveMigratorAction.of("spawning.animals.exceptions", "spawning.animals.exceptions"))
                //.addAction(MoveMigratorAction.of("spawning.monsters.spawn", "spawning.monsters.spawn"))
                .addAction(BooleanMigratorAction.of("spawning.monsters.spawn"))
                .addAction(MoveMigratorAction.of("spawning.monsters.amount", "spawning.monsters.tick-rate"))
                .addAction(IntegerMigratorAction.of("spawning.monsters.tick-rate"))
                //.addAction(MoveMigratorAction.of("spawning.monsters.exceptions", "spawning.monsters.exceptions"))
                .addAction(MoveMigratorAction.of("worldBlacklist", "world-blacklist"))
                .addAction(new LegacyAliasMigrator())
                .build();
    }

    public Try<Void> load() {
        return configHandle.load();
    }

    public Try<Void> load(ConfigurationSection section) {
        return configHandle.load(section);
    }

    public String getWorldName() {
        return worldName;
    }

    public Collection<String> getConfigurablePropertyNames() {
        return configNodes.getNodes().getNames();
    }

    public Try<Object> getProperty(String name) {
        return stringPropertyHandle.getProperty(name);
    }

    public Try<Void> setProperty(String name, Object value) {
        return stringPropertyHandle.setProperty(name, value);
    }

    public boolean getAdjustSpawn() {
        return configHandle.get(configNodes.ADJUST_SPAWN);
    }

    public Try<Void> setAdjustSpawn(boolean adjustSpawn) {
        return configHandle.set(configNodes.ADJUST_SPAWN, adjustSpawn);
    }

    public @Nullable String getAlias() {
        return configHandle.get(configNodes.ALIAS);
    }

    public Try<Void> setAlias(String alias) {
        return configHandle.set(configNodes.ALIAS, alias);
    }

    public boolean getAllowFlight() {
        return configHandle.get(configNodes.ALLOW_FLIGHT);
    }

    public Try<Void> setAllowFlight(boolean allowFlight) {
        return configHandle.set(configNodes.ALLOW_FLIGHT, allowFlight);
    }

    public boolean getAllowWeather() {
        return configHandle.get(configNodes.ALLOW_WEATHER);
    }

    public Try<Void> setAllowWeather(boolean allowWeather) {
        return configHandle.set(configNodes.ALLOW_WEATHER, allowWeather);
    }

    public boolean getAutoHeal() {
        return configHandle.get(configNodes.AUTO_HEAL);
    }

    public Try<Void> setAutoHeal(boolean autoHeal) {
        return configHandle.set(configNodes.AUTO_HEAL, autoHeal);
    }

    public boolean getAutoLoad() {
        return configHandle.get(configNodes.AUTO_LOAD);
    }

    public Try<Void> setAutoLoad(boolean autoLoad) {
        return configHandle.set(configNodes.AUTO_LOAD, autoLoad);
    }

    public boolean getBedRespawn() {
        return configHandle.get(configNodes.BED_RESPAWN);
    }

    public Try<Void> setBedRespawn(boolean bedRespawn) {
        return configHandle.set(configNodes.BED_RESPAWN, bedRespawn);
    }

    public Difficulty getDifficulty() {
        return configHandle.get(configNodes.DIFFICULTY);
    }

    public Try<Void> setDifficulty(Difficulty difficulty) {
        return configHandle.set(configNodes.DIFFICULTY, difficulty);
    }

    public double getEntryFeeAmount() {
        return configHandle.get(configNodes.ENTRY_FEE_AMOUNT);
    }

    public Try<Void> setEntryFeeAmount(double entryFeeAmount) {
        return configHandle.set(configNodes.ENTRY_FEE_AMOUNT, entryFeeAmount);
    }

    public Material getEntryFeeCurrency() {
        return configHandle.get(configNodes.ENTRY_FEE_CURRENCY);
    }

    public Try<Void> setEntryFeeCurrency(Material entryFeeCurrency) {
        return configHandle.set(configNodes.ENTRY_FEE_CURRENCY, entryFeeCurrency);
    }

    public World.Environment getEnvironment() {
        return configHandle.get(configNodes.ENVIRONMENT);
    }

    public Try<Void> setEnvironment(World.Environment environment) {
        return configHandle.set(configNodes.ENVIRONMENT, environment);
    }

    public GameMode getGameMode() {
        return configHandle.get(configNodes.GAMEMODE);
    }

    public Try<Void> setGameMode(GameMode gamemode) {
        return configHandle.set(configNodes.GAMEMODE, gamemode);
    }

    public @Nullable String getGenerator() {
        return configHandle.get(configNodes.GENERATOR);
    }

    public Try<Void> setGenerator(String generator) {
        return configHandle.set(configNodes.GENERATOR, generator);
    }

    public boolean isHidden() {
        return configHandle.get(configNodes.HIDDEN);
    }

    public Try<Void> setHidden(boolean hidden) {
        return configHandle.set(configNodes.HIDDEN, hidden);
    }

    public boolean getHunger() {
        return configHandle.get(configNodes.HUNGER);
    }

    public Try<Void> setHunger(boolean hunger) {
        return configHandle.set(configNodes.HUNGER, hunger);
    }

    public boolean getKeepSpawnInMemory() {
        return configHandle.get(configNodes.KEEP_SPAWN_IN_MEMORY);
    }

    public Try<Void> setKeepSpawnInMemory(boolean keepSpawnInMemory) {
        return configHandle.set(configNodes.KEEP_SPAWN_IN_MEMORY, keepSpawnInMemory);
    }

    public int getPlayerLimit() {
        return configHandle.get(configNodes.PLAYER_LIMIT);
    }

    public Try<Void> setPlayerLimit(int playerLimit) {
        return configHandle.set(configNodes.PLAYER_LIMIT, playerLimit);
    }

    public AllowedPortalType getPortalForm() {
        return configHandle.get(configNodes.PORTAL_FORM);
    }

    public Try<Void> setPortalForm(AllowedPortalType portalForm) {
        return configHandle.set(configNodes.PORTAL_FORM, portalForm);
    }

    public boolean getPvp() {
        return configHandle.get(configNodes.PVP);
    }

    public Try<Void> setPvp(boolean pvp) {
        return configHandle.set(configNodes.PVP, pvp);
    }

    public String getRespawnWorld() {
        return configHandle.get(configNodes.RESPAWN_WORLD);
    }

    public Try<Void> setRespawnWorld(String respawnWorld) {
        return configHandle.set(configNodes.RESPAWN_WORLD, respawnWorld);
    }

    public double getScale() {
        return configHandle.get(configNodes.SCALE);
    }

    public Try<Void> setScale(double scale) {
        return configHandle.set(configNodes.SCALE, scale);
    }

    public long getSeed() {
        return configHandle.get(configNodes.SEED);
    }

    public Try<Void> setSeed(long seed) {
        return configHandle.set(configNodes.SEED, seed);
    }

    public Location getSpawnLocation() {
        return configHandle.get(configNodes.SPAWN_LOCATION);
    }

    public Try<Void> setSpawnLocation(Location spawnLocation) {
        return configHandle.set(configNodes.SPAWN_LOCATION, spawnLocation);
    }

    public boolean getSpawningAnimals() {
        return configHandle.get(configNodes.SPAWNING_ANIMALS);
    }

    public Try<Void> setSpawningAnimals(boolean spawningAnimals) {
        return configHandle.set(configNodes.SPAWNING_ANIMALS, spawningAnimals);
    }

    public int getSpawningAnimalsTicks() {
        return configHandle.get(configNodes.SPAWNING_ANIMALS_TICKS);
    }

    public Try<Void> setSpawningAnimalsTicks(int spawningAnimalsAmount) {
        return configHandle.set(configNodes.SPAWNING_ANIMALS_TICKS, spawningAnimalsAmount);
    }

    public List<String> getSpawningAnimalsExceptions() {
        return configHandle.get(configNodes.SPAWNING_ANIMALS_EXCEPTIONS);
    }

    public Try<Void> setSpawningAnimalsExceptions(List<String> spawningAnimalsExceptions) {
        return configHandle.set(configNodes.SPAWNING_ANIMALS_EXCEPTIONS, spawningAnimalsExceptions);
    }

    public boolean getSpawningMonsters() {
        return configHandle.get(configNodes.SPAWNING_MONSTERS);
    }

    public Try<Void> setSpawningMonsters(boolean spawningMonsters) {
        return configHandle.set(configNodes.SPAWNING_MONSTERS, spawningMonsters);
    }

    public int getSpawningMonstersTicks() {
        return configHandle.get(configNodes.SPAWNING_MONSTERS_TICKS);
    }

    public Try<Void> setSpawningMonstersTicks(int spawningMonstersAmount) {
        return configHandle.set(configNodes.SPAWNING_MONSTERS_TICKS, spawningMonstersAmount);
    }

    public List<String> getSpawningMonstersExceptions() {
        return configHandle.get(configNodes.SPAWNING_MONSTERS_EXCEPTIONS);
    }

    public Try<Void> setSpawningMonstersExceptions(List<String> spawningMonstersExceptions) {
        return configHandle.set(configNodes.SPAWNING_MONSTERS_EXCEPTIONS, spawningMonstersExceptions);
    }

    public List<String> getWorldBlacklist() {
        return configHandle.get(configNodes.WORLD_BLACKLIST);
    }

    public Try<Void> setWorldBlacklist(List<String> worldBlacklist) {
        return configHandle.set(configNodes.WORLD_BLACKLIST, worldBlacklist);
    }

    public void setMVWorld(@NotNull LoadedMultiverseWorld world) {
        configNodes.setWorld(world);
    }

    public boolean hasMVWorld() {
        return configNodes.getWorld() != null;
    }

    public void deferenceMVWorld() {
        configNodes.setWorld(null);
    }
}
