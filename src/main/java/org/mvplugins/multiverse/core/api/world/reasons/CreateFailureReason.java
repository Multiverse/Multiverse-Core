package org.mvplugins.multiverse.core.api.world.reasons;

import co.aikar.locales.MessageKey;
import co.aikar.locales.MessageKeyProvider;

import org.mvplugins.multiverse.core.api.locale.MVCorei18n;
import org.mvplugins.multiverse.core.api.result.FailureReason;

/**
 * Result of a world creation operation.
 *
 * @since 5.0
 */
public enum CreateFailureReason implements FailureReason {
    /**
     * The world name is invalid.
     *
     * @since 5.0
     */
    INVALID_WORLDNAME(MVCorei18n.CREATEWORLD_INVALIDWORLDNAME),

    /**
     * The target new world folder already exists.
     *
     * @since 5.0
     */
    WORLD_EXIST_FOLDER(MVCorei18n.CREATEWORLD_WORLDEXISTFOLDER),

    /**
     * The target new world is already exist but unloaded.
     *
     * @since 5.0
     */
    WORLD_EXIST_UNLOADED(MVCorei18n.CREATEWORLD_WORLDEXISTUNLOADED),

    /**
     * The target new world is already exist and loaded.
     *
     * @since 5.0
     */
    WORLD_EXIST_LOADED(MVCorei18n.CREATEWORLD_WORLDEXISTLOADED),

    /**
     * Bukkit API failed to create the world.
     *
     * @since 5.0
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
