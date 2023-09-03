package com.onarandombox.MultiverseCore.worldnew.results;

import co.aikar.locales.MessageKeyProvider;
import com.onarandombox.MultiverseCore.utils.MVCorei18n;
import com.onarandombox.MultiverseCore.utils.result.SuccessReason;

public class DeleteWorldResult {
    public enum Success implements SuccessReason {
        DELETED
    }

    public static class Failure extends RemoveWorldResult.Failure {
        public static final Failure WORLD_FOLDER_NOT_FOUND = new Failure(MVCorei18n.GENERIC_FAILURE);
        public static final Failure FAILED_TO_DELETE_FOLDER = new Failure(MVCorei18n.GENERIC_FAILURE);

        Failure(MessageKeyProvider message) {
            super(message);
        }
    }
}
