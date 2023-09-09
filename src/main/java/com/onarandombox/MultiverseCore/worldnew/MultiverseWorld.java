package com.onarandombox.MultiverseCore.worldnew;

import com.google.common.base.Strings;
import com.onarandombox.MultiverseCore.world.configuration.AllowedPortalType;
import com.onarandombox.MultiverseCore.worldnew.config.WorldConfig;
import io.vavr.control.Try;
import org.bukkit.Bukkit;
import org.bukkit.Difficulty;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.List;

public class MultiverseWorld {

    protected final String worldName;
    protected WorldConfig worldConfig;

    MultiverseWorld(String worldName, WorldConfig worldConfig) {
        this.worldName = worldName;
        this.worldConfig = worldConfig;
    }

    public String getName() {
        return worldName;
    }

    public boolean isLoaded() {
        return worldConfig.hasMVWorld();
    }

    public Collection<String> getConfigurablePropertyNames() {
        return worldConfig.getConfigurablePropertyNames();
    }

    public Try<Object> getProperty(String name) {
        return worldConfig.getProperty(name);
    }

    public Try<Void> setProperty(String name, Object value) {
        return worldConfig.setProperty(name, value);
    }

    public boolean getAdjustSpawn() {
        return worldConfig.getAdjustSpawn();
    }

    public Try<Void> setAdjustSpawn(boolean adjustSpawn) {
        return worldConfig.setAdjustSpawn(adjustSpawn);
    }

    public String getAlias() {
        return Strings.isNullOrEmpty(worldConfig.getAlias()) ? worldName : worldConfig.getAlias();
    }

    public Try<Void> setAlias(String alias) {
        return worldConfig.setAlias(alias);
    }

    public boolean getAllowFlight() {
        return worldConfig.getAllowFlight();
    }

    public Try<Void> setAllowFlight(boolean allowFlight) {
        return worldConfig.setAllowFlight(allowFlight);
    }

    public boolean getAllowWeather() {
        return worldConfig.getAllowWeather();
    }

    public Try<Void> setAllowWeather(boolean allowWeather) {
        return worldConfig.setAllowWeather(allowWeather);
    }

    public boolean getAutoHeal() {
        return worldConfig.getAutoHeal();
    }

    public Try<Void> setAutoHeal(boolean autoHeal) {
        return worldConfig.setAutoHeal(autoHeal);
    }

    public boolean getAutoLoad() {
        return worldConfig.getAutoLoad();
    }

    public Try<Void> setAutoLoad(boolean autoLoad) {
        return worldConfig.setAutoLoad(autoLoad);
    }

    public boolean getBedRespawn() {
        return worldConfig.getBedRespawn();
    }

    public Try<Void> setBedRespawn(boolean bedRespawn) {
        return worldConfig.setBedRespawn(bedRespawn);
    }

    public Material getCurrency() {
        return worldConfig.getEntryFeeCurrency();
    }

    public Try<Void> setCurrency(Material currency) {
        return worldConfig.setEntryFeeCurrency(currency);
    }

    public Difficulty getDifficulty() {
        return worldConfig.getDifficulty();
    }

    public Try<Void> setDifficulty(Difficulty difficulty) {
        return worldConfig.setDifficulty(difficulty);
    }

    public World.Environment getEnvironment() {
        return worldConfig.getEnvironment();
    }

    public GameMode getGameMode() {
        return worldConfig.getGameMode();
    }

    public Try<Void> setGameMode(GameMode gameMode) {
        return worldConfig.setGameMode(gameMode);
    }

    public String getGenerator() {
        return worldConfig.getGenerator();
    }

    public boolean isHidden() {
        return worldConfig.isHidden();
    }

    public Try<Void> setHidden(boolean hidden) {
        return worldConfig.setHidden(hidden);
    }

    public boolean getHunger() {
        return worldConfig.getHunger();
    }

    public Try<Void> setHunger(boolean hunger) {
        return worldConfig.setHunger(hunger);
    }

    public boolean getKeepSpawnInMemory() {
        return worldConfig.getKeepSpawnInMemory();
    }

    public Try<Void> setKeepSpawnInMemory(boolean keepSpawnInMemory) {
        return worldConfig.setKeepSpawnInMemory(keepSpawnInMemory);
    }

