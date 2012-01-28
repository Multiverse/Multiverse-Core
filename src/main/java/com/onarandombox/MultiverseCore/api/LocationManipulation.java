package com.onarandombox.MultiverseCore.api;

import org.bukkit.Location;
import org.bukkit.entity.Vehicle;
import org.bukkit.util.Vector;

/**
 * Used to manipulate locations.
 */
public interface LocationManipulation {
    /**
     * Convert a Location into a Colon separated string to allow us to store it in text.
     * <p>
     * WORLD:X,Y,Z:yaw:pitch
     * <p>
     * The corresponding String2Loc function is {@link #stringToLocation}
     *
     * @param location The Location to save.
     * @return The location as a string in this format: WORLD:x,y,z:yaw:pitch
     */
    String locationToString(Location location);

    /**
     * This method simply does some rounding, rather than forcing a call to the server to get the blockdata.
     *
     * @param l The location to round to the block location
     * @return A rounded location.
     */
    Location getBlockLocation(Location l);

    /**
     * Returns a new location from a given string. The format is as follows:
     * <p>
     * WORLD:X,Y,Z:yaw:pitch
     * <p>
     * The corresponding Location2String function is {@link #stringToLocation}
     *
     * @param locationString The location represented as a string (WORLD:X,Y,Z:yaw:pitch)
     * @return A new location defined by the string or null if the string was invalid.
     */
    Location stringToLocation(String locationString);

    /**
     * Returns a colored string with the coords.
     *
     * @param l The {@link Location}
     * @return The {@link String}
     */
    String strCoords(Location l);

    /**
     * Converts a location to a printable readable formatted string including pitch/yaw.
     *
     * @param l The {@link Location}
     * @return The {@link String}
     */
    String strCoordsRaw(Location l);

    /**
     * Return the NESW Direction a Location is facing.
     *
     * @param location The {@link Location}
     * @return The NESW Direction
     */
    String getDirection(Location location);

    /**
     * Returns the float yaw position for the given cardinal direction.
     *
     * @param orientation The cardinal direction
     * @return The yaw
     */
    float getYaw(String orientation);

    /**
     * Returns a speed float from a given vector.
     *
     * @param v The {@link Vector}
     * @return The speed
     */
    float getSpeed(Vector v);

    /**
     * Returns a translated vector from the given direction.
     *
     * @param v The old {@link Vector}
     * @param direction The new direction
     * @return The translated {@link Vector}
     */
    Vector getTranslatedVector(Vector v, String direction);

    /**
     * Returns the next Location that a {@link Vehicle} is traveling at.
     *
     * @param v The {@link Vehicle}
     * @return The {@link Location}
     */
    Location getNextBlock(Vehicle v);
}
