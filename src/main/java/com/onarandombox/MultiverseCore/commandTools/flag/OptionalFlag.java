package com.onarandombox.MultiverseCore.commandTools.flag;

import com.onarandombox.MultiverseCore.MultiverseCore;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public abstract class OptionalFlag<T> extends Flag<T> {

    public OptionalFlag(@NotNull String key, @NotNull Class<T> type) {
        super(key, type);
    }

    public abstract T calculateValue(@Nullable String value,
                                     @NotNull MultiverseCore plugin,
                                     @NotNull CommandSender sender);

    @Override
    public boolean validValue(@Nullable String value) {
        return false;
    }
}
