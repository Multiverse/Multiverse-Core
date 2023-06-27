package com.onarandombox.MultiverseCore.world.entrycheck;

import co.aikar.locales.MessageKey;
import co.aikar.locales.MessageKeyProvider;
import com.onarandombox.MultiverseCore.utils.MVCorei18n;
import com.onarandombox.MultiverseCore.utils.checkresult.FailureReason;
import com.onarandombox.MultiverseCore.utils.checkresult.SuccessReason;

public class BlacklistResult {
    public enum Success implements SuccessReason {
        UNKNOWN_FROM_WORLD,
        BYPASSED_BLACKLISTED,
        NOT_BLACKLISTED
    }

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
