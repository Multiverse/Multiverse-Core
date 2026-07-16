package org.mvplugins.multiverse.core.locale.message;

import java.util.Objects;

import co.aikar.commands.ACFUtil;
import co.aikar.commands.CommandIssuer;
import co.aikar.commands.Locales;
import co.aikar.locales.MessageKeyProvider;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.mvplugins.multiverse.core.command.MVCommandManager;

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
    public static LocalizedMessage of(@NotNull MessageKeyProvider messageKeyProvider, @NotNull MessageReplacement... replacements) {
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
    public static LocalizedMessage of(
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
     * Gets the replacements for this message. This is the raw, non-localized parsing of replacements.
     * <br/>
     * This array is guaranteed to be of even length and suitable for use with
     * {@link ACFUtil#replaceStrings(String, String...)}.
     *
     * @return The replacements
     *
     * @since 5.8
     */
    @ApiStatus.AvailableSince("5.8")
    public @NotNull String[] getRawReplacements() {
        return toReplacementsArray(replacements);
    }

    /**
     * Gets the replacements for this message with localization support when the plugin has loaded and injected its
     * default {@link MVCommandManager#getLocales()} instance.
     * Otherwise, it will fall back to use {@link getRawReplacements()}.
     *
     * @return The replacements
     */
    public @NotNull String[] getReplacements() {
        return getRawReplacements();
    }

    /**
     * Gets the replacements for this message with localization support based on the command issuer's locale config.
     * <br/>
     * This array is guaranteed to be of even length and suitable for use with
     * {@link ACFUtil#replaceStrings(String, String...)}.
     *
     * @param issuer The command issuer the message is for
     * @return The replacements
     *
     * @since 5.8
     */
    @ApiStatus.AvailableSince("5.8")
    public @NotNull String[] getReplacements(@NotNull CommandIssuer issuer) {
        return getReplacements(issuer.getManager().getLocales(), issuer);
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
        return toReplacementsArray(locales, commandIssuer, replacements);
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
     * Gets the raw formatted message.
     * <br/>
     * This is the raw, non-localized message with replacements applied.
     * This method will never use the locale message key.
     *
     * @return The formatted message
     *
     * @since 5.8
     */
    @ApiStatus.AvailableSince("5.8")
    public @NotNull String rawFormatted() {
        String[] parsedReplacements = getRawReplacements();
        if (parsedReplacements.length == 0) {
            return raw();
        }
        return ACFUtil.replaceStrings(message, parsedReplacements);
    }

    /**
     * Gets the formatted message.
     * <br/>
     * This is the localized message with replacements applied when the plugin has loaded and injected its default
     * {@link MVCommandManager#getLocales()} instance. Otherwise, it will fall back to use {@link rawFormatted()}.
     *
     * @return The formatted message
     */
    public @NotNull String formatted() {
        return rawFormatted();
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
     * command issuer.
     *
     * @param commandIssuer The command issuer the message is for.
     * @return The formatted, localized message
     */
    public @NotNull String formatted(@NotNull CommandIssuer commandIssuer) {
        return formatted(commandIssuer.getManager().getLocales(), commandIssuer);
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
        String[] parsedReplacements = getReplacements(locales, commandIssuer);
        if (parsedReplacements.length == 0) {
            return raw();
        }
        return ACFUtil.replaceStrings(message, parsedReplacements);
    }

    private static String[] toReplacementsArray(@NotNull MessageReplacement... replacements) {
        String[] replacementsArray = new String[replacements.length * 2];
        int i = 0;
        for (MessageReplacement replacement : replacements) {
            replacementsArray[i++] = replacement.getKey();
            replacementsArray[i++] = replacement.getReplacement().fold(s -> s, Message::rawFormatted);
        }
        return replacementsArray;
    }

    private static String[] toReplacementsArray(
            @NotNull Locales locales,
            @Nullable CommandIssuer commandIssuer,
            @NotNull MessageReplacement... replacements) {
        String[] replacementsArray = new String[replacements.length * 2];
        int i = 0;
        for (MessageReplacement replacement : replacements) {
            replacementsArray[i++] = replacement.getKey();
            replacementsArray[i++] = replacement.getReplacement().fold(
                    str -> str,
                    message -> message.formatted(locales, commandIssuer));
        }
        return replacementsArray;
    }
}
