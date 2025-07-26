package org.mvplugins.multiverse.core.utils.text;

import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

interface TextFormatter {
    void sendFormattedMessage(@NotNull CommandSender sender, @Nullable String message);

    @Nullable String removeColor(@Nullable String message);

    @Nullable String removeAmpColor(@Nullable String message);

    @Nullable String removeSectionColor(@Nullable String message);

    @Nullable String colorize(@Nullable String message);
}
