package com.onarandombox.MultiverseCore.world.entrycheck;

import co.aikar.locales.MessageKey;
import co.aikar.locales.MessageKeyProvider;
import com.onarandombox.MultiverseCore.utils.MVCorei18n;
import com.onarandombox.MultiverseCore.utils.result.FailureReason;
import com.onarandombox.MultiverseCore.utils.result.SuccessReason;

public class WorldAccessResult {
    public enum Success implements SuccessReason {
        NO_ENFORCE_WORLD_ACCESS,
        HAS_WORLD_ACCESS
    }

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
