package org.mvplugins.multiverse.core.teleportation;

import co.aikar.locales.MessageKey;
import co.aikar.locales.MessageKeyProvider;
import org.mvplugins.multiverse.core.event.MVTeleportDestinationEvent;
import org.mvplugins.multiverse.core.locale.MVCorei18n;
import org.mvplugins.multiverse.core.utils.result.FailureReason;

/**
 * Reasons for a failed teleport.
 */
public enum TeleportFailureReason implements FailureReason {
    /**
     * The destination was null.
     */
    NULL_DESTINATION(MVCorei18n.TELEPORTFAILUREREASON_NULL_DESTINATION),

    /**
     * The location was null.
     */
    NULL_LOCATION(MVCorei18n.TELEPORTFAILUREREASON_NULL_LOCATION),

    /**
     * The location was unsafe.
     */
    UNSAFE_LOCATION(MVCorei18n.TELEPORTFAILUREREASON_UNSAFE_LOCATION),

    /**
     * The server teleport return false.
     */
    TELEPORT_FAILED(MVCorei18n.TELEPORTFAILUREREASON_TELEPORT_FAILED),

    /**
     * An exception was thrown.
     */
    TELEPORT_FAILED_EXCEPTION(MVCorei18n.TELEPORTFAILUREREASON_TELEPORT_FAILED_EXCEPTION),

    /**
     * The {@link MVTeleportDestinationEvent} was cancelled.
     */
    EVENT_CANCELLED(MVCorei18n.TELEPORTFAILUREREASON_EVENT_CANCELLED),
    ;

    private final MessageKeyProvider messageKey;

    TeleportFailureReason(MessageKeyProvider message) {
        this.messageKey = message;
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public MessageKey getMessageKey() {
        return messageKey.getMessageKey();
    }
}
