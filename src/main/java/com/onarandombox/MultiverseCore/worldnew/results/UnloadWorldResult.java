package com.onarandombox.MultiverseCore.worldnew.results;

import co.aikar.locales.MessageKey;
import co.aikar.locales.MessageKeyProvider;
import com.onarandombox.MultiverseCore.utils.MVCorei18n;
import com.onarandombox.MultiverseCore.utils.result.FailureReason;
import com.onarandombox.MultiverseCore.utils.result.SuccessReason;

public class UnloadWorldResult {
    public enum Success implements SuccessReason {
        UNLOADED
    }

    public static class Failure implements FailureReason {
        public static final Failure WORLD_NON_EXISTENT = new Failure(MVCorei18n.GENERIC_FAILURE);
        public static final Failure WORLD_OFFLINE = new Failure(MVCorei18n.GENERIC_FAILURE);
        public static final Failure BUKKIT_UNLOAD_FAILED = new Failure(MVCorei18n.GENERIC_FAILURE);

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
