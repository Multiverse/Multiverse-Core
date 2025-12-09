package org.mvplugins.multiverse.core.command.context.issueraware;

import co.aikar.commands.BukkitCommandExecutionContext;
import co.aikar.commands.BukkitCommandIssuer;
import co.aikar.commands.InvalidCommandArgument;
import co.aikar.commands.contexts.IssuerAwareContextResolver;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.ApiStatus;
import org.mvplugins.multiverse.core.locale.message.Message;

import java.util.Objects;
import java.util.function.BiFunction;
import java.util.function.Function;

/**
 * Reusable logic of issuer only and issuer aware context resolvers
 *
 * @since 5.1
 */
@ApiStatus.AvailableSince("5.1")
public final class IssuerAwareContextBuilder<T> {

    private BiFunction<BukkitCommandExecutionContext, Player, T> fromPlayer;
    private BiFunction<BukkitCommandExecutionContext, String, T> fromInput;
    private Function<BukkitCommandExecutionContext, Message> issuerOnlyFailMessage = context -> Message.of("This command can only be used by a player.");
    private BiFunction<BukkitCommandExecutionContext, Player, Message> issuerAwarePlayerFailMessage = (context, player) -> Message.of("Unable to resolve context for player '" + player.getName() + "'.");
    private BiFunction<BukkitCommandExecutionContext, String, Message> issuerAwareInputFailMessage = (context, input) -> Message.of("Unable to resolve context for input '" + input + "'.");
    private BiFunction<BukkitCommandExecutionContext, String, Message> inputOnlyFailMessage = (context, input) -> Message.of("Unable to resolve context for input '" + input + "'.");

    public IssuerAwareContextBuilder() {
    }

    /**
     * Parse value from player itself.
     *
     * @param fromInput the parsing function
     * @return The same builder for chaining
     *
     * @since 5.1
     */
    @ApiStatus.AvailableSince("5.1")
    public IssuerAwareContextBuilder<T> fromPlayer(BiFunction<BukkitCommandExecutionContext, Player, T> fromInput) {
        this.fromPlayer = fromInput;
        return this;
    }

    /**
     * Parse value from command input string.
     *
     * @param fromInput the parsing function
     * @return The same builder for chaining
     *
     * @since 5.1
     */
    @ApiStatus.AvailableSince("5.1")
    public IssuerAwareContextBuilder<T> fromInput(BiFunction<BukkitCommandExecutionContext, String, T> fromInput) {
        this.fromInput = fromInput;
        return this;
    }

    /**
     * When getting value fails as issuer is not a player.
     *
     * @param issuerOnlyFailMessage The message
     * @return The same builder for chaining
     *
     * @since 5.1
     */
    @ApiStatus.AvailableSince("5.1")
    public IssuerAwareContextBuilder<T> issuerOnlyFailMessage(Function<BukkitCommandExecutionContext, Message> issuerOnlyFailMessage) {
        this.issuerOnlyFailMessage = issuerOnlyFailMessage;
        return this;
    }

    /**
     * When getting value fails as player cannot parse the value.
     *
     * @param issuerAwarePlayerFailMessage The message
     * @return The same builder for chaining
     *
     * @since 5.1
     */
    @ApiStatus.AvailableSince("5.1")
    public IssuerAwareContextBuilder<T> issuerAwarePlayerFailMessage(BiFunction<BukkitCommandExecutionContext, Player, Message> issuerAwarePlayerFailMessage) {
        this.issuerAwarePlayerFailMessage = issuerAwarePlayerFailMessage;
        return this;
    }

    /**
     * When getting value fails as input cannot parse the value.
     *
     * @param issuerAwareInputFailMessage The message
     * @return The same builder for chaining
     *
     * @since 5.1
     */
    @ApiStatus.AvailableSince("5.1")
    public IssuerAwareContextBuilder<T> issuerAwareInputFailMessage(BiFunction<BukkitCommandExecutionContext, String, Message> issuerAwareInputFailMessage) {
        this.issuerAwareInputFailMessage = issuerAwareInputFailMessage;
        return this;
    }

