package com.onarandombox.MultiverseCore.utils.checkresult;

import co.aikar.locales.MessageKey;
import co.aikar.locales.MessageKeyProvider;
import com.onarandombox.MultiverseCore.utils.MVCorei18n;

public interface FailureReason extends MessageKeyProvider {
    default MessageKey getMessageKey() {
        return MVCorei18n.GENERIC_FAILURE.getMessageKey();
    }
}
