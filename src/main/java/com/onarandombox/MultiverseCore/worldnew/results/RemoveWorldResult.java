package com.onarandombox.MultiverseCore.worldnew.results;

import co.aikar.locales.MessageKey;
import co.aikar.locales.MessageKeyProvider;
import com.onarandombox.MultiverseCore.utils.MVCorei18n;
import com.onarandombox.MultiverseCore.utils.result.FailureReason;
import com.onarandombox.MultiverseCore.utils.result.SuccessReason;

public class RemoveWorldResult {
    public enum Success implements SuccessReason {
        REMOVED
    }

    public static class Failure extends UnloadWorldResult.Failure {
        // TODO
        Failure(MessageKeyProvider message) {
            super(message);
        }
    }
}
