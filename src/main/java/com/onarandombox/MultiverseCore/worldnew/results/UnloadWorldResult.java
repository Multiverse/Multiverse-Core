package com.onarandombox.MultiverseCore.worldnew.results;

import co.aikar.locales.MessageKey;
import co.aikar.locales.MessageKeyProvider;
import com.onarandombox.MultiverseCore.utils.MVCorei18n;
import com.onarandombox.MultiverseCore.utils.result.FailureReason;
import com.onarandombox.MultiverseCore.utils.result.SuccessReason;

public class UnloadWorldResult {
    public enum Success implements SuccessReason {
        UNLOADED(MVCorei18n.UNLOADWORLD_UNLOADED)
        ;

        private final MessageKeyProvider message;

        Success(MessageKeyProvider message) {
            this.message = message;
        }

        @Override
        public MessageKey getMessageKey() {
            return message.getMessageKey();
        }
    }

    public enum Failure implements FailureReason {
        WORLD_ALREADY_UNLOADING(MVCorei18n.UNLOADWORLD_WORLDALREADYUNLOADING),
        WORLD_NON_EXISTENT(MVCorei18n.UNLOADWORLD_WORLDNONEXISTENT),
        WORLD_UNLOADED(MVCorei18n.UNLOADWORLD_WORLDUNLOADED),
        BUKKIT_UNLOAD_FAILED(MVCorei18n.UNLOADWORLD_BUKKITUNLOADFAILED),
        ;

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
