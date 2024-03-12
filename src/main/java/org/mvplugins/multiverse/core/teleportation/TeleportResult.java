package org.mvplugins.multiverse.core.teleportation;

import org.mvplugins.multiverse.core.utils.result.FailureReason;
import org.mvplugins.multiverse.core.utils.result.SuccessReason;

public class TeleportResult {
    public enum Success implements SuccessReason {
        SUCCESS
    }

    public enum Failure implements FailureReason {
        NULL_DESTINATION,
        NULL_LOCATION,
        UNSAFE_LOCATION,
        TELEPORT_FAILED,
        TELEPORT_FAILED_EXCEPTION,
        PLAYER_OFFLINE,
    }
}
