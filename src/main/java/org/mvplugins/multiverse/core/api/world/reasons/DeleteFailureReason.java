package org.mvplugins.multiverse.core.api.world.reasons;

import co.aikar.locales.MessageKey;
import co.aikar.locales.MessageKeyProvider;

import org.mvplugins.multiverse.core.api.event.MVWorldDeleteEvent;
import org.mvplugins.multiverse.core.api.locale.MVCorei18n;
import org.mvplugins.multiverse.core.api.result.FailureReason;

/**
 * Result of a world deletion operation.
 *
 * @since 5.0
 */
public enum DeleteFailureReason implements FailureReason {
    /**
     * The world does not exist.
     *
     * @since 5.0
     */
    WORLD_NON_EXISTENT(MVCorei18n.DELETEWORLD_WORLDNONEXISTENT),

    /**
     * The world could not be loaded.
     *
     * @since 5.0
     */
    LOAD_FAILED(MVCorei18n.DELETEWORLD_LOADFAILED),

    /**
     * The world could not be unloaded.
     *
     * @since 5.0
     */
    WORLD_FOLDER_NOT_FOUND(MVCorei18n.DELETEWORLD_WORLDFOLDERNOTFOUND),

    /**
     * The world could not be removed.
     *
     * @since 5.0
     */
    REMOVE_FAILED(MVCorei18n.GENERIC_FAILURE),

    /**
     * The world folder could not be deleted.
     *
     * @since 5.0
     */
    FAILED_TO_DELETE_FOLDER(MVCorei18n.DELETEWORLD_FAILEDTODELETEFOLDER),

    /**
     * The {@link MVWorldDeleteEvent} was cancelled.
     *
     * @since 5.0
     */
    EVENT_CANCELLED(MVCorei18n.GENERIC_FAILURE); // todo: messaging

    private final MessageKeyProvider message;

    DeleteFailureReason(MessageKeyProvider message) {
        this.message = message;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public MessageKey getMessageKey() {
        return message.getMessageKey();
    }
}
