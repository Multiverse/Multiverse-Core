package org.mvplugins.multiverse.core.world.entrycheck;

import co.aikar.locales.MessageKey;
import co.aikar.locales.MessageKeyProvider;

import org.mvplugins.multiverse.core.locale.MVCorei18n;
import org.mvplugins.multiverse.core.utils.result.FailureReason;
import org.mvplugins.multiverse.core.utils.result.SuccessReason;

/**
 * Result of a world entry fee check.
 */
public final class EntryFeeResult {
    /**
     * Success reasons for an entry fee check.
     */
    public enum Success implements SuccessReason {
        FREE_ENTRY,
        ENOUGH_MONEY,
        EXEMPT_FROM_ENTRY_FEE,
        CONSOLE_OR_BLOCK_COMMAND_SENDER
    }

    /**
     * Failure reasons for an entry fee check.
     */
    public enum Failure implements FailureReason {
        NOT_ENOUGH_MONEY(MVCorei18n.ENTRYCHECK_NOTENOUGHMONEY),
        CANNOT_PAY_ENTRY_FEE(MVCorei18n.ENTRYCHECK_CANNOTPAYENTRYFEE);

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
