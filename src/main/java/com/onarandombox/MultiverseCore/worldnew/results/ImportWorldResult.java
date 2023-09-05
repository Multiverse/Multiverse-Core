package com.onarandombox.MultiverseCore.worldnew.results;

import co.aikar.locales.MessageKey;
import co.aikar.locales.MessageKeyProvider;
import com.onarandombox.MultiverseCore.utils.MVCorei18n;
import com.onarandombox.MultiverseCore.utils.result.FailureReason;
import com.onarandombox.MultiverseCore.utils.result.SuccessReason;

public class ImportWorldResult {
    public enum Success implements SuccessReason {
        IMPORTED
    }

    public enum Failure implements FailureReason {
        INVALID_WORLDNAME(MVCorei18n.GENERIC_FAILURE),
        WORLD_FOLDER_INVALID(MVCorei18n.GENERIC_FAILURE),
        WORLD_EXIST_OFFLINE(MVCorei18n.GENERIC_FAILURE),
        WORLD_EXIST_LOADED(MVCorei18n.GENERIC_FAILURE),
        BUKKIT_CREATION_FAILED(MVCorei18n.GENERIC_FAILURE),
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
