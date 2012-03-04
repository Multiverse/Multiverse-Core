package com.onarandombox.MultiverseCore.configuration;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import me.main__.util.SerializationConfig.Property;
import me.main__.util.SerializationConfig.SerializationConfig;

import org.bukkit.configuration.serialization.SerializableAs;

/**
 * SpawnSubSettings.
 */
@SerializableAs("MVSpawnSubSettings")
public class SubSpawnSettings extends SerializationConfig {
    @Property
    private boolean spawn;
    @Property
    private List<String> exceptions;

    public SubSpawnSettings() {
        super();
    }

    public SubSpawnSettings(Map<String, Object> values) {
        super(values);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setDefaults() {
        spawn = true;
        exceptions = new ArrayList<String>();
    }

    /**
     * @return spawn
     */
    public boolean doSpawn() {
        return spawn;
    }

    /**
     * @param spawn The new value.
     */
    public void setSpawn(boolean spawn) {
        this.spawn = spawn;
    }

    /**
     * @return the exceptions
     */
    public List<String> getExceptions() {
        return exceptions;
    }
}
