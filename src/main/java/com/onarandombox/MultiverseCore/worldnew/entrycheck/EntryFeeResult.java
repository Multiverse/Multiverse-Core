package com.onarandombox.MultiverseCore.worldnew.entrycheck;


import co.aikar.locales.MessageKey;
import co.aikar.locales.MessageKeyProvider;
import com.onarandombox.MultiverseCore.utils.MVCorei18n;
import com.onarandombox.MultiverseCore.utils.result.FailureReason;
import com.onarandombox.MultiverseCore.utils.result.SuccessReason;

public class EntryFeeResult {
    public enum Success implements SuccessReason {
        FREE_ENTRY,
        ENOUGH_MONEY,
        EXEMPT_FROM_ENTRY_FEE,
        CONSOLE_OR_BLOCK_COMMAND_SENDER
    }

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