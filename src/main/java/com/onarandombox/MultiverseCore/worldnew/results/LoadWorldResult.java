package com.onarandombox.MultiverseCore.worldnew.results;

import co.aikar.locales.MessageKey;
import co.aikar.locales.MessageKeyProvider;
import com.onarandombox.MultiverseCore.utils.MVCorei18n;
import com.onarandombox.MultiverseCore.utils.result.FailureReason;
import com.onarandombox.MultiverseCore.utils.result.SuccessReason;

/**
 * Result of a world loading operation.
 */
public class LoadWorldResult {
    public enum Success implements SuccessReason {
        LOADED(MVCorei18n.LOADWORLD_LOADED);

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
        WORLD_ALREADY_LOADING(MVCorei18n.LOADWORLD_WORLDALREADYLOADING),
        WORLD_NON_EXISTENT(MVCorei18n.LOADWORLD_WORLDNONEXISTENT),
        WORLD_EXIST_FOLDER(MVCorei18n.LOADWORLD_WORLDEXISTFOLDER),
        WORLD_EXIST_LOADED(MVCorei18n.LOADWORLD_WORLDEXISTLOADED),
        BUKKIT_CREATION_FAILED(MVCorei18n.LOADWORLD_BUKKITCREATIONFAILED);

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
