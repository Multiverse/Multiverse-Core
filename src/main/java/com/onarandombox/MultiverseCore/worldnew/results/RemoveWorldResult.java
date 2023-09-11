package com.onarandombox.MultiverseCore.worldnew.results;

import co.aikar.locales.MessageKey;
import co.aikar.locales.MessageKeyProvider;
import com.onarandombox.MultiverseCore.utils.MVCorei18n;
import com.onarandombox.MultiverseCore.utils.result.FailureReason;

/**
 * Result of a world removal operation.
 */
public enum RemoveWorldResult implements FailureReason {
    WORLD_NON_EXISTENT(MVCorei18n.REMOVEWORLD_WORLDNONEXISTENT),
    UNLOAD_FAILED(null);

    private final MessageKeyProvider message;

    RemoveWorldResult(MessageKeyProvider message) {
        this.message = message;
    }

    @Override
    public MessageKey getMessageKey() {
        return message.getMessageKey();
    }
}
