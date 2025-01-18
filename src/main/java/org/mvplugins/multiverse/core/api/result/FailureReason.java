package org.mvplugins.multiverse.core.api.result;

import co.aikar.locales.MessageKey;
import co.aikar.locales.MessageKeyProvider;

import org.mvplugins.multiverse.core.api.locale.MVCorei18n;

/**
 * Represents a failure reason for an {@link Attempt}.
 *
 * @since 5.0
 */
public interface FailureReason extends MessageKeyProvider {
    default MessageKey getMessageKey() {
        return MVCorei18n.GENERIC_FAILURE.getMessageKey();
    }
}
