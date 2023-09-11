package com.onarandombox.MultiverseCore.worldnew.results;

import co.aikar.locales.MessageKey;
import co.aikar.locales.MessageKeyProvider;
import com.onarandombox.MultiverseCore.utils.result.FailureReason;

/**
 * Result of a world regeneration operation.
 */
public enum RegenWorldResult implements FailureReason {
    DELETE_FAILED(null),
    CREATE_FAILED(null);

    private final MessageKeyProvider message;

    RegenWorldResult(MessageKeyProvider message) {
        this.message = message;
    }

    @Override
    public MessageKey getMessageKey() {
        return message.getMessageKey();
    }
}
