package org.mvplugins.multiverse.core.world.reasons;

import co.aikar.locales.MessageKey;
import co.aikar.locales.MessageKeyProvider;

import org.jetbrains.annotations.ApiStatus;
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
     * The server software does not support create worlds using namespaced key. Only legacy world name is supported.
     * Generally this will only be an issue on Spigot servers.
     *
     * @since 5.7
     */
    @ApiStatus.AvailableSince("5.7")
    NAMESPACEDKEY_UNSUPPORTED(MVCorei18n.WORLDKEYPARSE_NAMESPACEDKEYUNSUPPORTED),

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
    WORLD_CREATOR_FAILED(MVCorei18n.GENERIC_FAILURE),
    ;

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
