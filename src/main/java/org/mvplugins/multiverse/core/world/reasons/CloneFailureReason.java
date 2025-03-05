package org.mvplugins.multiverse.core.world.reasons;

import co.aikar.locales.MessageKey;
import co.aikar.locales.MessageKeyProvider;

import org.mvplugins.multiverse.core.locale.MVCorei18n;
import org.mvplugins.multiverse.core.utils.result.FailureReason;

/**
 * Result of a world clone operation.
 */
public enum CloneFailureReason implements FailureReason {
    /**
     * The world name is invalid.
     */
    INVALID_WORLDNAME(MVCorei18n.CLONEWORLD_INVALIDWORLDNAME),

    /**
     * The target new world folder already exists.
     */
    WORLD_EXIST_FOLDER(MVCorei18n.CLONEWORLD_WORLDEXISTFOLDER),

    /**
     * The target new world is already exist but unloaded.
     */
    WORLD_EXIST_UNLOADED(MVCorei18n.CLONEWORLD_WORLDEXISTUNLOADED),

    /**
     * The target new world is already loaded.
     */
    WORLD_EXIST_LOADED(MVCorei18n.CLONEWORLD_WORLDEXISTLOADED),

    /**
     * Failed to copy the world folder contents.
     */
    COPY_FAILED(MVCorei18n.CLONEWORLD_COPYFAILED),

    /**
     * Failed to import the new world.
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
