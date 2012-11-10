/******************************************************************************
 * Multiverse 2 Copyright (c) the Multiverse Team 2011.                       *
 * Multiverse 2 is licensed under the BSD License.                            *
 * For more information please check the README.md file included              *
 * with this project.                                                         *
 ******************************************************************************/

package com.onarandombox.MultiverseCore.api;

import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.Vector;

/**
 * A destination API for Multiverse
 * Any plugin can add these to MV and when they are, any action that uses them (portals, MVTP, etc.) can use them!
 */
public interface MVDestination {
    /**
     * Returns the identifier or prefix that is required for this destination.
     * <p>
     * Portals have a prefix of "p" for example and OpenWarp (third party plugin) uses "ow". This is derived from a
     * hash and cannot have duplicate values. Read that as your plugin cannot use 'p' because it's already used.
     * Please check the wiki when adding a custom destination!
     *
     * @return The identifier or prefix that is required for this destination.
     */
    String getIdentifier();

    /**
     * Allows you to determine if a Destination is valid for the type it thinks it is.
     * <p>
     * An example of this would be the exact destination. A valid string would be: e:0,0,0 where an invalid one would
     * be e:1:2:3. The first string would return true the second would return false. This is simply a convenience
     * method
     * and does not even NEED to be called, but it's highly recommended if you're teleporting, but it's mainly for
     * Multiverse Internal use.
     *
     * @param plugin      The plugin who the type belongs to.
     * @param destination The destination string. ex: p:MyPortal:nw
     *
     * @return True if the destination is valid, false if not.
     */
    boolean isThisType(JavaPlugin plugin, String destination);

    /**
     * Returns the location a specific entity will spawn at when being teleported to this Destination.
     * <p>
     * To just retrieve the location as it is stored you can just pass null, but be warned some destinations may return
     * null back to you if you do this. It is always safer to pass an actual entity. This is used so things like
     * minecarts can be teleported.
     * <p>
     * Do not forget to use {@link #getVelocity()} as destinations can use this too!
     *
     * @param entity The entity to be teleported.
     *
     * @return The location of the entity.
     */
    Location getLocation(Entity entity);

    /**
     * Returns the velocity vector for this destination.
     * <p>
     * Plugins wishing to fully support MVDestinations MUST implement this.
     *
     * @return A vector representing the speed/direction the player should travel when arriving
     */
    Vector getVelocity();

    /**
     * Sets the destination string.
     * <p>
     * This should be used when you want to tell this destination object about a change in where it should take people.
     * The destination param should be match the result from {@link #getIdentifier()}. A valid example would be that if
     * {@link #getIdentifier()} returned "ow" our destination string could be "ow:TownCenter" but could not be
     * "p:HomePortal"
     *
     * @param plugin      The plugin who the type belongs to.
     * @param destination The destination string. ex: p:MyPortal:nw
     */
    void setDestination(JavaPlugin plugin, String destination);

    /**
     * Returns true if the destination is valid and players will be taken to it.
     * <p>
     * Even if destinations are in the correct format (p:MyPortal) MyPortal may not exist, and therefore this would
     * return false.
     *
     * @return True if the destination is valid; false if not.
     */
    boolean isValid();

    /**
     * Gives you a general friendly description of the type of destination.
     * <p>
     * For example, the PlayerDestination sets this to "Player". You can use this to show where a player will be taken.
     *
     * @return A friendly string description of the type of destination.
     */
    String getType();

    /**
     * Gives you a specific name of the destination.
     * <p>
     * For example, the PlayerDestination sets this to The Player's Name.
     *
     * @return A friendly string stating the name of the destination.
     */
    String getName();

    /**
     * Returns a string that can easily be saved in the config that contains all the details needed to rebuild this
     * destination.
     * <p>
     * ex: e:0,0,0:50:50
     *
     * @return The savable config string.
     */
    String toString();

    /**
     * Returns the permissions string required to go here.
     * <p>
     * ex: multiverse.access.world
     * <p>
     * NOTE: This is NOT the permission to use the teleport command.
     *
     * @return the permissions string required to go here.
     */
    String getRequiredPermission();

    /**
     * Should the Multiverse SafeTeleporter be used?
     * <p>
     * If not, MV will blindly take people to the location specified.
     *
     * @return True if the SafeTeleporter will be used, false if not.
     */
    boolean useSafeTeleporter();
}
