/******************************************************************************
 * Multiverse 2 Copyright (c) the Multiverse Team 2011.                       *
 * Multiverse 2 is licensed under the BSD License.                            *
 * For more information please check the README.md file included              *
 * with this project.                                                         *
 ******************************************************************************/

package com.onarandombox.MultiverseCore.configuration;

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

    // GameMode
    /**
     * Constructs a new ConfigProperty.
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
     * Constructs a new TempStringConfigProperty
     *
     * The boolean is a dummy. This is so I can differentiate from the non-temp one.
     *
     * @param name The name of this ConifgProperty
     * @param defaultValue The default value.
     * @param help What string is shown for help.
     * @param method The method that should be executed.
     * @param b Dummy.
     * @return The TempStringConfigProperty
     */
    public TempStringConfigProperty getNewProperty(String name, String defaultValue, String help, String method, boolean b) {
        return new TempStringConfigProperty(name, defaultValue, help, method);
    }
}
