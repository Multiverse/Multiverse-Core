package org.mvplugins.multiverse.core.world.reasons;

import co.aikar.locales.MessageKey;
import co.aikar.locales.MessageKeyProvider;

import org.jetbrains.annotations.ApiStatus;
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
     * The server software does not support create worlds using namespaced key. Only legacy world name is supported.
     * Generally this will only be an issue on Spigot servers.
     *
     * @since 5.7
     */
    @ApiStatus.AvailableSince("5.7")
    NAMESPACEDKEY_UNSUPPORTED(MVCorei18n.WORLDKEYPARSE_NAMESPACEDKEYUNSUPPORTED),

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
     * The import environment input does not match the loaded Bukkit world's environment.
     *
     * @since 5.2
     */
    @ApiStatus.AvailableSince("5.2")
    BUKKIT_ENVIRONMENT_MISMATCH(MVCorei18n.IMPORTWORLD_BUKKITENVIRONMENTMISMATCH),

    /**
     * Bukkit API failed to create the world.
     */
    WORLD_CREATOR_FAILED(MVCorei18n.GENERIC_FAILURE);

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
