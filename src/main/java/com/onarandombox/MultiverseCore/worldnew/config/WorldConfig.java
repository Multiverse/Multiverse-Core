package com.onarandombox.MultiverseCore.worldnew.config;

import com.dumptruckman.minecraft.util.Logging;
import com.onarandombox.MultiverseCore.configuration.handle.ConfigurationSectionHandle;
import com.onarandombox.MultiverseCore.world.configuration.AllowedPortalType;
import com.onarandombox.MultiverseCore.worldnew.MVWorld;
import io.vavr.control.Try;
import org.bukkit.Difficulty;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class WorldConfig {

    private final String worldName;
    private final WorldConfigNodes configNodes;
    private final ConfigurationSectionHandle configHandle;

    public WorldConfig(@NotNull String worldName, @NotNull final ConfigurationSection configSection) {
        this.worldName = worldName;
        this.configNodes = new WorldConfigNodes();
        // TODO: Config migration and version
        this.configHandle = ConfigurationSectionHandle.builder(configSection)
                .logger(Logging.getLogger())
                .nodes(configNodes.getNodes())
                .build();
        load();
    }

    public boolean load() {
        return configHandle.load();
    }

    public String getWorldName() {
        return worldName;
    }

    public Try<Object> getProperty(String name) {
        return configHandle.get(name);
    }

    public Try<Void> setProperty(String name, Object value) {
        return configHandle.set(name, value);
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

    public int getSpawningAnimalsAmount() {
        return configHandle.get(configNodes.SPAWNING_ANIMALS_AMOUNT);
    }

    public Try<Void> setSpawningAnimalsAmount(int spawningAnimalsAmount) {
        return configHandle.set(configNodes.SPAWNING_ANIMALS_AMOUNT, spawningAnimalsAmount);
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

    public int getSpawningMonstersAmount() {
        return configHandle.get(configNodes.SPAWNING_MONSTERS_AMOUNT);
    }

    public Try<Void> setSpawningMonstersAmount(int spawningMonstersAmount) {
        return configHandle.set(configNodes.SPAWNING_MONSTERS_AMOUNT, spawningMonstersAmount);
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

    public void setMVWorld(@NotNull MVWorld world) {
        configNodes.setMVWorld(world);
    }

    public void unloadMVWorld() {
        configNodes.unloadMVWorld();
    }
}
