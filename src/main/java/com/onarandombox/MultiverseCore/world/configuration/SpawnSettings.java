package com.onarandombox.MultiverseCore.world.configuration;

import me.main__.util.SerializationConfig.Property;
import me.main__.util.SerializationConfig.SerializationConfig;
import org.bukkit.configuration.serialization.SerializableAs;

import java.util.Map;

/**
 * Spawning-Settings.
 */
@SerializableAs("MVSpawnSettings")
public class SpawnSettings extends SerializationConfig {
    @Property
    private SubSpawnSettings animals;
    @Property
    private SubSpawnSettings monsters;

    public SpawnSettings() {
        super();
    }

    public SpawnSettings(Map<String, Object> values) {
        super(values);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setDefaults() {
        animals = new SubSpawnSettings();
        monsters = new SubSpawnSettings();
    }

    /**
     * @return the animal-settings
     */
    public SubSpawnSettings getAnimalSettings() {
        return animals;
    }

    /**
     * @return the monster-settings
     */
    public SubSpawnSettings getMonsterSettings() {
        return monsters;
    }
}
