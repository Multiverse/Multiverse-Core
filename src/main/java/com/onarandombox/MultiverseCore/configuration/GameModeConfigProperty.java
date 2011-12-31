/******************************************************************************
 * Multiverse 2 Copyright (c) the Multiverse Team 2011.                       *
 * Multiverse 2 is licensed under the BSD License.                            *
 * For more information please check the README.md file included              *
 * with this project.                                                         *
 ******************************************************************************/

package com.onarandombox.MultiverseCore.configuration;

import org.bukkit.GameMode;
import org.bukkit.configuration.ConfigurationSection;

/**
 * A {@link GameMode} config-property.
 */
public class GameModeConfigProperty implements MVConfigProperty<GameMode> {
    private String name;
    private GameMode value;
    private String configNode;
    private ConfigurationSection section;
    private String help;

    public GameModeConfigProperty(ConfigurationSection section, String name, GameMode defaultValue, String help) {
        this(section, name, defaultValue, name, help);
    }

    public GameModeConfigProperty(ConfigurationSection section, String name, GameMode defaultValue, String configNode, String help) {
        this.name = name;
        this.configNode = configNode;
        this.section = section;
        this.help = help;
        this.value = defaultValue;
        this.parseValue(this.section.getString(this.configNode, defaultValue.toString()));
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public GameMode getValue() {
        return this.value;
    }

    @Override
    public boolean setValue(GameMode value) {
        if (value == null) {
            return false;
        }
        this.value = value;
        this.section.set(configNode, this.value.toString());
        return true;
    }

    @Override
    public boolean parseValue(String value) {
        try {
            return this.setValue(GameMode.getByValue(Integer.parseInt(value)));
        } catch (NumberFormatException nfe) {
            try {
                return this.setValue(GameMode.valueOf(value.toUpperCase()));
            } catch (Exception e) {
                return false;
            }
        }
    }

    @Override
    public String getConfigNode() {
        return this.configNode;
    }

    @Override
    public String toString() {
        return value.toString();
    }

    @Override
    public String getHelp() {
        return this.help;
    }
}
