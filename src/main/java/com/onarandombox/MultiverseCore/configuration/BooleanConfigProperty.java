/******************************************************************************
 * Multiverse 2 Copyright (c) the Multiverse Team 2011.                       *
 * Multiverse 2 is licensed under the BSD License.                            *
 * For more information please check the README.md file included              *
 * with this project.                                                         *
 ******************************************************************************/

package com.onarandombox.MultiverseCore.configuration;

import org.bukkit.configuration.ConfigurationSection;

public class BooleanConfigProperty implements MVConfigProperty<Boolean> {
    private String name;
    private Boolean value;
    private String configNode;
    private ConfigurationSection section;

    public BooleanConfigProperty(ConfigurationSection section, String name, Boolean defaultValue) {
        this.name = name;
        this.configNode = name;
        this.section = section;
        this.setValue(this.section.getBoolean(this.configNode, defaultValue));
    }

    public BooleanConfigProperty(ConfigurationSection section, String name, Boolean defaultValue, String configNode) {
        this.name = name;
        this.configNode = configNode;
        this.section = section;
        this.setValue(this.section.getBoolean(this.configNode, defaultValue));
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public Boolean getValue() {
        return this.value;
    }

    @Override
    public boolean setValue(Boolean value) {
        if (value == null) {
            return false;
        }
        this.value = value;
        this.section.set(configNode, this.value);
        return true;
    }

    @Override
    public boolean parseValue(String value) {
        if (value == null) {
            return false;
        }
        if (value.toLowerCase().equals("true") || value.toLowerCase().equals("false")) {
            this.setValue(Boolean.parseBoolean(value));
            return true;
        }
        return false;
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
