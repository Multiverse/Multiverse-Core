package org.mvplugins.multiverse.core.api.teleportation;

import co.aikar.locales.MessageKey;
import co.aikar.locales.MessageKeyProvider;
import org.mvplugins.multiverse.core.api.event.MVTeleportDestinationEvent;
import org.mvplugins.multiverse.core.api.locale.MVCorei18n;
import org.mvplugins.multiverse.core.api.result.FailureReason;

/**
 * Reasons for a failed teleport.
 *
 * @since 5.0
 */
public enum TeleportFailureReason implements FailureReason {
    /**
     * The destination was null.
     *
     * @since 5.0
     */
    NULL_DESTINATION(MVCorei18n.TELEPORTFAILUREREASON_NULL_DESTINATION),

    /**
     * The location was null.
     *
     * @since 5.0
     */
    NULL_LOCATION(MVCorei18n.TELEPORTFAILUREREASON_NULL_LOCATION),

    /**
     * The location was unsafe.
     *
     * @since 5.0
     */
    UNSAFE_LOCATION(MVCorei18n.TELEPORTFAILUREREASON_UNSAFE_LOCATION),

    /**
     * The server teleport return false.
     *
     * @since 5.0
     */
    TELEPORT_FAILED(MVCorei18n.TELEPORTFAILUREREASON_TELEPORT_FAILED),

    /**
     * An exception was thrown.
     *
     * @since 5.0
     */
    TELEPORT_FAILED_EXCEPTION(MVCorei18n.TELEPORTFAILUREREASON_TELEPORT_FAILED_EXCEPTION),

    /**
     * The {@link MVTeleportDestinationEvent} was cancelled.
     *
     * @since 5.0
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
