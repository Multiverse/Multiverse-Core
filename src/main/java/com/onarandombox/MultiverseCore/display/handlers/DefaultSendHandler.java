package com.onarandombox.MultiverseCore.display.handlers;

import java.util.List;

import co.aikar.commands.BukkitCommandIssuer;
import org.jetbrains.annotations.NotNull;

public class DefaultSendHandler implements SendHandler {

    private static DefaultSendHandler instance;

    public static DefaultSendHandler getInstance() {
        if (instance == null) {
            instance = new DefaultSendHandler();
        }
        return instance;
    }

    private DefaultSendHandler() {
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void send(@NotNull BukkitCommandIssuer issuer, @NotNull List<String> content) {
        content.forEach(issuer::sendMessage);
    }
}
