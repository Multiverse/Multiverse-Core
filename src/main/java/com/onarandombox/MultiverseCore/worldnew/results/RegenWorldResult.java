package com.onarandombox.MultiverseCore.worldnew.results;

import co.aikar.locales.MessageKey;
import co.aikar.locales.MessageKeyProvider;
import com.onarandombox.MultiverseCore.utils.MVCorei18n;
import com.onarandombox.MultiverseCore.utils.result.FailureReason;
import com.onarandombox.MultiverseCore.utils.result.SuccessReason;

public class RegenWorldResult {
    public enum Success implements SuccessReason {
        REGENERATED(MVCorei18n.GENERIC_SUCCESS)
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
        DELETE_FAILED(null),
        CREATE_FAILED(null),
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
