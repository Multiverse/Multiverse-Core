package org.mvplugins.multiverse.core.world.reasons;

import co.aikar.locales.MessageKey;
import co.aikar.locales.MessageKeyProvider;

import org.mvplugins.multiverse.core.locale.MVCorei18n;
import org.mvplugins.multiverse.core.utils.result.FailureReason;

/**
 * Result of a world creation operation.
 */
public enum CreateFailureReason implements FailureReason {
    /**
     * The world name is invalid.
     */
    INVALID_WORLDNAME(MVCorei18n.CREATEWORLD_INVALIDWORLDNAME),

    /**
     * The target new world folder already exists.
     */
    WORLD_EXIST_FOLDER(MVCorei18n.CREATEWORLD_WORLDEXISTFOLDER),

    /**
     * The target new world is already exist but unloaded.
     */
    WORLD_EXIST_UNLOADED(MVCorei18n.CREATEWORLD_WORLDEXISTUNLOADED),

    /**
     * The target new world is already exist and loaded.
     */
    WORLD_EXIST_LOADED(MVCorei18n.CREATEWORLD_WORLDEXISTLOADED),

    /**
     * Bukkit API failed to create the world.
     */
    BUKKIT_CREATION_FAILED(MVCorei18n.CREATEWORLD_BUKKITCREATIONFAILED);

    private final MessageKeyProvider message;

    CreateFailureReason(MessageKeyProvider message) {
        this.message = message;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public MessageKey getMessageKey() {
        return message.getMessageKey();
    }
}
