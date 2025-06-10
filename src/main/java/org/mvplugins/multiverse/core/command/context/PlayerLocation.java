package org.mvplugins.multiverse.core.command.context;

import org.bukkit.Location;
import org.jetbrains.annotations.ApiStatus;

/**
 * Wrapper for context that get location from player if executed by player,
 * else requires user input of location coordinates.
 *
 * @param value The location
 *
 * @since 5.1
 */
@ApiStatus.AvailableSince("5.1")
public record PlayerLocation(Location value) {
}
