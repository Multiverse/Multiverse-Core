package com.mvplugin.core;

import com.dumptruckman.minecraft.pluginbase.messaging.BundledMessage;
import com.dumptruckman.minecraft.pluginbase.messaging.ChatColor;

public class MultiverseException extends Exception {

    private final BundledMessage languageMessage;


    public MultiverseException(BundledMessage b) {
        super(String.format(ChatColor.translateAlternateColorCodes('&', b.getMessage().getDefault().get(0)), b.getArgs()));
        this.languageMessage = b;
    }

    public MultiverseException(BundledMessage b, Throwable throwable) {
        super(String.format(ChatColor.translateAlternateColorCodes('&', b.getMessage().getDefault().get(0)), b.getArgs()), throwable);
        this.languageMessage = b;
    }

    public BundledMessage getBundledMessage() {
        return this.languageMessage;
    }
}
