package com.onarandombox.MultiverseCore.worldnew.results;

import co.aikar.locales.MessageKey;
import co.aikar.locales.MessageKeyProvider;
import com.onarandombox.MultiverseCore.utils.MVCorei18n;
import com.onarandombox.MultiverseCore.utils.result.FailureReason;
import com.onarandombox.MultiverseCore.utils.result.SuccessReason;

public class CloneWorldResult {
    public enum Success implements SuccessReason {
        CLONED(MVCorei18n.CLONEWORLD_CLONED)
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
        INVALID_WORLDNAME(MVCorei18n.CLONEWORLD_INVALIDWORLDNAME),
        WORLD_EXIST_FOLDER(MVCorei18n.CLONEWORLD_WORLDEXISTFOLDER),
        WORLD_EXIST_UNLOADED(MVCorei18n.CLONEWORLD_WORLDEXISTUNLOADED),
        WORLD_EXIST_LOADED(MVCorei18n.CLONEWORLD_WORLDEXISTLOADED),
        COPY_FAILED(MVCorei18n.CLONEWORLD_COPYFAILED),
        IMPORT_FAILED(null),
        MV_WORLD_FAILED(null), // TODO
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
