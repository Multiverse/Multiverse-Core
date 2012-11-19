package com.mvplugin.core;

import com.dumptruckman.minecraft.pluginbase.properties.PropertyValueException;
import com.mvplugin.core.api.MultiverseWorld;
import com.mvplugin.core.api.WorldProperties;
import com.mvplugin.core.minecraft.Difficulty;
import com.mvplugin.core.minecraft.GameMode;
import com.mvplugin.core.minecraft.PlayerPosition;
import com.mvplugin.core.minecraft.PortalType;
import com.mvplugin.core.minecraft.WorldEnvironment;

import java.util.List;

abstract class AbstractMultiverseWorld implements MultiverseWorld {

    private final WorldProperties worldProperties;

    protected AbstractMultiverseWorld(final WorldProperties worldProperties) {
        this.worldProperties = worldProperties;
    }

    @Override
    public WorldEnvironment getEnvironment() {
        return getProperties().get(WorldProperties.ENVIRONMENT);
    }

    @Override
    public void setEnvironment(final WorldEnvironment environment) {
        try {
            getProperties().set(WorldProperties.ENVIRONMENT, environment);
        } catch (PropertyValueException e) {
            e.printStackTrace();
        }
    }

    @Override
    public Difficulty getDifficulty() {
        return getProperties().get(WorldProperties.DIFFICULTY);
    }

