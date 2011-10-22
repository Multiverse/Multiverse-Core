/******************************************************************************
 * Multiverse 2 Copyright (c) the Multiverse Team 2011.                       *
 * Multiverse 2 is licensed under the BSD License.                            *
 * For more information please check the README.md file included              *
 * with this project.                                                         *
 ******************************************************************************/

package com.onarandombox.MultiverseCore.configuration;

import org.bukkit.configuration.ConfigurationSection;

public class StringConfigProperty implements MVConfigProperty<String> {
    private String name;
    private String value;
    private String configNode;
    private ConfigurationSection section;

    public StringConfigProperty(ConfigurationSection section, String name, String defaultValue) {
        this.name = name;
        this.configNode = name;
        this.section = section;
        this.parseValue(this.section.getString(this.configNode, defaultValue));
    }

    public StringConfigProperty(ConfigurationSection section, String name, String defaultValue, String configNode) {
        this.name = name;
        this.configNode = configNode;
        this.section = section;
        this.parseValue(this.section.getString(this.configNode, defaultValue));
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public String getValue() {
        return this.value;
    }

    @Override
    public boolean parseValue(String value) {
        if (value == null) {
            return false;
        }
        this.setValue(value);
        return true;
    }

    @Override
    public String getConfigNode() {
        return this.configNode;
    }

    @Override
    public String toString() {
        return value;
    }

    @Override
    public boolean setValue(String value) {
        if (value == null) {
            return false;
        }
        this.value = value;
        this.section.set(configNode, this.value);
        return true;
    }
}
