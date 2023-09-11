package com.onarandombox.MultiverseCore.worldnew.entrycheck;

import co.aikar.locales.MessageKey;
import co.aikar.locales.MessageKeyProvider;
import com.onarandombox.MultiverseCore.utils.MVCorei18n;
import com.onarandombox.MultiverseCore.utils.result.FailureReason;
import com.onarandombox.MultiverseCore.utils.result.SuccessReason;

/**
 * Result of a world access check.
 */
public class WorldAccessResult {
    /**
     * Success reasons for a world access check.
     */
    public enum Success implements SuccessReason {
        NO_ENFORCE_WORLD_ACCESS,
        HAS_WORLD_ACCESS
    }

    /**
     * Failure reasons for a world access check.
     */
    public enum Failure implements FailureReason {
        NO_WORLD_ACCESS(MVCorei18n.ENTRYCHECK_NOWORLDACCESS);

        private final MessageKeyProvider message;

        Failure(MessageKeyProvider message) {
            this.message = message;
        }

        @Override
        public MessageKey getMessageKey() {
            return message.getMessageKey();
        }
    }
}
