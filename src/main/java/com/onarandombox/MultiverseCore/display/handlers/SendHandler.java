package com.onarandombox.MultiverseCore.display.handlers;

import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * Handles the sending of all content to the command sender.
 */
@FunctionalInterface
public interface SendHandler {
    /**
     * Sends all the content to the given command sender.
     *
     * @param sender    The target which the content will be displayed to.
     * @param content   The content to display.
     */
    void send(@NotNull CommandSender sender, @NotNull List<String> content);
}
