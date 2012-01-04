/******************************************************************************
 * Multiverse 2 Copyright (c) the Multiverse Team 2011.                       *
 * Multiverse 2 is licensed under the BSD License.                            *
 * For more information please check the README.md file included              *
 * with this project.                                                         *
 ******************************************************************************/

package com.onarandombox.MultiverseCore.configuration;

import org.bukkit.configuration.ConfigurationSection;

/**
 * A {@link Boolean} config-property.
 */
public class BooleanConfigProperty implements MVConfigProperty<Boolean> {
    private String name;
    private Boolean value;
    private String configNode;
    private ConfigurationSection section;
    private String help;

    public BooleanConfigProperty(ConfigurationSection section, String name, Boolean defaultValue, String help) {
        this(section, name, defaultValue, name, help);
    }

    public BooleanConfigProperty(ConfigurationSection section, String name, Boolean defaultValue, String configNode, String help) {
        this.name = name;
        this.configNode = configNode;
        this.section = section;
        this.help = help;
        this.value = defaultValue;
        this.setValue(this.section.getBoolean(this.configNode, defaultValue));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getName() {
        return this.name;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Boolean getValue() {
        return this.value;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean setValue(Boolean value) {
        if (value == null) {
            return false;
        }
        this.value = value;
        this.section.set(configNode, this.value);
        return true;
    }

    /**
     * {@inheritDoc}
     */
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

    /**
     * {@inheritDoc}
     */
    @Override
    public String getConfigNode() {
        return this.configNode;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getHelp() {
        return this.help;
    }

    @Override
    public String toString() {
        return value.toString();
    }
}
