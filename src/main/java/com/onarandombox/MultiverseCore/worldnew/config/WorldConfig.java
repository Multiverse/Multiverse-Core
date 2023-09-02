package com.onarandombox.MultiverseCore.worldnew.config;

import com.dumptruckman.minecraft.util.Logging;
import com.onarandombox.MultiverseCore.configuration.handle.ConfigurationSectionHandle;
import com.onarandombox.MultiverseCore.world.configuration.AllowedPortalType;
import io.vavr.control.Try;
import org.bukkit.Difficulty;
import org.bukkit.GameMode;
import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class WorldConfig {
    private final WorldConfigNodes configNodes;
    private final ConfigurationSectionHandle configHandle;

    public WorldConfig(@NotNull final ConfigurationSection configSection) {
        this.configNodes = new WorldConfigNodes();
        // TODO: Config migration and version
        this.configHandle = ConfigurationSectionHandle.builder(configSection)
                .logger(Logging.getLogger())
                .nodes(configNodes.getNodes())
                .build();
        this.configHandle.load();
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

    public World.Environment getEnvironment() {
        return configHandle.get(configNodes.ENVIRONMENT);
    }

    public Try<Void> setEnvironment(World.Environment environment) {
        return configHandle.set(configNodes.ENVIRONMENT, environment);
    }

    public GameMode getGamemode() {
        return configHandle.get(configNodes.GAMEMODE);
    }

    public Try<Void> setGamemode(GameMode gamemode) {
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

    public List<String> getWorldBlacklist() {
        return (List<String>) configHandle.get(configNodes.WORLD_BLACKLIST);
    }

    public void setWorldBlacklist(List<String> worldBlacklist) {
        configHandle.set(configNodes.WORLD_BLACKLIST, worldBlacklist);
    }
}
