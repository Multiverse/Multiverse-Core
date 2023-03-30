package com.onarandombox.MultiverseCore.utils.message;

import co.aikar.commands.ACFUtil;
import co.aikar.commands.CommandIssuer;
import co.aikar.locales.MessageKey;
import co.aikar.locales.MessageKeyProvider;
import com.onarandombox.MultiverseCore.commandtools.PluginLocales;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

final class LocalizedMessage extends Message implements MessageKeyProvider {

    private final @NotNull MessageKeyProvider messageKeyProvider;

    LocalizedMessage(
            @NotNull MessageKeyProvider messageKeyProvider,
            @NotNull String message,
            @NotNull MessageReplacement... replacements
    ) {
        super(message, replacements);
        this.messageKeyProvider = messageKeyProvider;
    }

    @Override
    public MessageKey getMessageKey() {
        return messageKeyProvider.getMessageKey();
    }

    @Override
    public @NotNull String formatted(@NotNull PluginLocales locales, @Nullable CommandIssuer commandIssuer) {
        Objects.requireNonNull(locales, "locales must not be null");

        if (getReplacements().length == 0) {
            return raw();
        }
        return ACFUtil.replaceStrings(locales.getMessage(commandIssuer, getMessageKey()), getReplacements());
    }
}
