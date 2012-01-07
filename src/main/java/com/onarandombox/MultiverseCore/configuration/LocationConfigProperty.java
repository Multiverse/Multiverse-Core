/******************************************************************************
 * Multiverse 2 Copyright (c) the Multiverse Team 2011.                       *
 * Multiverse 2 is licensed under the BSD License.                            *
 * For more information please check the README.md file included              *
 * with this project.                                                         *
 ******************************************************************************/

package com.onarandombox.MultiverseCore.configuration;

import com.onarandombox.MultiverseCore.utils.LocationManipulation;
import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;

/**
 * A {@link Location} config-property.
 */
public class LocationConfigProperty implements MVActiveConfigProperty<Location> {
    private String name;
    private Location value;
    private String configNode;
    private ConfigurationSection section;
    private String help;
    private String method;

    public LocationConfigProperty(ConfigurationSection section, String name, Location defaultValue, String help) {
        this(section, name, defaultValue, name, help);
    }

    public LocationConfigProperty(ConfigurationSection section, String name, Location defaultValue, String configNode, String help) {
        this.name = name;
        this.configNode = configNode;
        this.section = section;
        this.help = help;
        this.value = defaultValue;
        this.setValue(this.getLocationFromConfig(defaultValue));
    }

    public LocationConfigProperty(ConfigurationSection section, String name, Location defaultValue, String configNode, String help, String method) {
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
    public Location getValue() {
        return this.value;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean parseValue(String value) {
        Location parsed = LocationManipulation.stringToLocation(value);
        return this.setValue(parsed);
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

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean setValue(Location value) {
        if (value == null) {
            return false;
        }
        this.value = value;
        this.section.set(configNode + ".x", this.value.getX());
        this.section.set(configNode + ".y", this.value.getY());
        this.section.set(configNode + ".z", this.value.getZ());
        this.section.set(configNode + ".pitch", this.value.getPitch());
        this.section.set(configNode + ".yaw", this.value.getYaw());
        this.section.set(configNode + ".world", this.value.getWorld().getName());

        return true;
    }

    private Location getLocationFromConfig(Location defaultValue) {
        double x = this.section.getDouble(this.configNode + ".x", defaultValue.getX());
        double y = this.section.getDouble(this.configNode + ".y", defaultValue.getY());
        double z = this.section.getDouble(this.configNode + ".z", defaultValue.getZ());
        double pitch = this.section.getDouble(this.configNode + ".pitch", defaultValue.getPitch());
        double yaw = this.section.getDouble(this.configNode + ".yaw", defaultValue.getYaw());
        String w = this.section.getString(this.configNode + ".world", defaultValue.getWorld().getName());
        Location found = LocationManipulation.stringToLocation(w + ":" + x + "," + y + "," + z + ":" + yaw + ":" + pitch);
        if (found != null) {
            return found;
        }
        return defaultValue;
    }

    @Override
    public String toString() {
        return LocationManipulation.strCoordsRaw(this.value);
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
        return Location.class;
    }
}
