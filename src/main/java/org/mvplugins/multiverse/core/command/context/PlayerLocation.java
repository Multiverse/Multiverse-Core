package org.mvplugins.multiverse.core.command.context;

import org.bukkit.Location;

/**
 * Wrapper for context that get location from player if executed by player,
 * else requires user input of location coordinates.
 *
 * @param value The location
 */
public record PlayerLocation(Location value) {
}
