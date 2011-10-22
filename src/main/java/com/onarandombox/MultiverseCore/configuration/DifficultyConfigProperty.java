/******************************************************************************
 * Multiverse 2 Copyright (c) the Multiverse Team 2011.                       *
 * Multiverse 2 is licensed under the BSD License.                            *
 * For more information please check the README.md file included              *
 * with this project.                                                         *
 ******************************************************************************/

package com.onarandombox.MultiverseCore.configuration;

import com.onarandombox.MultiverseCore.enums.EnglishChatColor;
import org.bukkit.Difficulty;
import org.bukkit.configuration.ConfigurationSection;

public class DifficultyConfigProperty implements MVConfigProperty<Difficulty> {
    private String name;
    private Difficulty value;
    private String configNode;
    private ConfigurationSection section;

    public DifficultyConfigProperty(ConfigurationSection section, String name, Difficulty defaultValue) {
        this.name = name;
        this.configNode = name;
        this.section = section;
        this.parseValue(this.section.getString(this.configNode, defaultValue.toString()));
    }

    public DifficultyConfigProperty(ConfigurationSection section, String name, Difficulty defaultValue, String configNode) {
        this.name = name;
        this.configNode = configNode;
        this.section = section;
        this.parseValue(this.section.getString(this.configNode, defaultValue.toString()));
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public Difficulty getValue() {
        return this.value;
    }

    @Override
    public boolean setValue(Difficulty value) {
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
            return this.setValue(Difficulty.getByValue(Integer.parseInt(value)));
        } catch (NumberFormatException nfe) {
            try {
                return this.setValue(Difficulty.valueOf(value.toUpperCase()));
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
}
