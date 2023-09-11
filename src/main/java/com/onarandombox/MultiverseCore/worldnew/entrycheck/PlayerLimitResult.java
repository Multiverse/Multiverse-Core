package com.onarandombox.MultiverseCore.worldnew.entrycheck;

import co.aikar.locales.MessageKey;
import co.aikar.locales.MessageKeyProvider;
import com.onarandombox.MultiverseCore.utils.MVCorei18n;
import com.onarandombox.MultiverseCore.utils.result.FailureReason;
import com.onarandombox.MultiverseCore.utils.result.SuccessReason;

/**
 * Result of a world player limit check.
 */
public class PlayerLimitResult {
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
