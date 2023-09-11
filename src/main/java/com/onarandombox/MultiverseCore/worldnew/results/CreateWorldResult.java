package com.onarandombox.MultiverseCore.worldnew.results;

import co.aikar.locales.MessageKey;
import co.aikar.locales.MessageKeyProvider;
import com.onarandombox.MultiverseCore.utils.MVCorei18n;
import com.onarandombox.MultiverseCore.utils.result.FailureReason;
import com.onarandombox.MultiverseCore.utils.result.SuccessReason;

/**
 * Result of a world creation operation.
 */
public enum CreateWorldResult implements FailureReason {
    INVALID_WORLDNAME(MVCorei18n.CREATEWORLD_INVALIDWORLDNAME),
    WORLD_EXIST_FOLDER(MVCorei18n.CREATEWORLD_WORLDEXISTFOLDER),
    WORLD_EXIST_UNLOADED(MVCorei18n.CREATEWORLD_WORLDEXISTUNLOADED),
    WORLD_EXIST_LOADED(MVCorei18n.CREATEWORLD_WORLDEXISTLOADED),
    BUKKIT_CREATION_FAILED(MVCorei18n.CREATEWORLD_BUKKITCREATIONFAILED);

    private final MessageKeyProvider message;

    CreateWorldResult(MessageKeyProvider message) {
        this.message = message;
    }

    @Override
    public MessageKey getMessageKey() {
        return message.getMessageKey();
    }
}
