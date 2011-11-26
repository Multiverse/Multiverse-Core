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
    public BooleanConfigProperty getNewProperty(String name, boolean defaultValue, String help) {
        return new BooleanConfigProperty(this.section, name, defaultValue, help);
    }

    public BooleanConfigProperty getNewProperty(String name, boolean defaultValue, String node, String help) {
        return new BooleanConfigProperty(this.section, name, defaultValue, node, help);
    }

    // Integers
    public IntegerConfigProperty getNewProperty(String name, int defaultValue, String help) {
        return new IntegerConfigProperty(this.section, name, defaultValue, help);
    }

    public IntegerConfigProperty getNewProperty(String name, int defaultValue, String node, String help) {
        return new IntegerConfigProperty(this.section, name, defaultValue, node, help);
    }

    // Doubles
    public DoubleConfigProperty getNewProperty(String name, double defaultValue, String help) {
        return new DoubleConfigProperty(this.section, name, defaultValue, help);
    }

    public DoubleConfigProperty getNewProperty(String name, double defaultValue, String node, String help) {
        return new DoubleConfigProperty(this.section, name, defaultValue, node, help);
    }

    // Strings
    public StringConfigProperty getNewProperty(String name, String defaultValue, String help) {
        return new StringConfigProperty(this.section, name, defaultValue, help);
    }

    public StringConfigProperty getNewProperty(String name, String defaultValue, String node, String help) {
        return new StringConfigProperty(this.section, name, defaultValue, node, help);
    }

    // Colors
    public ColorConfigProperty getNewProperty(String name, EnglishChatColor defaultValue, String help) {
        return new ColorConfigProperty(this.section, name, defaultValue, help);
    }

    public ColorConfigProperty getNewProperty(String name, EnglishChatColor defaultValue, String node, String help) {
        return new ColorConfigProperty(this.section, name, defaultValue, node, help);
    }

    // Difficulty
    public DifficultyConfigProperty getNewProperty(String name, Difficulty defaultValue, String help) {
        return new DifficultyConfigProperty(this.section, name, defaultValue, help);
    }

    public DifficultyConfigProperty getNewProperty(String name, Difficulty defaultValue, String node, String help) {
        return new DifficultyConfigProperty(this.section, name, defaultValue, node, help);
    }

    // GameMode
    public GameModeConfigProperty getNewProperty(String name, GameMode defaultValue, String help) {
        return new GameModeConfigProperty(this.section, name, defaultValue, help);
    }

    public GameModeConfigProperty getNewProperty(String name, GameMode defaultValue, String node, String help) {
        return new GameModeConfigProperty(this.section, name, defaultValue, node, help);
    }

    // GameMode
    public LocationConfigProperty getNewProperty(String name, Location defaultValue, String help) {
        return new LocationConfigProperty(this.section, name, defaultValue, help);
    }

    public LocationConfigProperty getNewProperty(String name, Location defaultValue, String node, String help) {
        return new LocationConfigProperty(this.section, name, defaultValue, node, help);
    }
}
