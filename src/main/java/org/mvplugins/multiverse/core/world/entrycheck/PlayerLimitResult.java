package org.mvplugins.multiverse.core.world.entrycheck;

import co.aikar.locales.MessageKey;
import co.aikar.locales.MessageKeyProvider;

import org.mvplugins.multiverse.core.locale.MVCorei18n;
import org.mvplugins.multiverse.core.utils.result.FailureReason;
import org.mvplugins.multiverse.core.utils.result.SuccessReason;

/**
 * Result of a world player limit check.
 */
public final class PlayerLimitResult {
    /**
     * Success reasons for a player limit check.
     */
    public enum Success implements SuccessReason {
        NO_PLAYERLIMIT,
        WITHIN_PLAYERLIMIT,
        BYPASS_PLAYERLIMIT
    }

    /**
     * Failure reasons for a player limit check.
     */
    public enum Failure implements FailureReason {
        EXCEED_PLAYERLIMIT(MVCorei18n.ENTRYCHECK_EXCEEDPLAYERLIMIT);

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
