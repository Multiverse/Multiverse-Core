package com.onarandombox.MultiverseCore.display.parsers;

import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * Parse objects into string or list of strings.
 */
@FunctionalInterface
public interface ContentParser {
    /**
     * Parse the object to string(s) and add it to the content.
     *
     * @param sender    The target which the content will be displayed to.
     * @param content   The content to display.
     */
    void parse(@NotNull CommandSender sender, @NotNull List<String> content);
}
