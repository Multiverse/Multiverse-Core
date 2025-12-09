package org.mvplugins.multiverse.core.locale.message;

import java.util.Objects;

import co.aikar.commands.ACFUtil;
import co.aikar.commands.CommandIssuer;
import co.aikar.commands.Locales;
import co.aikar.locales.MessageKey;
import co.aikar.locales.MessageKeyProvider;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public final class LocalizedMessage extends Message implements MessageKeyProvider {

    private final @NotNull MessageKeyProvider messageKeyProvider;

    LocalizedMessage(
            @NotNull MessageKeyProvider messageKeyProvider,
            @NotNull String message,
            @NotNull MessageReplacement... replacements) {
        super(message, replacements);
        this.messageKeyProvider = messageKeyProvider;
    }

    @Override
    public MessageKey getMessageKey() {
        return messageKeyProvider.getMessageKey();
    }

    @Override
    public @NotNull String formatted(@NotNull Locales locales, @Nullable CommandIssuer commandIssuer) {
        String[] parsedReplacements = getReplacements(locales, commandIssuer);
        if (parsedReplacements.length == 0) {
            return locales.getMessage(commandIssuer, getMessageKey());
        }
        return ACFUtil.replaceStrings(locales.getMessage(commandIssuer, getMessageKey()), parsedReplacements);
    }
}
