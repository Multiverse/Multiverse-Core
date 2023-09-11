package com.onarandombox.MultiverseCore.worldnew.reasons;

import co.aikar.locales.MessageKey;
import co.aikar.locales.MessageKeyProvider;
import com.onarandombox.MultiverseCore.utils.MVCorei18n;
import com.onarandombox.MultiverseCore.utils.result.FailureReason;

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

    @Override
    public MessageKey getMessageKey() {
        return message.getMessageKey();
    }
}
