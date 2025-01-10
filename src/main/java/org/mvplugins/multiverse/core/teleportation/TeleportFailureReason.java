package org.mvplugins.multiverse.core.teleportation;

import org.mvplugins.multiverse.core.utils.result.FailureReason;

public enum TeleportFailureReason implements FailureReason {
    NULL_DESTINATION,
    NULL_LOCATION,
    UNSAFE_LOCATION,
    TELEPORT_FAILED,
    TELEPORT_FAILED_EXCEPTION,
    PLAYER_OFFLINE,
    EVENT_CANCELLED,
}
