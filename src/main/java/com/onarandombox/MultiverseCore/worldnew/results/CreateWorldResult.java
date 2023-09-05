package com.onarandombox.MultiverseCore.worldnew.results;

import co.aikar.locales.MessageKey;
import co.aikar.locales.MessageKeyProvider;
import com.onarandombox.MultiverseCore.utils.MVCorei18n;
import com.onarandombox.MultiverseCore.utils.result.FailureReason;
import com.onarandombox.MultiverseCore.utils.result.SuccessReason;

public class CreateWorldResult {
    public enum Success implements SuccessReason {
        CREATED(MVCorei18n.CREATEWORLD_CREATED)
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
        INVALID_WORLDNAME(MVCorei18n.CREATEWORLD_INVALIDWORLDNAME),
        WORLD_EXIST_FOLDER(MVCorei18n.CREATEWORLD_WORLDEXISTFOLDER),
        WORLD_EXIST_OFFLINE(MVCorei18n.CREATEWORLD_WORLDEXISTOFFLINE),
        WORLD_EXIST_LOADED(MVCorei18n.CREATEWORLD_WORLDEXISTLOADED),
        BUKKIT_CREATION_FAILED(MVCorei18n.CREATEWORLD_BUKKITCREATIONFAILED),
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
