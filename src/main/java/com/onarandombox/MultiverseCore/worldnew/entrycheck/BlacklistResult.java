package com.onarandombox.MultiverseCore.worldnew.entrycheck;

import co.aikar.locales.MessageKey;
import co.aikar.locales.MessageKeyProvider;
import com.onarandombox.MultiverseCore.utils.MVCorei18n;
import com.onarandombox.MultiverseCore.utils.result.FailureReason;
import com.onarandombox.MultiverseCore.utils.result.SuccessReason;

/**
 * Result of a world blacklist check.
 */
public class BlacklistResult {
    /**
     * Success reasons for a blacklist check.
     */
    public enum Success implements SuccessReason {
        UNKNOWN_FROM_WORLD,
        BYPASSED_BLACKLISTED,
        NOT_BLACKLISTED
    }

    /**
     * Failure reasons for a blacklist check.
     */
    public enum Failure implements FailureReason {
        BLACKLISTED(MVCorei18n.ENTRYCHECK_BLACKLISTED);

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
