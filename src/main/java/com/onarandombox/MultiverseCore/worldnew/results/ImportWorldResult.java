package com.onarandombox.MultiverseCore.worldnew.results;

import co.aikar.locales.MessageKey;
import co.aikar.locales.MessageKeyProvider;
import com.onarandombox.MultiverseCore.utils.MVCorei18n;
import com.onarandombox.MultiverseCore.utils.result.FailureReason;
import com.onarandombox.MultiverseCore.utils.result.SuccessReason;

public class ImportWorldResult {
    public enum Success implements SuccessReason {
        IMPORTED(MVCorei18n.IMPORTWORLD_IMPORTED);

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
        INVALID_WORLDNAME(MVCorei18n.IMPORTWORLD_INVALIDWORLDNAME),
        WORLD_FOLDER_INVALID(MVCorei18n.IMPORTWORLD_WORLDFOLDERINVALID),
        WORLD_EXIST_OFFLINE(MVCorei18n.IMPORTWORLD_WORLDEXISTOFFLINE),
        WORLD_EXIST_LOADED(MVCorei18n.IMPORTWORLD_WORLDEXISTLOADED),
        BUKKIT_CREATION_FAILED(MVCorei18n.IMPORTWORLD_BUKKITCREATIONFAILED),
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
