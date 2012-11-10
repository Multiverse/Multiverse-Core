package com.mvplugin.core;

import com.dumptruckman.minecraft.pluginbase.messaging.BundledMessage;

public class MultiverseException extends Exception {

    private final BundledMessage languageMessage;


    public MultiverseException(BundledMessage languageMessage) {
        super(languageMessage.getMessage().getDefault().get(0));
        this.languageMessage = languageMessage;
    }

    public MultiverseException(BundledMessage languageMessage, Throwable throwable) {
        super(languageMessage.getMessage().getDefault().get(0), throwable);
        this.languageMessage = languageMessage;
    }

    public BundledMessage getBundledMessage() {
        return this.languageMessage;
    }
}
