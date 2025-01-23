package org.mvplugins.multiverse.core.world.reasons;

import co.aikar.locales.MessageKey;
import co.aikar.locales.MessageKeyProvider;

import org.mvplugins.multiverse.core.locale.MVCorei18n;
import org.mvplugins.multiverse.core.utils.result.FailureReason;

/**
 * Result of a world import operation.
 */
public enum ImportFailureReason implements FailureReason {
    /**
     * The world name is invalid.
     */
    INVALID_WORLDNAME(MVCorei18n.IMPORTWORLD_INVALIDWORLDNAME),

    /**
     * The world folder is invalid.
     */
    WORLD_FOLDER_INVALID(MVCorei18n.IMPORTWORLD_WORLDFOLDERINVALID),

    /**
     * The target world folder already exists. You should load it instead.
     */
    WORLD_EXIST_UNLOADED(MVCorei18n.IMPORTWORLD_WORLDEXISTUNLOADED),

    /**
     * The target world is already exist and loaded.
     */
    WORLD_EXIST_LOADED(MVCorei18n.IMPORTWORLD_WORLDEXISTLOADED),

    /**
     * Bukkit API failed to create the world.
     */
    BUKKIT_CREATION_FAILED(MVCorei18n.IMPORTWORLD_BUKKITCREATIONFAILED);

    private final MessageKeyProvider message;

    ImportFailureReason(MessageKeyProvider message) {
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
