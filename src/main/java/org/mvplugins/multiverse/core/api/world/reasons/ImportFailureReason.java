package org.mvplugins.multiverse.core.api.world.reasons;

import co.aikar.locales.MessageKey;
import co.aikar.locales.MessageKeyProvider;

import org.mvplugins.multiverse.core.api.locale.MVCorei18n;
import org.mvplugins.multiverse.core.api.result.FailureReason;

/**
 * Result of a world import operation.
 *
 * @since 5.0
 */
public enum ImportFailureReason implements FailureReason {
    /**
     * The world name is invalid.
     *
     * @since 5.0
     */
    INVALID_WORLDNAME(MVCorei18n.IMPORTWORLD_INVALIDWORLDNAME),

    /**
     * The world folder is invalid.
     *
     * @since 5.0
     */
    WORLD_FOLDER_INVALID(MVCorei18n.IMPORTWORLD_WORLDFOLDERINVALID),

    /**
     * The target world folder already exists. You should load it instead.
     *
     * @since 5.0
     */
    WORLD_EXIST_UNLOADED(MVCorei18n.IMPORTWORLD_WORLDEXISTUNLOADED),

    /**
     * The target world is already exist and loaded.
     *
     * @since 5.0
     */
    WORLD_EXIST_LOADED(MVCorei18n.IMPORTWORLD_WORLDEXISTLOADED),

    /**
     * Bukkit API failed to create the world.
     *
     * @since 5.0
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
