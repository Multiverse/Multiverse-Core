package org.mvplugins.multiverse.core.api.world.reasons;

import co.aikar.locales.MessageKey;
import co.aikar.locales.MessageKeyProvider;

import org.mvplugins.multiverse.core.api.locale.MVCorei18n;
import org.mvplugins.multiverse.core.api.result.FailureReason;

/**
 * Result of a world clone operation.
 *
 * @since 5.0
 */
public enum CloneFailureReason implements FailureReason {
    /**
     * The world name is invalid.
     *
     * @since 5.0
     */
    INVALID_WORLDNAME(MVCorei18n.CLONEWORLD_INVALIDWORLDNAME),

    /**
     * The target new world folder already exists.
     *
     * @since 5.0
     */
    WORLD_EXIST_FOLDER(MVCorei18n.CLONEWORLD_WORLDEXISTFOLDER),

    /**
     * The target new world is already exist but unloaded.
     *
     * @since 5.0
     */
    WORLD_EXIST_UNLOADED(MVCorei18n.CLONEWORLD_WORLDEXISTUNLOADED),

    /**
     * The target new world is already loaded.
     *
     * @since 5.0
     */
    WORLD_EXIST_LOADED(MVCorei18n.CLONEWORLD_WORLDEXISTLOADED),

    /**
     * Failed to copy the world folder contents.
     *
     * @since 5.0
     */
    COPY_FAILED(MVCorei18n.CLONEWORLD_COPYFAILED),

    /**
     * Failed to import the new world.
     *
     * @since 5.0
     */
    IMPORT_FAILED(MVCorei18n.GENERIC_FAILURE);

    private final MessageKeyProvider message;

    CloneFailureReason(MessageKeyProvider message) {
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
