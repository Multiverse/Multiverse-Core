package com.onarandombox.multiverse.core;

import com.dumptruckman.minecraft.pluginbase.locale.BundledMessage;

public class WorldCreationException extends MultiverseException {

    public WorldCreationException(BundledMessage languageMessage) {
        super(languageMessage);
    }

    public WorldCreationException(BundledMessage languageMessage, Throwable throwable) {
        super(languageMessage, throwable);
    }
}
