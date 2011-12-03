/******************************************************************************
 * Multiverse 2 Copyright (c) the Multiverse Team 2011.                       *
 * Multiverse 2 is licensed under the BSD License.                            *
 * For more information please check the README.md file included              *
 * with this project.                                                         *
 ******************************************************************************/

package com.onarandombox.MultiverseCore.configuration;

import org.bukkit.configuration.ConfigurationSection;

public class DoubleConfigProperty implements MVConfigProperty<Double> {
    private String name;
    private Double value;
    private String configNode;
    private ConfigurationSection section;
    private String help;

    public DoubleConfigProperty(ConfigurationSection section, String name, Double defaultValue, String help) {
        this(section, name, defaultValue, name, help);
    }

    public DoubleConfigProperty(ConfigurationSection section, String name, Double defaultValue, String configNode, String help) {
        this.name = name;
        this.configNode = configNode;
        this.section = section;
        this.help = help;
        this.value = defaultValue;
        this.setValue(this.section.getDouble(this.configNode, defaultValue));
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public Double getValue() {
        return this.value;
    }

    @Override
    public boolean setValue(Double value) {
        if (value == null) {
            return false;
        }
        this.value = value;
        this.section.set(configNode, this.value);
        return true;
    }

    @Override
    public boolean parseValue(String value) {
        try {
            this.setValue(Double.parseDouble(value));
            return true;
        } catch (NumberFormatException e) {
            return false;
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
