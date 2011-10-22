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
    public BooleanConfigProperty getNewProperty(String name, boolean defaultValue) {
        return new BooleanConfigProperty(this.section, name, defaultValue);
    }

    public BooleanConfigProperty getNewProperty(String name, boolean defaultValue, String node) {
        return new BooleanConfigProperty(this.section, name, defaultValue, node);
    }

    // Integers
    public IntegerConfigProperty getNewProperty(String name, int defaultValue) {
        return new IntegerConfigProperty(this.section, name, defaultValue);
    }

    public IntegerConfigProperty getNewProperty(String name, int defaultValue, String node) {
        return new IntegerConfigProperty(this.section, name, defaultValue, node);
    }

    // Doubles
    public DoubleConfigProperty getNewProperty(String name, double defaultValue) {
        return new DoubleConfigProperty(this.section, name, defaultValue);
    }

    public DoubleConfigProperty getNewProperty(String name, double defaultValue, String node) {
        return new DoubleConfigProperty(this.section, name, defaultValue, node);
    }

    // Strings
    public StringConfigProperty getNewProperty(String name, String defaultValue) {
        return new StringConfigProperty(this.section, name, defaultValue);
    }

    public StringConfigProperty getNewProperty(String name, String defaultValue, String node) {
        return new StringConfigProperty(this.section, name, defaultValue, node);
    }

    // Colors
    public ColorConfigProperty getNewProperty(String name, EnglishChatColor defaultValue) {
        return new ColorConfigProperty(this.section, name, defaultValue);
    }

    public ColorConfigProperty getNewProperty(String name, EnglishChatColor defaultValue, String node) {
        return new ColorConfigProperty(this.section, name, defaultValue, node);
    }

    // Difficulty
    public DifficultyConfigProperty getNewProperty(String name, Difficulty defaultValue) {
        return new DifficultyConfigProperty(this.section, name, defaultValue);
    }

    public DifficultyConfigProperty getNewProperty(String name, Difficulty defaultValue, String node) {
        return new DifficultyConfigProperty(this.section, name, defaultValue, node);
    }

    // GameMode
    public GameModeConfigProperty getNewProperty(String name, GameMode defaultValue) {
        return new GameModeConfigProperty(this.section, name, defaultValue);
    }

    public GameModeConfigProperty getNewProperty(String name, GameMode defaultValue, String node) {
        return new GameModeConfigProperty(this.section, name, defaultValue, node);
    }

    // GameMode
    public LocationConfigProperty getNewProperty(String name, Location defaultValue) {
        return new LocationConfigProperty(this.section, name, defaultValue);
    }

    public LocationConfigProperty getNewProperty(String name, Location defaultValue, String node) {
        return new LocationConfigProperty(this.section, name, defaultValue, node);
    }
}
