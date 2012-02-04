/******************************************************************************
 * Multiverse 2 Copyright (c) the Multiverse Team 2012.                       *
 * Multiverse 2 is licensed under the BSD License.                            *
 * For more information please check the README.md file included              *
 * with this project.                                                         *
 ******************************************************************************/

package com.onarandombox.MultiverseCore.configuration;

import com.onarandombox.MultiverseCore.enums.AllowedPortalType;
import com.onarandombox.MultiverseCore.enums.EnglishChatColor;
import org.bukkit.Difficulty;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;

/** A factory to create config properties for a given world. */
public class ConfigPropertyFactory {
    private ConfigurationSection section;

    public ConfigPropertyFactory(ConfigurationSection section) {
        this.section = section;
    }

    // Booleans
    /**
     * Constructs a new ConfigProperty.
     *
     * @param name The name of this ConfigProperty.
     * @param defaultValue The default-value.
     * @param help The text that's displayed when a user failed to set the property.
     * @return The ConfigProperty.
     */
    public BooleanConfigProperty getNewProperty(String name, boolean defaultValue, String help) {
        return new BooleanConfigProperty(this.section, name, defaultValue, help);
    }

    /**
     * Constructs a new ConfigProperty.
     *
     * @param name The name of this ConfigProperty.
     * @param defaultValue The default-value.
     * @param node The name of the configuration-node this ConfigProperty will be stored as.
     * @param help The text that's displayed when a user failed to set the property.
     * @return The ConfigProperty.
     */
    public BooleanConfigProperty getNewProperty(String name, boolean defaultValue, String node, String help) {
        return new BooleanConfigProperty(this.section, name, defaultValue, node, help);
    }

    /**
     * Constructs a new ActiveBooleanConfigProperty
     *
     * This property will execute 'method' after it has been successfully set.
     *
     * @param name The name of this ConifgProperty
     * @param defaultValue The default value.
     * @param help What string is shown for help.
     * @param node The name of the configuration-node this ConfigProperty will be stored as.
     * @param method The method that should be executed.
     * @return The ActiveStringConfigProperty
     */
    public BooleanConfigProperty getNewProperty(String name, boolean defaultValue, String help, String node, String method) {
        return new BooleanConfigProperty(this.section, name, defaultValue, help, node, method);
    }

    // Integers
    /**
     * Constructs a new ConfigProperty.
     *
     * @param name The name of this ConfigProperty.
     * @param defaultValue The default-value.
     * @param help The text that's displayed when a user failed to set the property.
     * @return The ConfigProperty.
     */
    public IntegerConfigProperty getNewProperty(String name, int defaultValue, String help) {
        return new IntegerConfigProperty(this.section, name, defaultValue, help);
    }

    /**
     * Constructs a new ConfigProperty.
     *
     * @param name The name of this ConfigProperty.
     * @param defaultValue The default-value.
     * @param node The name of the configuration-node this ConfigProperty will be stored as.
     * @param help The text that's displayed when a user failed to set the property.
     * @return The ConfigProperty.
     */
    public IntegerConfigProperty getNewProperty(String name, int defaultValue, String node, String help) {
        return new IntegerConfigProperty(this.section, name, defaultValue, node, help);
    }

    // Doubles
    /**
     * Constructs a new ConfigProperty.
     *
     * @param name The name of this ConfigProperty.
     * @param defaultValue The default-value.
     * @param help The text that's displayed when a user failed to set the property.
     * @return The ConfigProperty.
     */
    public DoubleConfigProperty getNewProperty(String name, double defaultValue, String help) {
        return new DoubleConfigProperty(this.section, name, defaultValue, help);
    }

    /**
     * Constructs a new ConfigProperty.
     *
     * @param name The name of this ConfigProperty.
     * @param defaultValue The default-value.
     * @param node The name of the configuration-node this ConfigProperty will be stored as.
     * @param help The text that's displayed when a user failed to set the property.
     * @return The ConfigProperty.
     */
    public DoubleConfigProperty getNewProperty(String name, double defaultValue, String node, String help) {
        return new DoubleConfigProperty(this.section, name, defaultValue, node, help);
    }

    /**
     * Constructs a new ConfigProperty.
     *
     * @param name The name of this ConfigProperty.
     * @param defaultValue The default-value.
     * @param node The name of the configuration-node this ConfigProperty will be stored as.
     * @param help The text that's displayed when a user failed to set the property.
     * @param method The name of the method that's used to set this property.
     * @return The ConfigProperty.
     */
    public DoubleConfigProperty getNewProperty(String name, double defaultValue, String node, String help, String method) {
        return new DoubleConfigProperty(this.section, name, defaultValue, node, help, method);
    }

    // Strings
    /**
     * Constructs a new ConfigProperty.
     *
     * @param name The name of this ConfigProperty.
     * @param defaultValue The default-value.
     * @param help The text that's displayed when a user failed to set the property.
     * @return The ConfigProperty.
     */
    public StringConfigProperty getNewProperty(String name, String defaultValue, String help) {
        return new StringConfigProperty(this.section, name, defaultValue, help);
    }

    /**
     * Constructs a new ConfigProperty.
     *
     * @param name The name of this ConfigProperty.
     * @param defaultValue The default-value.
     * @param node The name of the configuration-node this ConfigProperty will be stored as.
     * @param help The text that's displayed when a user failed to set the property.
     * @return The ConfigProperty.
     */
    public StringConfigProperty getNewProperty(String name, String defaultValue, String node, String help) {
        return new StringConfigProperty(this.section, name, defaultValue, node, help);
    }

