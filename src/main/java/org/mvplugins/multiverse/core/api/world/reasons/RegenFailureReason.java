package org.mvplugins.multiverse.core.api.world.reasons;

import co.aikar.locales.MessageKey;
import co.aikar.locales.MessageKeyProvider;

import org.mvplugins.multiverse.core.api.locale.MVCorei18n;
import org.mvplugins.multiverse.core.api.result.FailureReason;

/**
 * Result of a world regeneration operation.
 *
 * @since 5.0
 */
public enum RegenFailureReason implements FailureReason {
    /**
     * The world does not exist.
     *
     * @since 5.0
     */
    DELETE_FAILED(MVCorei18n.GENERIC_FAILURE),

    /**
     * The new world could not be created.
     *
     * @since 5.0
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
