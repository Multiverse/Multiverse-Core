package com.onarandombox.MultiverseCore.commandTools.flag;

import co.aikar.commands.InvalidCommandArgument;
import com.onarandombox.MultiverseCore.MultiverseCore;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;

public abstract class Flag<T> {

    private final String key;
    private final Class<T> type;

    public Flag(@NotNull String key,
                @NotNull Class<T> type) {

        this.key = key;
        this.type = type;
        MVFlags.flagMap.put(this.key, this);
    }

    public abstract @NotNull Collection<String> suggestValue(@NotNull MultiverseCore plugin);

    public T parseValue(@Nullable String value,
                        @NotNull MultiverseCore plugin,
                        @NotNull CommandSender sender) {

        if (!validValue(value)) {
            showErrorMessage(value, sender);
            throw new InvalidCommandArgument();
        }
        return calculateValue(value, plugin, sender);
    }

    public abstract T calculateValue(@NotNull String value,
                                     @NotNull MultiverseCore plugin,
                                     @NotNull CommandSender sender);

    public boolean validValue(@Nullable String value) {
        return value != null;
    }

    public void showErrorMessage(@Nullable String value,
                                 @NotNull CommandSender sender) {
        sender.sendMessage(String.format("%sYou need to specify a value for flag '%s'.", ChatColor.RED, this.getKey()));
    }

    public abstract T getDefault();

    public @NotNull String getKey() {
        return key;
    }

    public @NotNull Class<T> getType() {
        return type;
    }

    @Override
    public String toString() {
        return "Flag{" +
                "key='" + key + '\'' +
                ", type=" + type +
                '}';
    }
}
