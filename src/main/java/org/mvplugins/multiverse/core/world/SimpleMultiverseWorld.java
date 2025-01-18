package org.mvplugins.multiverse.core.world;

import java.util.List;

import com.google.common.base.Strings;
import io.vavr.control.Try;
import org.bukkit.Bukkit;
import org.bukkit.Difficulty;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Biome;
import org.jetbrains.annotations.Nullable;

import org.mvplugins.multiverse.core.api.configuration.StringPropertyHandle;
import org.mvplugins.multiverse.core.api.world.MultiverseWorld;
import org.mvplugins.multiverse.core.world.config.AllowedPortalType;
import org.mvplugins.multiverse.core.world.config.SpawnLocation;
import org.mvplugins.multiverse.core.world.config.WorldConfig;

public class SimpleMultiverseWorld implements MultiverseWorld {
    /**
     * This world's name.
     */
    protected final String worldName;

    /**
     * This world's configuration.
     */
    protected WorldConfig worldConfig;

    SimpleMultiverseWorld(String worldName, WorldConfig worldConfig) {
        this.worldName = worldName;
        this.worldConfig = worldConfig;
    }

    @Override
    public String getName() {
        return worldName;
    }

    @Override
    public boolean isLoaded() {
        return worldConfig.hasMVWorld();
    }

    @Override
    public StringPropertyHandle getStringPropertyHandle() {
        return worldConfig.getStringPropertyHandle();
    }

    @Override
    public boolean getAdjustSpawn() {
        return worldConfig.getAdjustSpawn();
    }

    @Override
    public Try<Void> setAdjustSpawn(boolean adjustSpawn) {
        return worldConfig.setAdjustSpawn(adjustSpawn);
    }

    @Override
    public String getAlias() {
        return Strings.isNullOrEmpty(worldConfig.getAlias()) ? worldName : worldConfig.getAlias();
    }

    @Override
    public Try<Void> setAlias(String alias) {
        return worldConfig.setAlias(alias);
    }

    @Override
    public boolean getAllowFlight() {
        return worldConfig.getAllowFlight();
    }

    @Override
    public Try<Void> setAllowFlight(boolean allowFlight) {
        return worldConfig.setAllowFlight(allowFlight);
    }

    @Override
    public boolean getAllowWeather() {
        return worldConfig.getAllowWeather();
    }

    @Override
    public Try<Void> setAllowWeather(boolean allowWeather) {
        return worldConfig.setAllowWeather(allowWeather);
    }

    @Override
    public boolean getAnchorRespawn() {
        return worldConfig.getAnchorRespawn();
    }

    @Override
    public Try<Void> setAnchorSpawn(boolean anchorSpawn) {
        return worldConfig.setAnchorSpawn(anchorSpawn);
    }

    @Override
    public boolean getAutoHeal() {
        return worldConfig.getAutoHeal();
    }

    @Override
    public Try<Void> setAutoHeal(boolean autoHeal) {
        return worldConfig.setAutoHeal(autoHeal);
    }

    @Override
    public boolean getAutoLoad() {
        return worldConfig.getAutoLoad();
    }

    @Override
    public Try<Void> setAutoLoad(boolean autoLoad) {
        return worldConfig.setAutoLoad(autoLoad);
    }

    @Override
    public boolean getBedRespawn() {
        return worldConfig.getBedRespawn();
    }

    @Override
    public Try<Void> setBedRespawn(boolean bedRespawn) {
        return worldConfig.setBedRespawn(bedRespawn);
    }

    @Override
    public @Nullable Biome getBiome() {
        return worldConfig.getBiome();
    }

    @Override
    public Material getCurrency() {
        return worldConfig.getEntryFeeCurrency();
    }

    @Override
    public Try<Void> setCurrency(Material currency) {
        return worldConfig.setEntryFeeCurrency(currency);
    }

    @Override
    public Difficulty getDifficulty() {
        return worldConfig.getDifficulty();
    }

    @Override
    public Try<Void> setDifficulty(Difficulty difficulty) {
        return worldConfig.setDifficulty(difficulty);
    }

    @Override
    public World.Environment getEnvironment() {
        return worldConfig.getEnvironment();
    }

    @Override
    public GameMode getGameMode() {
        return worldConfig.getGameMode();
    }

    @Override
    public Try<Void> setGameMode(GameMode gameMode) {
        return worldConfig.setGameMode(gameMode);
    }

    @Override
    public String getGenerator() {
        return worldConfig.getGenerator();
    }

    @Override
    public boolean isHidden() {
        return worldConfig.isHidden();
    }

    @Override
    public Try<Void> setHidden(boolean hidden) {
        return worldConfig.setHidden(hidden);
    }

    @Override
    public boolean getHunger() {
        return worldConfig.getHunger();
    }

    @Override
    public Try<Void> setHunger(boolean hunger) {
        return worldConfig.setHunger(hunger);
    }

    @Override
    public boolean getKeepSpawnInMemory() {
        return worldConfig.getKeepSpawnInMemory();
    }

