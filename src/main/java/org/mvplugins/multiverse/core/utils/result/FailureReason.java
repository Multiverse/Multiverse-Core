package org.mvplugins.multiverse.core.utils.result;

import co.aikar.locales.MessageKey;
import co.aikar.locales.MessageKeyProvider;

import org.jetbrains.annotations.ApiStatus;
import org.mvplugins.multiverse.core.locale.MVCorei18n;

/**
 * Represents a failure reason for an {@link Attempt}.
 */
public interface FailureReason extends MessageKeyProvider {
    /**
     * A generic failure reason that can be used when no specific reason is applicable.
     *
     * @since 5.2
     */
    @ApiStatus.AvailableSince("5.2")
    FailureReason GENERIC = new FailureReason() { };

    default MessageKey getMessageKey() {
        return MVCorei18n.GENERIC_FAILURE.getMessageKey();
    }
}
