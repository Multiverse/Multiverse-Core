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
import java.util.concurrent.Callable;

abstract class AbstractMultiverseWorld implements MultiverseWorld {
    private final WorldProperties worldProperties;
    private Callable<?> apiObject;

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
        return 0;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void setSeed(long seed) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public String getGenerator() {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void setGenerator(String generator) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    //@Override TODO decide about config-api relationship
    public WorldProperties getWorldProperties() {
        return worldProperties;
    }

    @Override
    public String getAllPropertyNames() {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public String getAlias() {
        return getWorldProperties().get(WorldProperties.ALIAS);
    }

    @Override
    public void setAlias(final String alias) {
        getWorldProperties().set(WorldProperties.ALIAS, alias);
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
        return false;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void setHidden(boolean hidden) {
        //To change body of implemented methods use File | Settings | File Templates.
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
        return false;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void setHunger(boolean hungerEnabled) {
        //To change body of implemented methods use File | Settings | File Templates.
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
        return false;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void setAutoLoad(boolean autoLoad) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public boolean getBedRespawn() {
        return false;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void setBedRespawn(boolean autoLoad) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void setPlayerLimit(int limit) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public int getPlayerLimit() {
        return 0;  //To change body of implemented methods use File | Settings | File Templates.
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
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }
}
