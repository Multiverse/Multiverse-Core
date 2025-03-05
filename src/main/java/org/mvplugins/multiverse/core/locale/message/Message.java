package org.mvplugins.multiverse.core.locale.message;

import java.util.Objects;

import co.aikar.commands.ACFUtil;
import co.aikar.commands.CommandIssuer;
import co.aikar.commands.Locales;
import co.aikar.locales.MessageKeyProvider;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * A message that can be formatted with replacements and localized.
 */
public sealed class Message permits LocalizedMessage {

    /**
     * Creates a basic non-localized Message with the given message and replacements.
     *
     * @param message The message
     * @param replacements The replacements
     * @return A new Message
     */
    @Contract(value = "_, _ -> new", pure = true)
    public static Message of(@NotNull String message, @NotNull MessageReplacement... replacements) {
        Objects.requireNonNull(message, "message must not be null");
        for (MessageReplacement replacement : replacements) {
            Objects.requireNonNull(replacement, "replacements must not contain null");
        }

        return new Message(message, replacements);
    }

    /**
     * Creates a localized Message with the given message key provider and replacements.
     * <br/>
     * This message will extend {@link MessageKeyProvider} and delegate to the given message key provider.
     *
     * @param messageKeyProvider The message key provider
     * @param replacements The replacements
     * @return A new localizable Message
     */
    @Contract(value = "_, _ -> new", pure = true)
    public static Message of(@NotNull MessageKeyProvider messageKeyProvider, @NotNull MessageReplacement... replacements) {
        return of(messageKeyProvider, "{error_key: %s}".formatted(messageKeyProvider.getMessageKey().getKey()), replacements);
    }

    /**
     * Creates a localized Message with the given message key provider, non-localized message and replacements.
     * <br/>
     * The non-localized message is required for conditions where it is not practical to provide a localized message.
     * <br/>
     * This message will extend {@link MessageKeyProvider} and delegate to the given message key provider.
     *
     * @param messageKeyProvider The message key provider
     * @param nonLocalizedMessage The non-localized message
     * @param replacements The replacements
     * @return A new localizable Message
     */
    @Contract(value = "_, _, _ -> new", pure = true)
    public static Message of(
            @NotNull MessageKeyProvider messageKeyProvider,
            @NotNull String nonLocalizedMessage,
            @NotNull MessageReplacement... replacements) {
        Objects.requireNonNull(messageKeyProvider, "messageKeyProvider must not be null");
        Objects.requireNonNull(nonLocalizedMessage, "message must not be null");
        for (MessageReplacement replacement : replacements) {
            Objects.requireNonNull(replacement, "replacements must not contain null");
        }

        return new LocalizedMessage(messageKeyProvider, nonLocalizedMessage, replacements);
    }

    private final @NotNull String message;
    protected final @NotNull MessageReplacement[] replacements;

    protected Message(@NotNull String message, @NotNull MessageReplacement... replacements) {
        this.message = message;
        this.replacements = replacements;
    }

    /**
     * Gets the replacements for this message.
     * <br/>
     * This array is guaranteed to be of even length and suitable for use with
     * {@link ACFUtil#replaceStrings(String, String...)}.
     *
     * @return The replacements
     */
    public @NotNull String[] getReplacements() {
        return toReplacementsArray(replacements);
    }

    /**
     * Gets the replacements for this message with localization support.
     * <br/>
     * This array is guaranteed to be of even length and suitable for use with
     * {@link ACFUtil#replaceStrings(String, String...)}.
     *
     * @param locales       The MultiverseCore locales provider
     * @param commandIssuer The command issuer the message is for, or null for the console (default locale)
     * @return The replacements
     */
    public @NotNull String[] getReplacements(@NotNull Locales locales, @Nullable CommandIssuer commandIssuer) {
        return getReplacements();
    }

    /**
     * Gets the raw, non-localized, non-replaced message.
     *
     * @return The raw message
     */
    public @NotNull String raw() {
        return message;
    }

    /**
     * Gets the formatted message.
     * <br/>
     * This is the raw, non-localized message with replacements applied.
     *
     * @return The formatted message
     */
    public @NotNull String formatted() {
        String[] parsedReplacements = getReplacements();
        if (parsedReplacements.length == 0) {
            return raw();
        }
        return ACFUtil.replaceStrings(message, parsedReplacements);
    }

    /**
     * Gets the formatted message from localization data.
     * <br/>
     * This is the localized message with replacements applied. The message is localized using the default locale.
     *
     * @param locales The MultiverseCore locales provider
     * @return The formatted, localized message
     */
    public @NotNull String formatted(@NotNull Locales locales) {
        return formatted(locales, null);
    }

    /**
     * Gets the formatted message from localization data.
     * <br/>
     * This is the localized message with replacements applied. The message is localized using the locale of the given
     * command issuer, if not null.
     *
     * @param locales The MultiverseCore locales provider
     * @param commandIssuer The command issuer the message is for, or null for the console (default locale)
     * @return The formatted, localized message
     */
    public @NotNull String formatted(@NotNull Locales locales, @Nullable CommandIssuer commandIssuer) {
        return formatted();
    }

    private static String[] toReplacementsArray(@NotNull MessageReplacement... replacements) {
        String[] replacementsArray = new String[replacements.length * 2];
        int i = 0;
        for (MessageReplacement replacement : replacements) {
            replacementsArray[i++] = replacement.getKey();
            replacementsArray[i++] = replacement.getReplacement().fold(s -> s, Message::formatted);
        }
        return replacementsArray;
    }
}
