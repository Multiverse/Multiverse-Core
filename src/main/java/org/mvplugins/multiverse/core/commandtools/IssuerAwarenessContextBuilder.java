package org.mvplugins.multiverse.core.commandtools;

import co.aikar.commands.BukkitCommandExecutionContext;
import co.aikar.commands.BukkitCommandIssuer;
import co.aikar.commands.InvalidCommandArgument;
import co.aikar.commands.contexts.IssuerAwareContextResolver;
import com.google.common.base.Function;
import com.google.common.base.Predicate;

import java.util.Objects;
import java.util.function.Supplier;

public class IssuerAwarenessContextBuilder<T> {

    public static <T> IssuerAwarenessContextBuilder<T> create() {
        return new IssuerAwarenessContextBuilder<>();
    }

    private Predicate<BukkitCommandIssuer> canGetFromIssuer;
    private Function<BukkitCommandIssuer, T> getFromIssuer;
    private Function<String, T> getFromContext;
    private Supplier<InvalidCommandArgument> issuerOnlyError;
    private Function<String, InvalidCommandArgument> issuerAwareError;
    private Function<String, InvalidCommandArgument> inputOnlyError;

    private IssuerAwarenessContextBuilder() {
        // Dont expose constructor
    }

    public IssuerAwarenessContextBuilder<T> canGetFromIssuer(Predicate<BukkitCommandIssuer> canGetFromIssuer) {
        this.canGetFromIssuer = canGetFromIssuer;
        return this;
    }

    public IssuerAwarenessContextBuilder<T> getFromIssuer(Function<BukkitCommandIssuer, T> getFromIssuer) {
        this.getFromIssuer = getFromIssuer;
        return this;
    }

    public IssuerAwarenessContextBuilder<T> getFromContext(Function<String, T> getFromContext) {
        this.getFromContext = getFromContext;
        return this;
    }

    public IssuerAwarenessContextBuilder<T> issuerOnlyError(Supplier<InvalidCommandArgument> issuerOnlyError) {
        this.issuerOnlyError = issuerOnlyError;
        return this;
    }

    public IssuerAwarenessContextBuilder<T> issuerAwareError(Function<String, InvalidCommandArgument> issuerAwareError) {
        this.issuerAwareError = issuerAwareError;
        return this;
    }

    public IssuerAwarenessContextBuilder<T> inputOnlyError(Function<String, InvalidCommandArgument> inputOnlyError) {
        this.inputOnlyError = inputOnlyError;
        return this;
    }

    public IssuerAwareContextResolver<T, BukkitCommandExecutionContext> build() {
        Objects.requireNonNull(canGetFromIssuer);
        Objects.requireNonNull(getFromIssuer);
        Objects.requireNonNull(getFromContext);
        Objects.requireNonNull(issuerOnlyError);
        Objects.requireNonNull(issuerAwareError);
        Objects.requireNonNull(inputOnlyError);

        return context -> {
            String resolve = context.getFlagValue("resolve", "");

            if (resolve.equals("issuerOnly")) {
                if (canGetFromIssuer.test(context.getIssuer())) {
                    return getFromIssuer.apply(context.getIssuer());
                }
                if (context.isOptional()) {
                    return null;
                }
                throw issuerOnlyError.get();
            }

            String input = context.getFirstArg();
            T result = getFromContext.apply(input);

            if (resolve.equals("issuerAware")) {
                if (result != null) {
                    context.popFirstArg();
                    return result;
                }
                if (canGetFromIssuer.test(context.getIssuer())) {
                    return getFromIssuer.apply(context.getIssuer());
                }
                if (context.isOptional()) {
                    return null;
                }
                throw issuerAwareError.apply(input);
            }

            if (result != null) {
                context.popFirstArg();
                return result;
            }
            if (context.isOptional()) {
                return null;
            }
            throw inputOnlyError.apply(input);
        };
    }
}
