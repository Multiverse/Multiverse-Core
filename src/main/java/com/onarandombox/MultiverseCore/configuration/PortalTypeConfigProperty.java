/******************************************************************************
 * Multiverse 2 Copyright (c) the Multiverse Team 2012.                       *
 * Multiverse 2 is licensed under the BSD License.                            *
 * For more information please check the README.md file included              *
 * with this project.                                                         *
 ******************************************************************************/

package com.onarandombox.MultiverseCore.configuration;

import com.onarandombox.MultiverseCore.enums.AllowedPortalType;
import org.bukkit.configuration.ConfigurationSection;

/**
 * A {@link AllowedPortalType} config-property.
 */
public class PortalTypeConfigProperty implements MVConfigProperty<AllowedPortalType> {
    private String name;
    private AllowedPortalType value;
    private String configNode;
    private ConfigurationSection section;
    private String help;

    public PortalTypeConfigProperty(ConfigurationSection section, String name, AllowedPortalType defaultValue, String help) {
        this(section, name, defaultValue, name, help);
    }

    public PortalTypeConfigProperty(ConfigurationSection section, String name, AllowedPortalType defaultValue, String configNode, String help) {
        this.name = name;
        this.configNode = configNode;
        this.section = section;
        this.help = help;
        this.value = defaultValue;
        this.parseValue(this.section.getString(this.configNode, defaultValue.toString()));
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
    public AllowedPortalType getValue() {
        return this.value;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean setValue(AllowedPortalType value) {
        if (value == null) {
            return false;
        }
        this.value = value;
        this.section.set(configNode, this.value.toString());
        return true;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean parseValue(String value) {
        try {
            return this.setValue(AllowedPortalType.valueOf(value.toUpperCase()));
        } catch (Exception e) {
            return false;
        }
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
