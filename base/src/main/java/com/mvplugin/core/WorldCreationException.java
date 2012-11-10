package com.mvplugin.core;

import com.dumptruckman.minecraft.pluginbase.messaging.BundledMessage;

public class WorldCreationException extends MultiverseException {

    public WorldCreationException(BundledMessage languageMessage) {
        super(languageMessage);
    }

    public WorldCreationException(BundledMessage languageMessage, Throwable throwable) {
        super(languageMessage, throwable);
    }
}