    public int getPlayerLimit() {
        return worldConfig.getPlayerLimit();
    }

    public Try<Void> setPlayerLimit(int playerLimit) {
        return worldConfig.setPlayerLimit(playerLimit);
    }

    public AllowedPortalType getPortalForm() {
        return worldConfig.getPortalForm();
    }

    public Try<Void> setPortalForm(AllowedPortalType portalForm) {
        return worldConfig.setPortalForm(portalForm);
    }

    public double getPrice() {
        return worldConfig.getEntryFeeAmount();
    }

    public Try<Void> setPrice(double price) {
        return worldConfig.setEntryFeeAmount(price);
    }

    public boolean getPvp() {
        return worldConfig.getPvp();
    }

    public Try<Void> setPvp(boolean pvp) {
        return worldConfig.setPvp(pvp);
    }

    public String getRespawnWorldName() {
        return worldConfig.getRespawnWorld();
    }

    public @Nullable World getRespawnWorld() {
        return Bukkit.getWorld(worldConfig.getRespawnWorld());
    }

    public Try<Void> setRespawnWorld(World respawnWorld) {
        return worldConfig.setRespawnWorld(respawnWorld.getName());
    }

    public Try<Void> setRespawnWorld(MultiverseWorld respawnWorld) {
        return worldConfig.setRespawnWorld(respawnWorld.getName());
    }

    public Try<Void> setRespawnWorld(String respawnWorld) {
        return worldConfig.setRespawnWorld(respawnWorld);
    }

    public double getScale() {
        return worldConfig.getScale();
    }

    public Try<Void> setScale(double scale) {
        return worldConfig.setScale(scale);
    }

    public long getSeed() {
        return worldConfig.getSeed();
    }

    public Location getSpawnLocation() {
        return worldConfig.getSpawnLocation();
    }

    public Try<Void> setSpawnLocation(Location spawnLocation) {
        return worldConfig.setSpawnLocation(spawnLocation);
    }

    public boolean getSpawningAnimals() {
        return worldConfig.getSpawningAnimals();
    }

    public Try<Void> setSpawningAnimals(boolean spawningAnimals) {
        return worldConfig.setSpawningAnimals(spawningAnimals);
    }

    public int getSpawningAnimalsTicks() {
        return worldConfig.getSpawningAnimalsTicks();
    }

    public Try<Void> setSpawningAnimalsTicks(int spawningAnimalsAmount) {
        return worldConfig.setSpawningAnimalsTicks(spawningAnimalsAmount);
    }

    public List<String> getSpawningAnimalsExceptions() {
        return worldConfig.getSpawningAnimalsExceptions();
    }

    public Try<Void> setSpawningAnimalsExceptions(List<String> spawningAnimalsExceptions) {
        return worldConfig.setSpawningAnimalsExceptions(spawningAnimalsExceptions);
    }

    public boolean getSpawningMonsters() {
        return worldConfig.getSpawningMonsters();
    }

    public Try<Void> setSpawningMonsters(boolean spawningMonsters) {
        return worldConfig.setSpawningMonsters(spawningMonsters);
    }

    public int getSpawningMonstersTicks() {
        return worldConfig.getSpawningMonstersTicks();
    }

    public Try<Void> setSpawningMonstersTicks(int spawningMonstersAmount) {
        return worldConfig.setSpawningMonstersTicks(spawningMonstersAmount);
    }

    public List<String> getSpawningMonstersExceptions() {
        return worldConfig.getSpawningMonstersExceptions();
    }

    public Try<Void> setSpawningMonstersExceptions(List<String> spawningMonstersExceptions) {
        return worldConfig.setSpawningMonstersExceptions(spawningMonstersExceptions);
    }

    public List<String> getWorldBlacklist() {
        return worldConfig.getWorldBlacklist();
    }

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

    void setWorldConfig(WorldConfig worldConfig) {
        this.worldConfig = worldConfig;
    }

    @Override
    public String toString() {
        return "MultiverseWorld{"
                + "name='" + worldName + "', "
                + "env='" + getEnvironment() + "', "
                + "gen='" + getGenerator() + "'"
                + '}';
    }
}