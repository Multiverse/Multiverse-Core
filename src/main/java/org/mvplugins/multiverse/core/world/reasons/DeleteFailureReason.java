package org.mvplugins.multiverse.core.world.reasons;

import co.aikar.locales.MessageKey;
import co.aikar.locales.MessageKeyProvider;

import org.mvplugins.multiverse.core.event.MVWorldDeleteEvent;
import org.mvplugins.multiverse.core.locale.MVCorei18n;
import org.mvplugins.multiverse.core.utils.result.FailureReason;

/**
 * Result of a world deletion operation.
 */
public enum DeleteFailureReason implements FailureReason {
    /**
     * The world does not exist.
     */
    WORLD_NON_EXISTENT(MVCorei18n.DELETEWORLD_WORLDNONEXISTENT),

    /**
     * The world could not be loaded.
     */
    LOAD_FAILED(MVCorei18n.DELETEWORLD_LOADFAILED),

    /**
     * The world could not be unloaded.
     */
    WORLD_FOLDER_NOT_FOUND(MVCorei18n.DELETEWORLD_WORLDFOLDERNOTFOUND),

    /**
     * The world could not be removed.
     */
    REMOVE_FAILED(MVCorei18n.GENERIC_FAILURE),

    /**
     * The world folder could not be deleted.
     */
    FAILED_TO_DELETE_FOLDER(MVCorei18n.DELETEWORLD_FAILEDTODELETEFOLDER),

    /**
     * The {@link MVWorldDeleteEvent} was cancelled.
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
