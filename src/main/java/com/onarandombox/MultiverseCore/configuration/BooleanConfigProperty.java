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
public class BooleanConfigProperty implements MVActiveConfigProperty<Boolean> {
    private String name;
    private Boolean value;
    private String configNode;
    private ConfigurationSection section;
    private String help;
    private String method;

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

    public BooleanConfigProperty(ConfigurationSection section, String name, Boolean defaultValue, String configNode, String help, String method) {
        this(section, name, defaultValue, configNode, help);
        this.method = method;
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

    /**
     * Gets the method that will be executed.
     *
     * @return The name of the method in MVWorld to be called.
     */
    @Override
    public String getMethod() {
        return this.method;
    }

    /**
     * Sets the method that will be executed.
     *
     * @param methodName The name of the method in MVWorld to be called.
     */
    @Override
    public void setMethod(String methodName) {
        this.method = methodName;
    }

    /**
     * Returns the class of the object we're looking at.
     *
     * @return the class of the object we're looking at.
     */
    @Override
    public Class<?> getPropertyClass() {
        return Boolean.class;
    }
}
