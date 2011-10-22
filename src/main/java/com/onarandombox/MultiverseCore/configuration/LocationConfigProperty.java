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

public class LocationConfigProperty implements MVConfigProperty<Location> {
    private String name;
    private Location value;
    private String configNode;
    private ConfigurationSection section;

    public LocationConfigProperty(ConfigurationSection section, String name, Location defaultValue) {
        this.name = name;
        this.configNode = name;
        this.section = section;
        this.setValue(this.getLocationFromConfig(this.configNode, defaultValue));
    }

    public LocationConfigProperty(ConfigurationSection section, String name, Location defaultValue, String configNode) {
        this.name = name;
        this.configNode = configNode;
        this.section = section;
        this.setValue(this.getLocationFromConfig(this.configNode, defaultValue));
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public Location getValue() {
        return this.value;
    }

    @Override
    public boolean parseValue(String value) {
        Location parsed = LocationManipulation.getLocationFromString(value);
        return this.setValue(parsed);
    }

    @Override
    public String getConfigNode() {
        return this.configNode;
    }

    @Override
    public String toString() {
        return LocationManipulation.strCoordsRaw(this.value);
    }

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

    private Location getLocationFromConfig(String node, Location defaultValue) {
        double x = this.section.getDouble(configNode + ".x", defaultValue.getX());
        double y = this.section.getDouble(configNode + ".y", defaultValue.getY());
        double z = this.section.getDouble(configNode + ".z", defaultValue.getZ());
        double p = this.section.getDouble(configNode + ".pitch", defaultValue.getPitch());
        double yaw = this.section.getDouble(configNode + ".yaw", defaultValue.getYaw());
        String w = this.section.getString(configNode + ".world", defaultValue.getWorld().getName());
        Location found = LocationManipulation.getLocationFromString(w + ":" + x + "," + y + "," + z + ":" + p + ":" + yaw);
        if (found != null) {
            return found;
        }
        return defaultValue;
    }
}
