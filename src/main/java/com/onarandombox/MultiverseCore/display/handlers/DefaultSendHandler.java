package com.onarandombox.MultiverseCore.display.handlers;

import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

import java.util.List;

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
    public void send(@NotNull CommandSender sender, @NotNull List<String> content) {
        sender.sendMessage(content.toArray(new String[0]));
    }
}