    /**
     * When getting value fails as input cannot parse the value.
     *
     * @param inputOnlyFailMessage The message
     * @return The same builder for chaining
     *
     * @since 5.1
     */
    @ApiStatus.AvailableSince("5.1")
    public IssuerAwareContextBuilder<T> inputOnlyFailMessage(BiFunction<BukkitCommandExecutionContext, String, Message> inputOnlyFailMessage) {
        this.inputOnlyFailMessage = inputOnlyFailMessage;
        return this;
    }

    /**
     * Creates the issuer aware context resolver logic.
     *
     * @return The generated context resolver
     *
     * @since 5.1
     */
    @ApiStatus.AvailableSince("5.1")
    public IssuerAwareContextResolver<T, BukkitCommandExecutionContext> generateContext() {
        validateRequiredVariables();
        return context -> resolveValue(context, GenericIssuerAwareValue::new).value;
    }

    /**
     * Creates the issuer aware context resolver logic, with marking of whether the value was resolved from player or input.
     *
     * @param createValue The function to create the {@link IssuerAwareValue} value
     * @param <I> The type of the value
     * @return The generated context resolver
     *
     * @since 5.1
     */
    @ApiStatus.AvailableSince("5.1")
    public <I extends IssuerAwareValue> IssuerAwareContextResolver<I, BukkitCommandExecutionContext> generateContext(BiFunction<Boolean, T, I> createValue) {
        validateRequiredVariables();
        return context -> resolveValue(context, createValue);
    }

    private void validateRequiredVariables() {
        Objects.requireNonNull(fromPlayer);
        Objects.requireNonNull(fromInput);
        Objects.requireNonNull(issuerOnlyFailMessage);
        Objects.requireNonNull(issuerAwarePlayerFailMessage);
        Objects.requireNonNull(issuerAwareInputFailMessage);
        Objects.requireNonNull(inputOnlyFailMessage);
    }

    private <I extends IssuerAwareValue> I resolveValue(BukkitCommandExecutionContext context, BiFunction<Boolean, T, I> createValue) {
        BukkitCommandIssuer issuer = context.getIssuer();
        String resolve = context.getFlagValue("resolve", "");

        if (resolve.equals("issuerOnly")) {
            if (issuer.isPlayer()) {
                T result = fromPlayer.apply(context, issuer.getPlayer());
                if (result != null) {
                    return createValue.apply(true, result);
                }
            }
            throw new InvalidCommandArgument(issuerOnlyFailMessage.apply(context).formatted(issuer));
        }

        String input = context.getFirstArg();
        T result = fromInput.apply(context, input);
        if (result != null) {
            context.popFirstArg();
            return createValue.apply(false, result);
        }

        int maxArgForAware = context.getFlagValue("maxArgForAware", Integer.MAX_VALUE);
        long argLengthWithoutFlags = context.getArgs().stream()
                .takeWhile(value -> !value.startsWith("-") && !value.isEmpty()) // ignore the flags
                .count();

        if (resolve.equals("issuerAware") && argLengthWithoutFlags <= maxArgForAware) {
            if (issuer.isPlayer()) {
                Player player = issuer.getPlayer();
                result = fromPlayer.apply(context, player);
                if (result != null) {
                    return createValue.apply(true, result);
                }
                throw new InvalidCommandArgument(issuerAwarePlayerFailMessage.apply(context, player).formatted(issuer));
            }
            throw new InvalidCommandArgument(issuerAwareInputFailMessage.apply(context, input).formatted(issuer));
        }

        throw new InvalidCommandArgument(inputOnlyFailMessage.apply(context, input).formatted(issuer));
    }

    private static class GenericIssuerAwareValue<T> extends IssuerAwareValue {
        private final T value;

        public GenericIssuerAwareValue(boolean byIssuer, T value) {
            super(byIssuer);
            this.value = value;
        }
    }
}