    @Override
    public Try<Void> setKeepSpawnInMemory(boolean keepSpawnInMemory) {
        return worldConfig.setKeepSpawnInMemory(keepSpawnInMemory);
    }

    @Override
    public int getPlayerLimit() {
        return worldConfig.getPlayerLimit();
    }

    @Override
    public Try<Void> setPlayerLimit(int playerLimit) {
        return worldConfig.setPlayerLimit(playerLimit);
    }

    @Override
    public AllowedPortalType getPortalForm() {
        return worldConfig.getPortalForm();
    }

    @Override
    public Try<Void> setPortalForm(AllowedPortalType portalForm) {
        return worldConfig.setPortalForm(portalForm);
    }

    @Override
    public boolean isEntryFeeEnabled() {
        return worldConfig.isEntryFeeEnabled();
    }

    @Override
    public Try<Void> setEntryFeeEnabled(boolean entryFeeEnabled) {
        return worldConfig.setEntryFeeEnabled(entryFeeEnabled);
    }

    @Override
    public double getPrice() {
        return worldConfig.getEntryFeeAmount();
    }

    @Override
    public Try<Void> setPrice(double price) {
        return worldConfig.setEntryFeeAmount(price);
    }

    @Override
    public boolean getPvp() {
        return worldConfig.getPvp();
    }

    @Override
    public Try<Void> setPvp(boolean pvp) {
        return worldConfig.setPvp(pvp);
    }

    @Override
    public String getRespawnWorldName() {
        return worldConfig.getRespawnWorld();
    }

    @Override
    public @Nullable World getRespawnWorld() {
        return Bukkit.getWorld(worldConfig.getRespawnWorld());
    }

    @Override
    public Try<Void> setRespawnWorld(World respawnWorld) {
        return worldConfig.setRespawnWorld(respawnWorld.getName());
    }

    @Override
    public Try<Void> setRespawnWorld(MultiverseWorld respawnWorld) {
        return worldConfig.setRespawnWorld(respawnWorld.getName());
    }

    @Override
    public Try<Void> setRespawnWorld(String respawnWorld) {
        return worldConfig.setRespawnWorld(respawnWorld);
    }

    @Override
    public double getScale() {
        return worldConfig.getScale();
    }

    @Override
    public Try<Void> setScale(double scale) {
        return worldConfig.setScale(scale);
    }

    @Override
    public long getSeed() {
        return worldConfig.getSeed();
    }

    @Override
    public SpawnLocation getSpawnLocation() {
        return worldConfig.getSpawnLocation();
    }

    @Override
    public Try<Void> setSpawnLocation(Location spawnLocation) {
        return setSpawnLocation(new SpawnLocation(spawnLocation));
    }

    @Override
    public Try<Void> setSpawnLocation(SpawnLocation spawnLocation) {
        //todo: Maybe check of safe location if adjust spawn is enabled
        return worldConfig.setSpawnLocation(spawnLocation);
    }

    @Override
    public boolean getSpawningAnimals() {
        return worldConfig.getSpawningAnimals();
    }

    @Override
    public Try<Void> setSpawningAnimals(boolean spawningAnimals) {
        return worldConfig.setSpawningAnimals(spawningAnimals);
    }

    @Override
    public int getSpawningAnimalsTicks() {
        return worldConfig.getSpawningAnimalsTicks();
    }

    @Override
    public Try<Void> setSpawningAnimalsTicks(int spawningAnimalsAmount) {
        return worldConfig.setSpawningAnimalsTicks(spawningAnimalsAmount);
    }

    @Override
    public List<String> getSpawningAnimalsExceptions() {
        return worldConfig.getSpawningAnimalsExceptions();
    }

    @Override
    public Try<Void> setSpawningAnimalsExceptions(List<String> spawningAnimalsExceptions) {
        return worldConfig.setSpawningAnimalsExceptions(spawningAnimalsExceptions);
    }

    @Override
    public boolean getSpawningMonsters() {
        return worldConfig.getSpawningMonsters();
    }

    @Override
    public Try<Void> setSpawningMonsters(boolean spawningMonsters) {
        return worldConfig.setSpawningMonsters(spawningMonsters);
    }

    @Override
    public int getSpawningMonstersTicks() {
        return worldConfig.getSpawningMonstersTicks();
    }

    @Override
    public Try<Void> setSpawningMonstersTicks(int spawningMonstersAmount) {
        return worldConfig.setSpawningMonstersTicks(spawningMonstersAmount);
    }

    @Override
    public List<String> getSpawningMonstersExceptions() {
        return worldConfig.getSpawningMonstersExceptions();
    }

    @Override
    public Try<Void> setSpawningMonstersExceptions(List<String> spawningMonstersExceptions) {
        return worldConfig.setSpawningMonstersExceptions(spawningMonstersExceptions);
    }

    @Override
    public List<String> getWorldBlacklist() {
        return worldConfig.getWorldBlacklist();
    }

    @Override
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