    // Colors
    /**
     * Constructs a new ConfigProperty.
     *
     * @param name The name of this ConfigProperty.
     * @param defaultValue The default-value.
     * @param help The text that's displayed when a user failed to set the property.
     * @return The ConfigProperty.
     */
    public ColorConfigProperty getNewProperty(String name, EnglishChatColor defaultValue, String help) {
        return new ColorConfigProperty(this.section, name, defaultValue, help);
    }

    /**
     * Constructs a new ConfigProperty.
     *
     * @param name The name of this ConfigProperty.
     * @param defaultValue The default-value.
     * @param node The name of the configuration-node this ConfigProperty will be stored as.
     * @param help The text that's displayed when a user failed to set the property.
     * @return The ConfigProperty.
     */
    public ColorConfigProperty getNewProperty(String name, EnglishChatColor defaultValue, String node, String help) {
        return new ColorConfigProperty(this.section, name, defaultValue, node, help);
    }

    // Difficulty
    /**
     * Constructs a new ConfigProperty.
     *
     * @param name The name of this ConfigProperty.
     * @param defaultValue The default-value.
     * @param help The text that's displayed when a user failed to set the property.
     * @return The ConfigProperty.
     */
    public DifficultyConfigProperty getNewProperty(String name, Difficulty defaultValue, String help) {
        return new DifficultyConfigProperty(this.section, name, defaultValue, help);
    }

    /**
     * Constructs a new ConfigProperty.
     *
     * @param name The name of this ConfigProperty.
     * @param defaultValue The default-value.
     * @param node The name of the configuration-node this ConfigProperty will be stored as.
     * @param help The text that's displayed when a user failed to set the property.
     * @return The ConfigProperty.
     */
    public DifficultyConfigProperty getNewProperty(String name, Difficulty defaultValue, String node, String help) {
        return new DifficultyConfigProperty(this.section, name, defaultValue, node, help);
    }

    // GameMode
    /**
     * Constructs a new ConfigProperty.
     *
     * @param name The name of this ConfigProperty.
     * @param defaultValue The default-value.
     * @param help The text that's displayed when a user failed to set the property.
     * @return The ConfigProperty.
     */
    public GameModeConfigProperty getNewProperty(String name, GameMode defaultValue, String help) {
        return new GameModeConfigProperty(this.section, name, defaultValue, help);
    }

    /**
     * Constructs a new ConfigProperty.
     *
     * @param name The name of this ConfigProperty.
     * @param defaultValue The default-value.
     * @param node The name of the configuration-node this ConfigProperty will be stored as.
     * @param help The text that's displayed when a user failed to set the property.
     * @return The ConfigProperty.
     */
    public GameModeConfigProperty getNewProperty(String name, GameMode defaultValue, String node, String help) {
        return new GameModeConfigProperty(this.section, name, defaultValue, node, help);
    }

    // Location
    /**
     * Constructs a new LocationConfigProperty.
     *
     * @param name The name of this ConfigProperty.
     * @param defaultValue The default-value.
     * @param help The text that's displayed when a user failed to set the property.
     * @return The ConfigProperty.
     */
    public LocationConfigProperty getNewProperty(String name, Location defaultValue, String help) {
        return new LocationConfigProperty(this.section, name, defaultValue, help);
    }

    /**
     * Constructs a new ConfigProperty.
     *
     * @param name The name of this ConfigProperty.
     * @param defaultValue The default-value.
     * @param node The name of the configuration-node this ConfigProperty will be stored as.
     * @param help The text that's displayed when a user failed to set the property.
     * @return The ConfigProperty.
     */
    public LocationConfigProperty getNewProperty(String name, Location defaultValue, String node, String help) {
        return new LocationConfigProperty(this.section, name, defaultValue, node, help);
    }

    /**
     * Constructs a new ConfigProperty.
     *
     * @param name The name of this ConfigProperty.
     * @param defaultValue The default-value.
     * @param node The name of the configuration-node this ConfigProperty will be stored as.
     * @param help The text that's displayed when a user failed to set the property.
     * @param method The name of the method that's used to set this property.
     * @return The ConfigProperty.
     */
    public LocationConfigProperty getNewProperty(String name, Location defaultValue, String node, String help, String method) {
        return new LocationConfigProperty(this.section, name, defaultValue, node, help, method);
    }

        // GameMode
    /**
     * Constructs a new ConfigProperty.
     *
     * @param name The name of this ConfigProperty.
     * @param defaultValue The default-value.
     * @param help The text that's displayed when a user failed to set the property.
     * @return The ConfigProperty.
     */
    public PortalTypeConfigProperty getNewProperty(String name, AllowedPortalType defaultValue, String help) {
        return new PortalTypeConfigProperty(this.section, name, defaultValue, help);
    }

    /**
     * Constructs a new ActiveStringConfigProperty
     *
     * This property will execute 'method' after it has been successfully set.
     * This string will NOT be saved to the config file.
     *
     * @param name The name of this ConifgProperty
     * @param defaultValue The default value.
     * @param help What string is shown for help.
     * @param method The method that should be executed.
     * @param saveToConfig Should the variable save to the config?
     * @return The ActiveStringConfigProperty
     */
    public ActiveStringConfigProperty getNewProperty(String name, String defaultValue, String help, String method, boolean saveToConfig) {
        return new ActiveStringConfigProperty(name, defaultValue, help, method);
    }
}
