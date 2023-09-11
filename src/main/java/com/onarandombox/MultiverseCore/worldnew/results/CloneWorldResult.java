package com.onarandombox.MultiverseCore.worldnew.results;

import co.aikar.locales.MessageKey;
import co.aikar.locales.MessageKeyProvider;
import com.onarandombox.MultiverseCore.utils.MVCorei18n;
import com.onarandombox.MultiverseCore.utils.result.FailureReason;
import com.onarandombox.MultiverseCore.utils.result.SuccessReason;

/**
 * Result of a world clone operation.
 */
public enum CloneWorldResult implements FailureReason {
    INVALID_WORLDNAME(MVCorei18n.CLONEWORLD_INVALIDWORLDNAME),
    WORLD_EXIST_FOLDER(MVCorei18n.CLONEWORLD_WORLDEXISTFOLDER),
    WORLD_EXIST_UNLOADED(MVCorei18n.CLONEWORLD_WORLDEXISTUNLOADED),
    WORLD_EXIST_LOADED(MVCorei18n.CLONEWORLD_WORLDEXISTLOADED),
    COPY_FAILED(MVCorei18n.CLONEWORLD_COPYFAILED),
    IMPORT_FAILED(null),
    MV_WORLD_FAILED(null), // TODO
    ;

    private final MessageKeyProvider message;

    CloneWorldResult(MessageKeyProvider message) {
        this.message = message;
    }

    @Override
    public MessageKey getMessageKey() {
        return message.getMessageKey();
    }
}
