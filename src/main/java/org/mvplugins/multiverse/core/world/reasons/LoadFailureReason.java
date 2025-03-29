package org.mvplugins.multiverse.core.world.reasons;

import co.aikar.locales.MessageKey;
import co.aikar.locales.MessageKeyProvider;

import org.mvplugins.multiverse.core.locale.MVCorei18n;
import org.mvplugins.multiverse.core.utils.result.FailureReason;

/**
 * Result of a world loading operation.
 */
public enum LoadFailureReason implements FailureReason {
    /**
     * Loading operation is underway.
     */
    WORLD_ALREADY_LOADING(MVCorei18n.LOADWORLD_WORLDALREADYLOADING),

    /**
     * The world does not exist.
     */
    WORLD_NON_EXISTENT(MVCorei18n.LOADWORLD_WORLDNONEXISTENT),

    /**
     * The world folder exists but is not known to Multiverse.
     */
    WORLD_EXIST_FOLDER(MVCorei18n.LOADWORLD_WORLDEXISTFOLDER),

    /**
     * The world is already loaded.
     */
    WORLD_EXIST_LOADED(MVCorei18n.LOADWORLD_WORLDEXISTLOADED),

    /**
     * Bukkit API failed to create the world.
     */
    BUKKIT_CREATION_FAILED(MVCorei18n.LOADWORLD_BUKKITCREATIONFAILED);

    private final MessageKeyProvider message;

    LoadFailureReason(MessageKeyProvider message) {
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
