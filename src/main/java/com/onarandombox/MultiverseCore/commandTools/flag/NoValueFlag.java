package com.onarandombox.MultiverseCore.commandTools.flag;

import com.onarandombox.MultiverseCore.MultiverseCore;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.Collections;

public abstract class NoValueFlag<T> extends Flag<T> {

    public NoValueFlag(@NotNull String key, @NotNull Class<T> type) {
        super(key, type);
    }

    @Override
    public @NotNull Collection<String> suggestValue(@NotNull MultiverseCore plugin) {
        return Collections.emptyList();
    }

    public abstract T calculateValue(@Nullable String empty,
                                     @NotNull MultiverseCore plugin,
                                     @NotNull CommandSender sender);

    @Override
    public boolean validValue(@Nullable String value) {
        return value == null;
    }

    @Override
    public void showErrorMessage(@Nullable String value,
                                 @NotNull CommandSender sender) {

        sender.sendMessage(String.format("%sFlag '%s' does not take in any value.", ChatColor.RED, this.getKey()));
    }
}
