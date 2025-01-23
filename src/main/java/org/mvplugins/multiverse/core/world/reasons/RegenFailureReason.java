package org.mvplugins.multiverse.core.world.reasons;

import co.aikar.locales.MessageKey;
import co.aikar.locales.MessageKeyProvider;

import org.mvplugins.multiverse.core.locale.MVCorei18n;
import org.mvplugins.multiverse.core.utils.result.FailureReason;

/**
 * Result of a world regeneration operation.
 */
public enum RegenFailureReason implements FailureReason {
    /**
     * The world does not exist.
     */
    DELETE_FAILED(MVCorei18n.GENERIC_FAILURE),

    /**
     * The new world could not be created.
     */
    CREATE_FAILED(MVCorei18n.GENERIC_FAILURE);

    private final MessageKeyProvider message;

    RegenFailureReason(MessageKeyProvider message) {
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
