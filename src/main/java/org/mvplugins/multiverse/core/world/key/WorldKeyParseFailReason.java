package org.mvplugins.multiverse.core.world.key;

import co.aikar.locales.MessageKey;
import co.aikar.locales.MessageKeyProvider;
import org.jetbrains.annotations.ApiStatus;
import org.mvplugins.multiverse.core.locale.MVCorei18n;
import org.mvplugins.multiverse.core.utils.result.FailureReason;

/**
 * Reasons why parsing a world key or name failed.
 * <br />
 * These values are returned by parsing utilities such as {@link org.mvplugins.multiverse.core.world.key.WorldKeyOrName#parse}
 * to indicate the specific cause of failure so callers can present an appropriate localized message.
 *
 * @since 5.7
 */
@ApiStatus.AvailableSince("5.7")
public enum WorldKeyParseFailReason implements FailureReason {
    /**
     * The provided input was null or empty.
     *
     * @since 5.7
     */
    @ApiStatus.AvailableSince("5.7")
    EMPTY(MVCorei18n.WORLDKEYPARSE_EMPTY),

    /**
     * The provided world name contains invalid characters or format.
     *
     * @since 5.7
     */
    @ApiStatus.AvailableSince("5.7")
    INVALID_WORLD_NAME(MVCorei18n.WORLDKEYPARSE_INVALIDWORLDNAME),

    /**
     * The provided string was intended to be a namespaced key but did not parse as a valid
     * {@link org.bukkit.NamespacedKey} (malformed namespace/key or missing parts).
     *
     * @since 5.7
     */
    @ApiStatus.AvailableSince("5.7")
    INVALID_NAMESPACED_KEY(MVCorei18n.WORLDKEYPARSE_INVALIDNAMESPACEDKEY),

    /**
     * The platform/server does not support namespaced keys for worlds. Only PaperMC can create worlds with
     * namespaced keys. Spigot does not support it.
     *
     * @since 5.7
     */
    @ApiStatus.AvailableSince("5.7")
    NAMESPACED_KEY_UNSUPPORTED(MVCorei18n.WORLDKEYPARSE_NAMESPACEDKEYUNSUPPORTED),
    ;

    private final MessageKeyProvider message;

    WorldKeyParseFailReason(MessageKeyProvider message) {
        this.message = message;
    }

    /**
     * Return the localized message key associated with this failure reason.
     * <p>
     * Implementations of {@link FailureReason} can use this to provide a user-facing localized
     * message for the failure.
     *
     * @return The {@link MessageKey} that corresponds to this failure reason
     * @since 5.7
     */
    @Override
    public MessageKey getMessageKey() {
        return message.getMessageKey();
    }
}
