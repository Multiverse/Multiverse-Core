package com.onarandombox.MultiverseCore.worldnew.reasons;

import co.aikar.locales.MessageKey;
import co.aikar.locales.MessageKeyProvider;
import com.onarandombox.MultiverseCore.utils.MVCorei18n;
import com.onarandombox.MultiverseCore.utils.result.FailureReason;

/**
 * Result of a world unloading operation.
 */
public enum UnloadFailureReason implements FailureReason {
    /**
     * Unloading operation is underway.
     */
    WORLD_ALREADY_UNLOADING(MVCorei18n.UNLOADWORLD_WORLDALREADYUNLOADING),

    /**
     * The world does not exist.
     */
    WORLD_NON_EXISTENT(MVCorei18n.UNLOADWORLD_WORLDNONEXISTENT),

    /**
     * The world is already unloaded.
     */
    WORLD_UNLOADED(MVCorei18n.UNLOADWORLD_WORLDUNLOADED),

    /**
     * Bukkit API failed to unload the world.
     */
    BUKKIT_UNLOAD_FAILED(MVCorei18n.UNLOADWORLD_BUKKITUNLOADFAILED);

    private final MessageKeyProvider message;

    UnloadFailureReason(MessageKeyProvider message) {
        this.message = message;
    }

    @Override
    public MessageKey getMessageKey() {
        return message.getMessageKey();
    }
}