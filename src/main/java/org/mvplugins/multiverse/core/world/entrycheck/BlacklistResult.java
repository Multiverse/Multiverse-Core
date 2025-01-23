package org.mvplugins.multiverse.core.world.entrycheck;

import co.aikar.locales.MessageKey;
import co.aikar.locales.MessageKeyProvider;

import org.mvplugins.multiverse.core.locale.MVCorei18n;
import org.mvplugins.multiverse.core.utils.result.FailureReason;
import org.mvplugins.multiverse.core.utils.result.SuccessReason;

/**
 * Result of a world blacklist check.
 */
public final class BlacklistResult {
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
