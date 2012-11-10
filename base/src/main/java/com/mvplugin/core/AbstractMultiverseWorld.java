package com.mvplugin.core;

import com.mvplugin.core.api.MultiverseWorld;
import com.mvplugin.core.api.WorldProperties;
import com.mvplugin.core.minecraft.Difficulty;
import com.mvplugin.core.minecraft.GameMode;
import com.mvplugin.core.minecraft.PlayerPosition;
import com.mvplugin.core.minecraft.PortalType;
import com.mvplugin.core.minecraft.WorldEnvironment;
import com.mvplugin.core.minecraft.WorldType;

import java.util.List;

abstract class AbstractMultiverseWorld implements MultiverseWorld {

    private final WorldProperties worldProperties;

    protected AbstractMultiverseWorld(final WorldProperties worldProperties) {
        this.worldProperties = worldProperties;
    }

    @Override
    public WorldType getWorldType() {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public WorldEnvironment getEnvironment() {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void setEnvironment(WorldEnvironment environment) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public Difficulty getDifficulty() {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public boolean setDifficulty(Difficulty difficulty) {
        return false;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public long getSeed() {
        return getProperties().get(WorldProperties.SEED);
    }

    @Override
    public void setSeed(long seed) {
        getProperties().set(WorldProperties.SEED, seed);
    }

    @Override
    public String getGenerator() {
        return getProperties().get(WorldProperties.GENERATOR);
    }

    @Override
    public void setGenerator(final String generator) {
        getProperties().set(WorldProperties.GENERATOR, generator != null ? generator : "");
    }

    @Override
    public WorldProperties getProperties() {
        return worldProperties;
    }

    @Override
    public String getAllPropertyNames() {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public String getAlias() {
        return getProperties().get(WorldProperties.ALIAS);
    }

    @Override
    public void setAlias(final String alias) {
        getProperties().set(WorldProperties.ALIAS, alias != null ? alias : "");
    }

    @Override
    public String getColor() {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public boolean setColor(String color) {
        return false;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public String getStyle() {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public boolean setStyle(String style) {
        return false;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public String getColoredWorldString() {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public boolean canAnimalsSpawn() {
        return false;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void setAllowAnimalSpawn(boolean allowAnimalSpawn) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public List<String> getAnimalList() {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public boolean canMonstersSpawn() {
        return false;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void setAllowMonsterSpawn(boolean allowMonsterSpawn) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public List<String> getMonsterList() {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public boolean isPVPEnabled() {
        return false;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void setPVPMode(boolean pvpMode) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public boolean isHidden() {
        return getProperties().get(WorldProperties.HIDDEN);
    }

    @Override
    public void setHidden(final boolean hidden) {
        getProperties().set(WorldProperties.HIDDEN, hidden);
    }

    @Override
    public boolean isWeatherEnabled() {
        return false;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void setEnableWeather(boolean enableWeather) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public boolean isKeepingSpawnInMemory() {
        return false;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void setKeepSpawnInMemory(boolean keepSpawnInMemory) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public PlayerPosition getSpawnLocation() {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void setSpawnLocation(PlayerPosition spawnLocation) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public boolean getHunger() {
        return getProperties().get(WorldProperties.HUNGER);
    }

    @Override
    public void setHunger(final boolean hungerEnabled) {
        getProperties().set(WorldProperties.HUNGER, hungerEnabled);
    }

    @Override
    public GameMode getGameMode() {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public boolean setGameMode(GameMode gameMode) {
        return false;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public double getPrice() {
        return 0;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void setPrice(double price) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public int getCurrency() {
        return 0;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void setCurrency(int item) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public String getRespawnToWorld() {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public boolean setRespawnToWorld(String respawnWorld) {
        return false;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public double getScaling() {
        return 0;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public boolean setScaling(double scaling) {
        return false;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public boolean getAutoHeal() {
        return false;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void setAutoHeal(boolean heal) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public boolean getAdjustSpawn() {
        return false;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void setAdjustSpawn(boolean adjust) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public boolean getAutoLoad() {
        return getProperties().get(WorldProperties.AUTO_LOAD);
    }

    @Override
    public void setAutoLoad(final boolean autoLoad) {
        getProperties().set(WorldProperties.AUTO_LOAD, autoLoad);
    }

    @Override
    public boolean getBedRespawn() {
        return getProperties().get(WorldProperties.BED_RESPAWN);
    }

    @Override
    public void setBedRespawn(final boolean bedRespawn) {
        getProperties().set(WorldProperties.BED_RESPAWN, bedRespawn);
    }

    @Override
    public void setPlayerLimit(final int limit) {
        getProperties().set(WorldProperties.PLAYER_LIMIT, limit);
    }

    @Override
    public int getPlayerLimit() {
        return getProperties().get(WorldProperties.PLAYER_LIMIT);
    }

    @Override
    public String getTime() {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public boolean setTime(String timeAsString) {
        return false;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void allowPortalMaking(PortalType type) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public PortalType getAllowedPortals() {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public List<String> getWorldBlacklist() {
        return getProperties().get(WorldProperties.BLACK_LIST);
    }

    @Override
    public void save() {
        getProperties().flush();
    }
}
