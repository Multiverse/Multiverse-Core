package com.onarandombox.MultiverseCore.messaging;

import co.aikar.locales.MessageKey;
import co.aikar.locales.MessageKeyProvider;

import java.util.Locale;

public enum CoreMessageKeys implements MessageKeyProvider {

    CONFIG_SAVE_FAILED,
    DEBUG_INFO_OFF,
    DEBUG_INFO_ON;

    private final MessageKey key = MessageKey.of("mv-core." + this.name().toLowerCase(Locale.ENGLISH));

    @Override
    public MessageKey getMessageKey() {
        return this.key;
    }
}