    @Override
    public boolean setDifficulty(final Difficulty difficulty) {
        // TODO Validate?
        try {
            return getProperties().set(WorldProperties.DIFFICULTY, difficulty);
        } catch (PropertyValueException e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public long getSeed() {
        return getProperties().get(WorldProperties.SEED);
    }

    @Override
    public void setSeed(long seed) {
        try {
            getProperties().set(WorldProperties.SEED, seed);
        } catch (PropertyValueException e) {
            e.printStackTrace();
        }
    }

    @Override
    public String getGenerator() {
        return getProperties().get(WorldProperties.GENERATOR);
    }

    @Override
    public void setGenerator(final String generator) {
        try {
            getProperties().set(WorldProperties.GENERATOR, generator != null ? generator : "");
        } catch (PropertyValueException e) {
            e.printStackTrace();
        }
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
        try {
            getProperties().set(WorldProperties.ALIAS, alias != null ? alias : "");
        } catch (PropertyValueException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
    }

    @Override
    public boolean canAnimalsSpawn() {
        return getProperties().get(WorldProperties.SPAWNING)
                .get(WorldProperties.Spawning.ANIMALS)
                .get(WorldProperties.Spawning.Animals.SPAWN);
    }

    @Override
    public void setAllowAnimalSpawn(final boolean allowAnimalSpawn) {
        try {
            getProperties().get(WorldProperties.SPAWNING)
                    .get(WorldProperties.Spawning.ANIMALS)
                    .set(WorldProperties.Spawning.Animals.SPAWN, allowAnimalSpawn);
        } catch (PropertyValueException e) {
            e.printStackTrace();
        }
    }

    @Override
    public List<String> getAnimalList() {
        return getProperties().get(WorldProperties.SPAWNING)
                .get(WorldProperties.Spawning.ANIMALS)
                .get(WorldProperties.Spawning.Animals.EXCEPTIONS);
    }

    @Override
    public boolean canMonstersSpawn() {
        return getProperties().get(WorldProperties.SPAWNING)
                .get(WorldProperties.Spawning.MONSTERS)
                .get(WorldProperties.Spawning.Monsters.SPAWN);
    }

    @Override
    public void setAllowMonsterSpawn(final boolean allowMonsterSpawn) {
        try {
            getProperties().get(WorldProperties.SPAWNING)
                    .get(WorldProperties.Spawning.MONSTERS)
                    .set(WorldProperties.Spawning.Monsters.SPAWN, allowMonsterSpawn);
        } catch (PropertyValueException e) {
            e.printStackTrace();
        }
    }

    @Override
    public List<String> getMonsterList() {
        return getProperties().get(WorldProperties.SPAWNING)
                .get(WorldProperties.Spawning.ANIMALS)
                .get(WorldProperties.Spawning.Monsters.EXCEPTIONS);
    }

    @Override
    public boolean isPVPEnabled() {
        return getProperties().get(WorldProperties.PVP);
    }

    @Override
    public void setPVPMode(final boolean pvpMode) {
        try {
            getProperties().set(WorldProperties.PVP, pvpMode);
        } catch (PropertyValueException e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean isHidden() {
        return getProperties().get(WorldProperties.HIDDEN);
    }

    @Override
    public void setHidden(final boolean hidden) {
        try {
            getProperties().set(WorldProperties.HIDDEN, hidden);
        } catch (PropertyValueException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
    }

    @Override
    public boolean getPrefixChat() {
        return getProperties().get(WorldProperties.PREFIX_CHAT);
    }

    @Override
    public void setPrefixChat(final boolean prefixChat) {
        try {
            getProperties().set(WorldProperties.PREFIX_CHAT, prefixChat);
        } catch (PropertyValueException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
    }

    @Override
    public boolean isWeatherEnabled() {
        return getProperties().get(WorldProperties.ALLOW_WEATHER);
    }

    @Override
    public void setEnableWeather(final boolean enableWeather) {
        try {
            getProperties().set(WorldProperties.ALLOW_WEATHER, enableWeather);
        } catch (PropertyValueException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
    }

    @Override
    public boolean isKeepingSpawnInMemory() {
        return getProperties().get(WorldProperties.KEEP_SPAWN);
    }

    @Override
    public void setKeepSpawnInMemory(final boolean keepSpawnInMemory) {
        try {
            getProperties().set(WorldProperties.KEEP_SPAWN, keepSpawnInMemory);
        } catch (PropertyValueException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
    }

    @Override
    public PlayerPosition getSpawnLocation() {
        return getProperties().get(WorldProperties.SPAWN_LOCATION);
    }

    @Override
    public void setSpawnLocation(final PlayerPosition spawnLocation) {
        try {
            getProperties().set(WorldProperties.SPAWN_LOCATION, spawnLocation);
        } catch (PropertyValueException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
    }

    @Override
    public boolean getHunger() {
        return getProperties().get(WorldProperties.HUNGER);
    }

    @Override
    public void setHunger(final boolean hungerEnabled) {
        try {
            getProperties().set(WorldProperties.HUNGER, hungerEnabled);
        } catch (PropertyValueException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
    }

    @Override
    public GameMode getGameMode() {
        return getProperties().get(WorldProperties.GAME_MODE);
    }

    @Override
    public boolean setGameMode(final GameMode gameMode) {
        // Todo validate?
        try {
            return getProperties().set(WorldProperties.GAME_MODE, gameMode);
        } catch (PropertyValueException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
        return false;
    }

    @Override
    public double getPrice() {
        return getProperties().get(WorldProperties.ENTRY_FEE).get(WorldProperties.EntryFee.AMOUNT);
    }

    @Override
    public void setPrice(final double price) {
        try {
            getProperties().get(WorldProperties.ENTRY_FEE).set(WorldProperties.EntryFee.AMOUNT, price);
        } catch (PropertyValueException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
    }

    @Override
    public int getCurrency() {
        return getProperties().get(WorldProperties.ENTRY_FEE).get(WorldProperties.EntryFee.CURRENCY);
    }

    @Override
    public void setCurrency(final int item) {
        try {
            getProperties().get(WorldProperties.ENTRY_FEE).set(WorldProperties.EntryFee.CURRENCY, item);
        } catch (PropertyValueException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
    }

    @Override
    public String getRespawnToWorld() {
        return getProperties().get(WorldProperties.RESPAWN_WORLD);
    }

    @Override
    public boolean setRespawnToWorld(final String respawnWorld) {
        // TODO validation?
        try {
            return getProperties().set(WorldProperties.RESPAWN_WORLD, respawnWorld);
        } catch (PropertyValueException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
        return false;
    }

    @Override
    public double getScaling() {
        return getProperties().get(WorldProperties.SCALE);
    }

    @Override
    public boolean setScaling(final double scaling) {
        // TODO validation?
        try {
            return getProperties().set(WorldProperties.SCALE, scaling);
        } catch (PropertyValueException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
        return false;
    }

    @Override
    public boolean getAutoHeal() {
        return getProperties().get(WorldProperties.AUTO_HEAL);
    }

    @Override
    public void setAutoHeal(final boolean heal) {
        try {
            getProperties().set(WorldProperties.AUTO_HEAL, heal);
        } catch (PropertyValueException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
    }

    @Override
    public boolean getAdjustSpawn() {
        return getProperties().get(WorldProperties.ADJUST_SPAWN);
    }

    @Override
    public void setAdjustSpawn(final boolean adjust) {
        try {
            getProperties().set(WorldProperties.ADJUST_SPAWN, adjust);
        } catch (PropertyValueException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
    }

    @Override
    public boolean getAutoLoad() {
        return getProperties().get(WorldProperties.AUTO_LOAD);
    }

    @Override
    public void setAutoLoad(final boolean autoLoad) {
        try {
            getProperties().set(WorldProperties.AUTO_LOAD, autoLoad);
        } catch (PropertyValueException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
    }

    @Override
    public boolean getBedRespawn() {
        return getProperties().get(WorldProperties.BED_RESPAWN);
    }

    @Override
    public void setBedRespawn(final boolean bedRespawn) {
        try {
            getProperties().set(WorldProperties.BED_RESPAWN, bedRespawn);
        } catch (PropertyValueException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
    }

    @Override
    public void setPlayerLimit(final int limit) {
        try {
            getProperties().set(WorldProperties.PLAYER_LIMIT, limit);
        } catch (PropertyValueException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
    }

    @Override
    public int getPlayerLimit() {
        return getProperties().get(WorldProperties.PLAYER_LIMIT);
    }

    @Override
    public void allowPortalMaking(final PortalType type) {
        try {
            getProperties().set(WorldProperties.PORTAL_FORM, type);
        } catch (PropertyValueException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
    }

    @Override
    public PortalType getAllowedPortals() {
        return getProperties().get(WorldProperties.PORTAL_FORM);
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
