package com.onarandombox.MultiverseCore.worldnew.results;

import co.aikar.locales.MessageKey;
import co.aikar.locales.MessageKeyProvider;
import com.onarandombox.MultiverseCore.utils.MVCorei18n;
import com.onarandombox.MultiverseCore.utils.result.SuccessReason;

public class DeleteWorldResult {
    public enum Success implements SuccessReason {
        DELETED(MVCorei18n.DELETEWORLD_DELETED)
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

    public enum Failure implements WorldFailureReason {
        WORLD_NON_EXISTENT(MVCorei18n.DELETEWORLD_WORLDNONEXISTENT),
        WORLD_FOLDER_NOT_FOUND(MVCorei18n.DELETEWORLD_WORLDFOLDERNOTFOUND),
        FAILED_TO_DELETE_FOLDER(MVCorei18n.DELETEWORLD_FAILEDTODELETEFOLDER),
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
