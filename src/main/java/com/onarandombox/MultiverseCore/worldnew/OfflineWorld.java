package com.onarandombox.MultiverseCore.worldnew;

import com.onarandombox.MultiverseCore.world.configuration.AllowedPortalType;
import com.onarandombox.MultiverseCore.worldnew.config.WorldConfig;
import io.vavr.control.Try;
import org.bukkit.Bukkit;
import org.bukkit.Difficulty;
import org.bukkit.GameMode;
import org.bukkit.World;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class OfflineWorld {

    protected final String worldName;
    protected WorldConfig worldConfig;

    OfflineWorld(String worldName, WorldConfig worldConfig) {
        this.worldName = worldName;
        this.worldConfig = worldConfig;
    }

    public String getName() {
        return worldName;
    }

    public boolean isLoaded() {
        return worldConfig.hasMVWorld();
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
        return worldConfig.getAlias();
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

    public Try<Void> setRespawnWorld(OfflineWorld respawnWorld) {
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
}
