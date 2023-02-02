package com.onarandombox.MultiverseCore.locale;

import co.aikar.locales.MessageKey;
import co.aikar.locales.MessageKeyProvider;

public enum MVCorei18n implements MessageKeyProvider {

        CONFIG_SAVE_FAILED,
        DEBUG_INFO_OFF,
        DEBUG_INFO_ON;

        private final MessageKey key = MessageKey.of("mv-core." + this.name().toLowerCase());

        @Override
        public MessageKey getMessageKey() {
            return this.key;
        }
    }
