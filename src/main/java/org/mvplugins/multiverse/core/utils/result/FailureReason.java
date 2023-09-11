package org.mvplugins.multiverse.core.utils.result;

import co.aikar.locales.MessageKey;
import co.aikar.locales.MessageKeyProvider;
import org.mvplugins.multiverse.core.utils.MVCorei18n;

public interface FailureReason extends MessageKeyProvider {
    default MessageKey getMessageKey() {
        return MVCorei18n.GENERIC_FAILURE.getMessageKey();
    }
}
